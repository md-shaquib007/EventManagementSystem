package com.ceoms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventRequest {
    @NotBlank
    private String title;
    private String description;
    private Long categoryId;
    private Long departmentId;
    private String facultyCoordinator;
    private String studentCoordinator;
    private Long venueId;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationHours;
    @Min(1)
    private Integer capacity;
    private String objectives;
    private String requirements;
    private String resources;
    private String equipment;
    private String sponsors;
    private BigDecimal estimatedBudget;
}
