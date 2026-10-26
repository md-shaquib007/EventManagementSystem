package com.ceoms.repository;

import com.ceoms.entity.Event;
import com.ceoms.entity.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    List<Event> findUpcomingEvents(@Param("today") LocalDate today);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.deletedAt IS NULL")
    long countActiveEvents();

    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND (LOWER(e.title) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(e.eventCode) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Event> search(@Param("q") String q);

    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.venue.id = :venueId " +
           "AND e.status NOT IN ('REJECTED', 'ARCHIVED') " +
           "AND (:excludeId IS NULL OR e.id != :excludeId) " +
           "AND e.startDate <= :endDate AND e.endDate >= :startDate")
    List<Event> findVenueConflicts(@Param("venueId") Long venueId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("excludeId") Long excludeId);
}
