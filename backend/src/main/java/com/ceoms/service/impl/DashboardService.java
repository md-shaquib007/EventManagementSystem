package com.ceoms.service.impl;

import com.ceoms.dto.response.DashboardResponse;
import com.ceoms.entity.User;
import com.ceoms.entity.enums.ApprovalStatus;
import com.ceoms.entity.enums.EventStatus;
import com.ceoms.entity.enums.RegistrationStatus;
import com.ceoms.entity.enums.RoleType;
import com.ceoms.repository.*;
import com.ceoms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final BudgetRepository budgetRepository;
    private final BillRepository billRepository;
    private final RegistrationRepository registrationRepository;
    private final EventTaskRepository eventTaskRepository;
    private final NotificationRepository notificationRepository;
    private final ExpenseRepository expenseRepository;
    private final AttendanceRepository attendanceRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public DashboardResponse getAdminDashboard() {
        return DashboardResponse.builder()
                .totalEvents(eventRepository.countActiveEvents())
                .activeEvents(eventRepository.countByStatus(EventStatus.ONGOING))
                .upcomingEvents(eventRepository.findUpcomingEvents(LocalDate.now()).size())
                .completedEvents(eventRepository.countByStatus(EventStatus.COMPLETED))
                .operators(userRepository.findAll().stream()
                        .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName() == RoleType.OPERATOR))
                        .count())
                .departments(departmentRepository.count())
                .registrations(registrationRepository.count())
                .totalBudget(budgetRepository.sumApprovedBudgets())
                .totalExpenses(budgetRepository.sumActualSpending())
                .pendingBills(billRepository.countByApprovalStatus(ApprovalStatus.PENDING))
                .pendingApprovals(eventRepository.countByStatus(EventStatus.SUBMITTED)
                        + eventRepository.countByStatus(EventStatus.UNDER_REVIEW))
                .monthlyEvents(buildMonthlyEvents(null))
                .expenseTrends(buildExpenseTrends())
                .budgetUtilization(buildBudgetUtilization())
                .departmentPerformance(buildDepartmentPerformance())
                .attendanceAnalysis(buildAttendanceAnalysis())
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardResponse getOperatorDashboard() {
        User user = securityUtils.getCurrentUser();
        Long userId = user.getId();
        return DashboardResponse.builder()
                .assignedEvents(eventRepository.countByOrganizerId(userId))
                .todaysEvents(eventRepository.findAll().stream()
                        .filter(e -> e.getOrganizer().getId().equals(userId)
                                && e.getStartDate().equals(LocalDate.now()))
                        .count())
                .pendingTasks(eventRepository.findAll().stream()
                        .filter(e -> e.getOrganizer().getId().equals(userId))
                        .mapToLong(e -> eventTaskRepository.countByEventIdAndCompletedFalse(e.getId()))
                        .sum())
                .registrations(registrationRepository.findAll().stream()
                        .filter(r -> r.getEvent().getOrganizer().getId().equals(userId))
                        .count())
                .totalExpenses(budgetRepository.findAll().stream()
                        .filter(b -> b.getEvent().getOrganizer().getId().equals(userId))
                        .map(b -> b.getActualSpending())
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .uploadedBills(billRepository.findAll().stream()
                        .filter(b -> b.getUploadedBy() != null && b.getUploadedBy().getId().equals(userId))
                        .count())
                .monthlyEvents(buildMonthlyEvents(userId))
                .budgetUtilization(buildBudgetUtilization())
                .attendanceAnalysis(buildAttendanceAnalysis())
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardResponse getStudentDashboard() {
        User user = securityUtils.getCurrentUser();
        return DashboardResponse.builder()
                .registeredEvents(registrationRepository.findByUserIdAndStatus(user.getId(), RegistrationStatus.REGISTERED).size())
                .upcomingEvents(registrationRepository.findByUserIdAndStatus(user.getId(), RegistrationStatus.REGISTERED).stream()
                        .filter(r -> r.getEvent().getStartDate().isAfter(LocalDate.now()))
                        .count())
                .certificates(registrationRepository.findByUserIdAndStatus(user.getId(), RegistrationStatus.REGISTERED).stream()
                        .filter(r -> r.getEvent().getStatus() == EventStatus.COMPLETED)
                        .count())
                .notifications(notificationRepository.countByUserIdAndReadFalse(user.getId()))
                .build();
    }

    private List<Map<String, Object>> buildMonthlyEvents(Long organizerId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            int finalMonth = month;
            long count = eventRepository.findAll().stream()
                    .filter(e -> e.getStartDate() != null && e.getStartDate().getMonthValue() == finalMonth)
                    .filter(e -> organizerId == null || e.getOrganizer().getId().equals(organizerId))
                    .count();
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("count", count);
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> buildExpenseTrends() {
        List<Map<String, Object>> result = new ArrayList<>();
        expenseRepository.sumByCategory().forEach(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("category", row[0].toString());
            item.put("amount", row[1]);
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildBudgetUtilization() {
        List<Map<String, Object>> result = new ArrayList<>();
        budgetRepository.findAll().forEach(b -> {
            Map<String, Object> item = new HashMap<>();
            item.put("event", b.getEvent().getTitle());
            item.put("approved", b.getApprovedAmount());
            item.put("spent", b.getActualSpending());
            item.put("utilization", b.getUtilizationPercentage());
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildDepartmentPerformance() {
        List<Map<String, Object>> result = new ArrayList<>();
        departmentRepository.findAll().forEach(d -> {
            long eventCount = eventRepository.findAll().stream()
                    .filter(e -> e.getDepartment() != null && e.getDepartment().getId().equals(d.getId()))
                    .count();
            Map<String, Object> item = new HashMap<>();
            item.put("department", d.getName());
            item.put("events", eventCount);
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildAttendanceAnalysis() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int month = 1; month <= 6; month++) {
            int finalMonth = month;
            long count = attendanceRepository.findAll().stream()
                    .filter(a -> a.getCheckInTime().getMonthValue() == finalMonth)
                    .count();
            Map<String, Object> item = new HashMap<>();
            item.put("month", finalMonth);
            item.put("count", count);
            result.add(item);
        }
        return result;
    }
}
