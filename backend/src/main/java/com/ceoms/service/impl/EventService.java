package com.ceoms.service.impl;

import com.ceoms.audit.AuditService;
import com.ceoms.dto.request.ApprovalRequest;
import com.ceoms.dto.request.EventRequest;
import com.ceoms.dto.response.EventResponse;
import com.ceoms.dto.response.PageResponse;
import com.ceoms.entity.*;
import com.ceoms.entity.enums.EventStatus;
import com.ceoms.entity.enums.NotificationType;
import com.ceoms.entity.enums.RoleType;
import com.ceoms.exception.BadRequestException;
import com.ceoms.exception.ForbiddenException;
import com.ceoms.exception.ResourceNotFoundException;
import com.ceoms.mapper.EventMapper;
import com.ceoms.notification.NotificationService;
import com.ceoms.repository.*;
import com.ceoms.security.SecurityUtils;
import com.ceoms.specification.EventSpecification;
import com.ceoms.storage.FileStorageService;
import com.ceoms.util.CodeGenerator;
import com.ceoms.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final VenueRepository venueRepository;
    private final BudgetRepository budgetRepository;
    private final EventMapper eventMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> getAll(EventStatus status, Long departmentId, Long categoryId,
                                               String search, LocalDate from, LocalDate to, Pageable pageable) {
        Specification<Event> spec = Specification.where(EventSpecification.notDeleted())
                .and(EventSpecification.hasStatus(status))
                .and(EventSpecification.hasDepartment(departmentId))
                .and(EventSpecification.hasCategory(categoryId))
                .and(EventSpecification.titleContains(search))
                .and(EventSpecification.startDateFrom(from))
                .and(EventSpecification.startDateTo(to));

        Specification<Event> finalSpec = spec;
        var currentUserOpt = securityUtils.getCurrentUserOptional();
        if (currentUserOpt.isPresent()) {
            User currentUser = currentUserOpt.get();
            boolean isOperator = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getName() == RoleType.OPERATOR);
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getName() == RoleType.SUPER_ADMIN);
            if (isOperator && !isAdmin) {
                finalSpec = finalSpec.and(EventSpecification.hasOrganizer(currentUser.getId()));
            }
        }

        Page<EventResponse> page = eventRepository.findAll(finalSpec, pageable).map(eventMapper::toResponse);
        return PageUtil.toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public EventResponse getById(Long id) {
        return eventMapper.toResponse(findEvent(id));
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        User organizer = securityUtils.getCurrentUser();
        Event event = mapRequestToEvent(new Event(), request);
        event.setEventCode(CodeGenerator.generateEventCode());
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.DRAFT);
        event = eventRepository.save(event);

        Budget budget = Budget.builder()
                .event(event)
                .estimatedAmount(request.getEstimatedBudget() != null ? request.getEstimatedBudget() : BigDecimal.ZERO)
                .build();
        budgetRepository.save(budget);
        event.setBudget(budget);

        auditService.log(organizer, "EVENT_CREATED", "Event", event.getId(), null, event.getTitle());
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event event = findEvent(id);
        validateEventOwnership(event);
        if (event.getStatus() != EventStatus.DRAFT && event.getStatus() != EventStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected events can be edited");
        }
        mapRequestToEvent(event, request);
        event = eventRepository.save(event);
        if (event.getBudget() != null && request.getEstimatedBudget() != null) {
            event.getBudget().setEstimatedAmount(request.getEstimatedBudget());
            budgetRepository.save(event.getBudget());
        }
        auditService.log(securityUtils.getCurrentUser(), "EVENT_UPDATED", "Event", id, null, event.getTitle());
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse submit(Long id) {
        Event event = findEvent(id);
        validateEventOwnership(event);
        if (event.getStatus() != EventStatus.DRAFT && event.getStatus() != EventStatus.REJECTED) {
            throw new BadRequestException("Event cannot be submitted in current status");
        }
        event.setStatus(EventStatus.SUBMITTED);
        eventRepository.save(event);
        auditService.log(securityUtils.getCurrentUser(), "EVENT_SUBMITTED", "Event", id, null, null);
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse approve(Long id, ApprovalRequest request) {
        Event event = findEvent(id);
        event.setStatus(EventStatus.APPROVED);
        event.setApprovalComment(request.getComment());
        if (event.getBudget() != null) {
            event.getBudget().setApprovedAmount(event.getBudget().getEstimatedAmount());
            event.getBudget().setApproved(true);
            budgetRepository.save(event.getBudget());
        }
        eventRepository.save(event);
        notificationService.send(event.getOrganizer(), "Event Approved",
                "Your event '" + event.getTitle() + "' has been approved.",
                NotificationType.EVENT_APPROVED, id);
        auditService.log(securityUtils.getCurrentUser(), "EVENT_APPROVED", "Event", id, null, null);
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse reject(Long id, ApprovalRequest request) {
        Event event = findEvent(id);
        event.setStatus(EventStatus.REJECTED);
        event.setApprovalComment(request.getComment());
        eventRepository.save(event);
        notificationService.send(event.getOrganizer(), "Event Rejected",
                "Your event '" + event.getTitle() + "' was rejected. Comment: " + request.getComment(),
                NotificationType.EVENT_REJECTED, id);
        auditService.log(securityUtils.getCurrentUser(), "EVENT_REJECTED", "Event", id, null, request.getComment());
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse startEvent(Long id) {
        Event event = findEvent(id);
        if (event.getStatus() != EventStatus.APPROVED) {
            throw new BadRequestException("Only approved events can be started");
        }
        event.setStatus(EventStatus.ONGOING);
        eventRepository.save(event);
        auditService.log(securityUtils.getCurrentUser(), "EVENT_STARTED", "Event", id, null, null);
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse completeEvent(Long id) {
        Event event = findEvent(id);
        validateEventOwnership(event);
        event.setStatus(EventStatus.COMPLETED);
        eventRepository.save(event);
        notificationService.send(event.getOrganizer(), "Event Completed",
                "Event '" + event.getTitle() + "' has been marked as completed.",
                NotificationType.EVENT_COMPLETED, id);
        auditService.log(securityUtils.getCurrentUser(), "EVENT_COMPLETED", "Event", id, null, null);
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse archive(Long id) {
        Event event = findEvent(id);
        event.setStatus(EventStatus.ARCHIVED);
        event.setDeletedAt(LocalDateTime.now());
        eventRepository.save(event);
        auditService.log(securityUtils.getCurrentUser(), "EVENT_ARCHIVED", "Event", id, null, null);
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse restore(Long id) {
        Event event = findEvent(id);
        event.setStatus(EventStatus.COMPLETED);
        event.setDeletedAt(null);
        eventRepository.save(event);
        auditService.log(securityUtils.getCurrentUser(), "EVENT_RESTORED", "Event", id, null, null);
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse uploadBanner(Long id, MultipartFile file) {
        Event event = findEvent(id);
        validateEventOwnership(event);
        String path = fileStorageService.storeFile(file, "banners");
        event.setBannerPath(path);
        eventRepository.save(event);
        auditService.log(securityUtils.getCurrentUser(), "BANNER_UPLOADED", "Event", id, null, path);
        return eventMapper.toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> search(String query) {
        if (query == null || query.isBlank()) return List.of();
        return eventRepository.search(query).stream().limit(50).map(eventMapper::toResponse).toList();
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    private void validateEventOwnership(Event event) {
        User current = securityUtils.getCurrentUser();
        boolean isAdmin = current.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleType.SUPER_ADMIN);
        if (!isAdmin && !event.getOrganizer().getId().equals(current.getId())) {
            throw new ForbiddenException("You can only manage your own events");
        }
    }

    private Event mapRequestToEvent(Event event, EventRequest request) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setFacultyCoordinator(request.getFacultyCoordinator());
        event.setStudentCoordinator(request.getStudentCoordinator());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setDurationHours(request.getDurationHours());
        if (request.getCapacity() != null) event.setCapacity(request.getCapacity());
        event.setObjectives(request.getObjectives());
        event.setRequirements(request.getRequirements());
        event.setResources(request.getResources());
        event.setEquipment(request.getEquipment());
        event.setSponsors(request.getSponsors());

        if (request.getCategoryId() != null) {
            event.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }
        if (request.getDepartmentId() != null) {
            event.setDepartment(departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        }
        if (request.getVenueId() != null) {
            event.setVenue(venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue not found")));
        }
        return event;
    }
}
