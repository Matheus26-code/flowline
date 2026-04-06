package com.flowline.flowline.repository;

import com.flowline.flowline.model.MovementOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<MovementOrder, Long> {
}
