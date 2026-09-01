package com.ceoms.service.impl;

import com.ceoms.audit.AuditService;
import com.ceoms.entity.*;
import com.ceoms.entity.enums.NotificationType;
import com.ceoms.entity.enums.RegistrationStatus;
import com.ceoms.exception.BadRequestException;
import com.ceoms.exception.ResourceNotFoundException;
import com.ceoms.notification.NotificationService;
import com.ceoms.repository.EventRepository;
import com.ceoms.repository.RegistrationRepository;
import com.ceoms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    public Registration register(Long eventId) {
        User user = securityUtils.getCurrentUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (registrationRepository.findByEventIdAndUserId(eventId, user.getId()).isPresent()) {
            throw new BadRequestException("Already registered for this event");
        }
        long count = registrationRepository.countByEventId(eventId);
        if (count >= event.getCapacity()) {
            throw new BadRequestException("Event is at full capacity");
        }
        Registration reg = Registration.builder().event(event).user(user).status(RegistrationStatus.REGISTERED).build();
        reg = registrationRepository.save(reg);
        notificationService.send(user, "Registration Confirmed",
                "You are registered for " + event.getTitle(), NotificationType.REGISTRATION_CONFIRMED, eventId);
        auditService.log(user, "EVENT_REGISTERED", "Registration", reg.getId(), null, event.getTitle());
        return reg;
    }

    @Transactional
    public void cancel(Long id) {
        Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        if (!reg.getUser().getId().equals(securityUtils.getCurrentUserId())) {
            throw new BadRequestException("Cannot cancel another user's registration");
        }
        reg.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(reg);
    }

    @Transactional(readOnly = true)
    public List<Registration> getMyRegistrations() {
        return registrationRepository.findByUserIdAndStatus(securityUtils.getCurrentUserId(), RegistrationStatus.REGISTERED);
    }

    @Transactional(readOnly = true)
    public List<Registration> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }
}
