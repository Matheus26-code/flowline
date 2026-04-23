package com.flowline.flowline.service;

import com.flowline.flowline.dto.OrderRequestDTO;
import com.flowline.flowline.dto.OrderResponseDTO;
import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.*;
import com.flowline.flowline.repository.OrderRepository;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.SectorRepository;
import com.flowline.flowline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementOrderService {

    private final OrderRepository orderRepository;
    private final SectorRepository sectorRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private record OrderDependencies(
            Sector originSector,
            Sector destinationSector,
            User user,
            Product product
    ) {}

    private OrderDependencies resolveDependencies(OrderRequestDTO request) {
        Sector originSector = sectorRepository.findById(request.originSectorId())
                .orElseThrow(() -> {
                    log.warn("Origin Sector not found with id: {}", request.originSectorId());
                    return new ResourceNotFoundException("Sector not found");
                });
        Sector destinationSector = sectorRepository.findById(request.destinationSectorId())
                .orElseThrow(() -> {
                    log.warn("Destination Sector not found with id: {}", request.originSectorId());
                    return new ResourceNotFoundException("Sector not found");
                });
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", request.userId());
                    return new ResourceNotFoundException("User not found");
                });
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", request.productId());
                    return new ResourceNotFoundException("Product not found");
                });

        return new OrderDependencies(originSector, destinationSector, user, product);
    }

    private OrderResponseDTO toResponseDTO(MovementOrder order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOriginSector().getId(),
                order.getDestinationSector().getId(),
                order.getUser().getId(),
                order.getProduct().getId(),
                order.getStatus(),
                order.getQuantity(),
                order.getCreatedAt());
    }

    public OrderResponseDTO createOrder (OrderRequestDTO request) {
        log.info("Creating a new order: {}", request);
        OrderDependencies deps = resolveDependencies(request);
        MovementOrder order = new MovementOrder();
        order.setOriginSector(deps.originSector);
        order.setDestinationSector(deps.destinationSector);
        order.setUser(deps.user);
        order.setProduct(deps.product);
        order.setStatus(MovementStatus.PENDING);
        order.setQuantity(request.quantity());
        order.setCreatedAt(LocalDateTime.now());
        OrderResponseDTO result = toResponseDTO(orderRepository.save(order));
        log.info("Order created successfully: id={}",
                result.id());
        return result;
    }

    public OrderResponseDTO findOrderById(Long id) {
        log.info("Finding order by id: {}", id);
        MovementOrder order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found with id: {}", id);
                    return new ResourceNotFoundException("Order not found with id: " + id);
                });
        OrderResponseDTO result = toResponseDTO(order);
        log.info("Order find successfully: id={}",
                result.id());
        return result;
    }

    public PageResponseDTO<OrderResponseDTO> findAllOrders(Pageable pageable) {
        log.info("Finding all orders by page: {}", pageable);
        Page<OrderResponseDTO> page = orderRepository.findAll(pageable)
                .map(this::toResponseDTO);
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO request) {
        log.info("Updating order by id: {}", id);
        OrderDependencies deps = resolveDependencies(request);
        MovementOrder order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found with id: {}", id);
                    return new ResourceNotFoundException("Order not found with id: " + id);
                });

        order.setOriginSector(deps.originSector);
        order.setDestinationSector(deps.destinationSector);
        order.setUser(deps.user);
        order.setProduct(deps.product);
        order.setStatus(MovementStatus.PENDING);
        order.setQuantity(request.quantity());
        OrderResponseDTO result = toResponseDTO(orderRepository.save(order));
        log.info("Order updated successfully: id={}",
                result.id());
        return result;
    }

    public void deleteOrderById(Long id) {
        log.info("Deleting order by id: {}", id);
        orderRepository.deleteById(id);
        log.info("Order deleted successfully: id={}", id);
    }
}