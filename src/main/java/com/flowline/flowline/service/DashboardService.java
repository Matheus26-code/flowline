package com.flowline.flowline.service;

import com.flowline.flowline.dto.DashboardResponseDTO;
import com.flowline.flowline.dto.OrderSummaryDTO;
import com.flowline.flowline.model.MovementStatus;
import com.flowline.flowline.repository.OrderRepository;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

        public DashboardResponseDTO getDashboard () {
            LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime end = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);

            OrderSummaryDTO orders = new OrderSummaryDTO(
                    orderRepository.count(),
                    orderRepository.countByStatus(MovementStatus.PENDING),
                    orderRepository.countByStatus(MovementStatus.DELIVERING),
                    orderRepository.countByStatus(MovementStatus.DELIVERED),
                    orderRepository.countByStatus(MovementStatus.CANCELLED),
                    orderRepository.countByCreatedAtBetween(start, end),
                    orderRepository.countByStatusAndCreatedAtBetween(MovementStatus.DELIVERED, start, end));

            return new DashboardResponseDTO(
                    orders,
                    productRepository.count(),
                    userRepository.count()
            );
        }
}
