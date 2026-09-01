package com.ceoms.service.impl;

import com.ceoms.audit.AuditService;
import com.ceoms.entity.Attendance;
import com.ceoms.entity.Event;
import com.ceoms.entity.User;
import com.ceoms.entity.enums.AttendanceType;
import com.ceoms.entity.enums.CheckInMethod;
import com.ceoms.exception.ResourceNotFoundException;
import com.ceoms.repository.AttendanceRepository;
import com.ceoms.repository.EventRepository;
import com.ceoms.repository.UserRepository;
import com.ceoms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Transactional
    public Attendance checkIn(Long eventId, Long userId, String attendeeName,
                              AttendanceType type, CheckInMethod method) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User attendee = null;
        if (userId != null) {
            attendee = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } else {
            attendee = securityUtils.getCurrentUser();
        }
        Attendance attendance = Attendance.builder()
                .event(event)
                .user(attendee)
                .attendeeName(attendeeName)
                .attendanceType(type)
                .checkInMethod(method)
                .checkInTime(LocalDateTime.now())
                .build();
        attendance = attendanceRepository.save(attendance);
        auditService.log(securityUtils.getCurrentUser(), "ATTENDANCE_MARKED", "Attendance", attendance.getId(), null, event.getTitle());
        return attendance;
    }

    @Transactional(readOnly = true)
    public List<Attendance> getEventAttendance(Long eventId) {
        return attendanceRepository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getMyAttendance() {
        return attendanceRepository.findByUserId(securityUtils.getCurrentUserId());
    }
}
