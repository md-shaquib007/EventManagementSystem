package com.ceoms.controller;

import com.ceoms.dto.response.ApiResponse;
import com.ceoms.entity.Department;
import com.ceoms.exception.ResourceNotFoundException;
import com.ceoms.repository.DepartmentRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@Tag(name = "Departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public ApiResponse<List<Department>> getAll() {
        return ApiResponse.success(departmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Department> getById(@PathVariable Long id) {
        return ApiResponse.success(departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Department> create(@Valid @RequestBody DepartmentRequest request) {
        Department dept = Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .headName(request.getHeadName())
                .build();
        return ApiResponse.success("Department created", departmentRepository.save(dept));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Department> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        dept.setName(request.getName());
        dept.setCode(request.getCode());
        dept.setDescription(request.getDescription());
        dept.setHeadName(request.getHeadName());
        return ApiResponse.success("Department updated", departmentRepository.save(dept));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        departmentRepository.deleteById(id);
        return ApiResponse.success("Department deleted", null);
    }

    @Data
    public static class DepartmentRequest {
        @NotBlank private String name;
        @NotBlank private String code;
        private String description;
        private String headName;
    }
}
