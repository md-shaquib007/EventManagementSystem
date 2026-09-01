package com.ceoms.mapper;

import com.ceoms.dto.response.UserResponse;
import com.ceoms.entity.User;
import com.ceoms.entity.enums.RoleType;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                .active(user.getActive())
                .emailVerified(user.getEmailVerified())
                .build();
    }
}
