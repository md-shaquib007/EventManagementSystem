package com.ceoms.repository;

import com.ceoms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEventId(Long eventId);
    List<Attendance> findByUserId(Long userId);
    Optional<Attendance> findByEventIdAndUserId(Long eventId, Long userId);
    long countByEventId(Long eventId);
}
