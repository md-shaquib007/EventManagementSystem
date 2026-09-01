package com.ceoms.controller;

import com.ceoms.dto.response.ApiResponse;
import com.ceoms.dto.response.DashboardResponse;
import com.ceoms.service.impl.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<DashboardResponse> adminDashboard() {
        return ApiResponse.success(dashboardService.getAdminDashboard());
    }

    @GetMapping("/operator")
    @PreAuthorize("hasRole('OPERATOR')")
    public ApiResponse<DashboardResponse> operatorDashboard() {
        return ApiResponse.success(dashboardService.getOperatorDashboard());
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<DashboardResponse> studentDashboard() {
        return ApiResponse.success(dashboardService.getStudentDashboard());
    }
}
