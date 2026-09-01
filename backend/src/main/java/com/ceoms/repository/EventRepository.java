package com.ceoms.repository;

import com.ceoms.entity.Event;
import com.ceoms.entity.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByEventCode(String eventCode);
    long countByStatus(EventStatus status);
    long countByOrganizerId(Long organizerId);
    long countByOrganizerIdAndStatus(Long organizerId, EventStatus status);

    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.status NOT IN ('ARCHIVED') AND e.startDate >= :today ORDER BY e.startDate")
    List<Event> findUpcomingEvents(LocalDate today);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.deletedAt IS NULL")
    long countActiveEvents();

    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND (LOWER(e.title) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(e.eventCode) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Event> search(String q);
}
