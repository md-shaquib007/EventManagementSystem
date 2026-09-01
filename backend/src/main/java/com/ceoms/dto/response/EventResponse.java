package com.ceoms.dto.response;

import com.ceoms.entity.enums.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class EventResponse {
    private Long id;
    private String eventCode;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long departmentId;
    private String departmentName;
    private Long organizerId;
    private String organizerName;
    private String facultyCoordinator;
    private String studentCoordinator;
    private Long venueId;
    private String venueName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationHours;
    private Integer capacity;
    private String objectives;
    private String requirements;
    private String resources;
    private String equipment;
    private String sponsors;
    private EventStatus status;
    private String bannerPath;
    private String approvalComment;
    private String eventHighlights;
    private BigDecimal estimatedBudget;
    private BigDecimal approvedBudget;
    private BigDecimal actualSpending;
    private BigDecimal remainingBudget;
    private BigDecimal budgetUtilization;
    private long registrationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
