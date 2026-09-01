package com.ceoms.controller;

import com.ceoms.dto.request.ApprovalRequest;
import com.ceoms.dto.request.EventRequest;
import com.ceoms.dto.response.ApiResponse;
import com.ceoms.dto.response.EventResponse;
import com.ceoms.dto.response.PageResponse;
import com.ceoms.entity.enums.EventStatus;
import com.ceoms.service.impl.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "List events with filters")
    public ApiResponse<PageResponse<EventResponse>> list(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ApiResponse.success(eventService.getAll(status, departmentId, categoryId, search, from, to,
                PageRequest.of(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(eventService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ApiResponse.success("Event created", eventService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<EventResponse> update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return ApiResponse.success("Event updated", eventService.update(id, request));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<EventResponse> submit(@PathVariable Long id) {
        return ApiResponse.success("Event submitted for review", eventService.submit(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<EventResponse> approve(@PathVariable Long id, @RequestBody ApprovalRequest request) {
        return ApiResponse.success("Event approved", eventService.approve(id, request));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<EventResponse> reject(@PathVariable Long id, @RequestBody ApprovalRequest request) {
        return ApiResponse.success("Event rejected", eventService.reject(id, request));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<EventResponse> start(@PathVariable Long id) {
        return ApiResponse.success("Event started", eventService.startEvent(id));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<EventResponse> complete(@PathVariable Long id) {
        return ApiResponse.success("Event completed", eventService.completeEvent(id));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<EventResponse> archive(@PathVariable Long id) {
        return ApiResponse.success("Event archived", eventService.archive(id));
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<EventResponse> restore(@PathVariable Long id) {
        return ApiResponse.success("Event restored", eventService.restore(id));
    }

    @PostMapping(value = "/{id}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<EventResponse> uploadBanner(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ApiResponse.success("Banner uploaded", eventService.uploadBanner(id, file));
    }

    @GetMapping("/search")
    public ApiResponse<List<EventResponse>> search(@RequestParam String q) {
        return ApiResponse.success(eventService.search(q));
    }
}
