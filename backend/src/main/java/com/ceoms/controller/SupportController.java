package com.ceoms.controller;

import com.ceoms.dto.response.ApiResponse;
import com.ceoms.dto.response.PageResponse;
import com.ceoms.entity.Announcement;
import com.ceoms.entity.AuditLog;
import com.ceoms.entity.Notification;
import com.ceoms.repository.AnnouncementRepository;
import com.ceoms.repository.AuditLogRepository;
import com.ceoms.repository.NotificationRepository;
import com.ceoms.security.SecurityUtils;
import com.ceoms.util.PageUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Support")
public class SupportController {

    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final AnnouncementRepository announcementRepository;
    private final SecurityUtils securityUtils;

    @GetMapping("/notifications")
    public ApiResponse<PageResponse<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = notificationRepository.findByUserIdOrderByCreatedAtDesc(
                securityUtils.getCurrentUserId(), PageRequest.of(page, size));
        return ApiResponse.success(PageUtil.toPageResponse(result));
    }

    @PutMapping("/notifications/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        n.setRead(true);
        notificationRepository.save(n);
        return ApiResponse.success(null);
    }

    @PutMapping("/notifications/read-all")
    public ApiResponse<Void> markAllRead() {
        notificationRepository.findByUserIdOrderByCreatedAtDesc(
                securityUtils.getCurrentUserId(), PageRequest.of(0, 1000))
                .forEach(n -> { n.setRead(true); notificationRepository.save(n); });
        return ApiResponse.success(null);
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<PageResponse<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = auditLogRepository.findAll(PageRequest.of(page, size, Sort.by("timestamp").descending()));
        return ApiResponse.success(PageUtil.toPageResponse(result));
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> getAnnouncements() {
        return ApiResponse.success(announcementRepository.findByActiveTrueOrderByCreatedAtDesc());
    }
}
