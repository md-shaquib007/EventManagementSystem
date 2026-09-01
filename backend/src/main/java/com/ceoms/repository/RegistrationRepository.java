package com.ceoms.repository;

import com.ceoms.entity.Registration;
import com.ceoms.entity.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByUserIdAndStatus(Long userId, RegistrationStatus status);
    Optional<Registration> findByEventIdAndUserId(Long eventId, Long userId);
    long countByEventId(Long eventId);
}
