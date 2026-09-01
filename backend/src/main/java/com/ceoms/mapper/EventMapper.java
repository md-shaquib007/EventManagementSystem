package com.ceoms.mapper;

import com.ceoms.dto.response.EventResponse;
import com.ceoms.entity.Event;
import com.ceoms.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final RegistrationRepository registrationRepository;

    public EventResponse toResponse(Event event) {
        BigDecimal estimated = BigDecimal.ZERO;
        BigDecimal approved = BigDecimal.ZERO;
        BigDecimal actual = BigDecimal.ZERO;
        BigDecimal remaining = BigDecimal.ZERO;
        BigDecimal utilization = BigDecimal.ZERO;

        if (event.getBudget() != null) {
            estimated = event.getBudget().getEstimatedAmount();
            approved = event.getBudget().getApprovedAmount();
            actual = event.getBudget().getActualSpending();
            remaining = event.getBudget().getRemainingBudget();
            utilization = event.getBudget().getUtilizationPercentage();
        }

        return EventResponse.builder()
                .id(event.getId())
                .eventCode(event.getEventCode())
                .title(event.getTitle())
                .description(event.getDescription())
                .categoryId(event.getCategory() != null ? event.getCategory().getId() : null)
                .categoryName(event.getCategory() != null ? event.getCategory().getName() : null)
                .departmentId(event.getDepartment() != null ? event.getDepartment().getId() : null)
                .departmentName(event.getDepartment() != null ? event.getDepartment().getName() : null)
                .organizerId(event.getOrganizer().getId())
                .organizerName(event.getOrganizer().getFullName())
                .facultyCoordinator(event.getFacultyCoordinator())
                .studentCoordinator(event.getStudentCoordinator())
                .venueId(event.getVenue() != null ? event.getVenue().getId() : null)
                .venueName(event.getVenue() != null ? event.getVenue().getName() : null)
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .durationHours(event.getDurationHours())
                .capacity(event.getCapacity())
                .objectives(event.getObjectives())
                .requirements(event.getRequirements())
                .resources(event.getResources())
                .equipment(event.getEquipment())
                .sponsors(event.getSponsors())
                .status(event.getStatus())
                .bannerPath(event.getBannerPath())
                .approvalComment(event.getApprovalComment())
                .eventHighlights(event.getEventHighlights())
                .estimatedBudget(estimated)
                .approvedBudget(approved)
                .actualSpending(actual)
                .remainingBudget(remaining)
                .budgetUtilization(utilization)
                .registrationCount(registrationRepository.countByEventId(event.getId()))
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
