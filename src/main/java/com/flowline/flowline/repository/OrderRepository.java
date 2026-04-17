package com.flowline.flowline.repository;

import com.flowline.flowline.model.MovementOrder;
import com.flowline.flowline.model.MovementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<MovementOrder, Long> {

    Long countByStatus(MovementStatus status);
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Long countByStatusAndCreatedAtBetween(
            MovementStatus status,
            LocalDateTime start,
            LocalDateTime end);
}
