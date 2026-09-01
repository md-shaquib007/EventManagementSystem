package com.ceoms.repository;

import com.ceoms.entity.EventGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventGalleryRepository extends JpaRepository<EventGallery, Long> {
    List<EventGallery> findByEventId(Long eventId);
}
