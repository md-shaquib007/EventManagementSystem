package com.ceoms.repository;

import com.ceoms.entity.EventTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
    List<EventTask> findByEventId(Long eventId);
    long countByEventIdAndCompletedFalse(Long eventId);
}
