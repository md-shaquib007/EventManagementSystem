package com.ceoms.dto.response;

import com.ceoms.entity.enums.RoleType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private Set<RoleType> roles;
    private Boolean active;
    private Boolean emailVerified;
}
