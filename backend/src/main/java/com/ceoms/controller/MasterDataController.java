package com.ceoms.controller;

import com.ceoms.dto.response.ApiResponse;
import com.ceoms.entity.EventCategory;
import com.ceoms.entity.Venue;
import com.ceoms.exception.ResourceNotFoundException;
import com.ceoms.repository.EventCategoryRepository;
import com.ceoms.repository.VenueRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Master Data")
public class MasterDataController {

    private final EventCategoryRepository categoryRepository;
    private final VenueRepository venueRepository;

    @GetMapping("/categories")
    public ApiResponse<List<EventCategory>> getCategories() {
        return ApiResponse.success(categoryRepository.findAll());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<EventCategory> createCategory(@RequestBody NameRequest request) {
        EventCategory cat = EventCategory.builder().name(request.getName()).description(request.getDescription()).build();
        return ApiResponse.success(categoryRepository.save(cat));
    }

    @GetMapping("/venues")
    public ApiResponse<List<Venue>> getVenues() {
        return ApiResponse.success(venueRepository.findAll());
    }

    @PostMapping("/venues")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Venue> createVenue(@RequestBody VenueRequest request) {
        Venue venue = Venue.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .facilities(request.getFacilities())
                .build();
        return ApiResponse.success(venueRepository.save(venue));
    }

    @Data
    public static class NameRequest {
        @NotBlank private String name;
        private String description;
    }

    @Data
    public static class VenueRequest {
        @NotBlank private String name;
        private Integer capacity;
        private String location;
        private String facilities;
    }
}
