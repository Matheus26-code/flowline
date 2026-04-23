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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
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
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found"));
        Sector destinationSector = sectorRepository.findById(request.destinationSectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
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
        OrderDependencies deps = resolveDependencies(request);

        MovementOrder order = new MovementOrder();
        order.setOriginSector(deps.originSector);
        order.setDestinationSector(deps.destinationSector);
        order.setUser(deps.user);
        order.setProduct(deps.product);
        order.setStatus(MovementStatus.PENDING);
        order.setQuantity(request.quantity());
        order.setCreatedAt(LocalDateTime.now());
        return toResponseDTO(orderRepository.save(order));
    }

    public OrderResponseDTO findOrderById(Long id) {
        MovementOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toResponseDTO(order);
    }

    public PageResponseDTO<OrderResponseDTO> findAllOrders(Pageable pageable) {
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
        OrderDependencies deps = resolveDependencies(request);
        MovementOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setOriginSector(deps.originSector);
        order.setDestinationSector(deps.destinationSector);
        order.setUser(deps.user);
        order.setProduct(deps.product);
        order.setStatus(MovementStatus.PENDING);
        order.setQuantity(request.quantity());
        return toResponseDTO(orderRepository.save(order));
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
}