package com.ceoms.specification;

import com.ceoms.entity.Event;
import com.ceoms.entity.enums.EventStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class EventSpecification {

    private EventSpecification() {}

    public static Specification<Event> hasStatus(EventStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Event> hasDepartment(Long departmentId) {
        return (root, query, cb) -> departmentId == null ? null : cb.equal(root.get("department").get("id"), departmentId);
    }

    public static Specification<Event> hasCategory(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Event> hasOrganizer(Long organizerId) {
        return (root, query, cb) -> organizerId == null ? null : cb.equal(root.get("organizer").get("id"), organizerId);
    }

    public static Specification<Event> titleContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%");
        };
    }

    public static Specification<Event> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Event> startDateFrom(LocalDate from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("startDate"), from);
    }

    public static Specification<Event> startDateTo(LocalDate to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("startDate"), to);
    }
}
