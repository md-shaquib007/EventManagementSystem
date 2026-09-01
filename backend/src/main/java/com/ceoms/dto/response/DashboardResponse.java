package com.ceoms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private long totalEvents;
    private long activeEvents;
    private long upcomingEvents;
    private long completedEvents;
    private long operators;
    private long departments;
    private long registrations;
    private BigDecimal totalBudget;
    private BigDecimal totalExpenses;
    private long pendingBills;
    private long pendingApprovals;
    private long assignedEvents;
    private long todaysEvents;
    private long pendingTasks;
    private long uploadedBills;
    private long registeredEvents;
    private long certificates;
    private long notifications;
    private List<Map<String, Object>> monthlyEvents;
    private List<Map<String, Object>> expenseTrends;
    private List<Map<String, Object>> budgetUtilization;
    private List<Map<String, Object>> departmentPerformance;
    private List<Map<String, Object>> attendanceAnalysis;
}
