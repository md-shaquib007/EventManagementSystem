package com.ceoms.security;

import com.ceoms.entity.User;
import com.ceoms.exception.ResourceNotFoundException;
import com.ceoms.repository.UserRepository;
import com.ceoms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public Optional<User> getCurrentUserOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return Optional.empty();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email);
    }

    public User getCurrentUser() {
        return getCurrentUserOptional()
                .orElseThrow(() -> new ResourceNotFoundException("Not authenticated"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
