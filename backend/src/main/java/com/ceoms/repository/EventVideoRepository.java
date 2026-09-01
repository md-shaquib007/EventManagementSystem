package com.ceoms.repository;

import com.ceoms.entity.EventVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventVideoRepository extends JpaRepository<EventVideo, Long> {
    List<EventVideo> findByEventId(Long eventId);
}
