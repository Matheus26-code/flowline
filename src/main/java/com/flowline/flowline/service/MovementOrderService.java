package com.flowline.flowline.service;

import com.flowline.flowline.dto.OrderRequestDTO;
import com.flowline.flowline.dto.OrderResponseDTO;
import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.model.*;
import com.flowline.flowline.repository.OrderRepository;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.SectorRepository;
import com.flowline.flowline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovementOrderService {

    private final OrderRepository orderRepository;
    private final SectorRepository sectorRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderResponseDTO createOrder (OrderRequestDTO request) {
        MovementOrder order = new MovementOrder();
        Sector originSector = sectorRepository.findById(request.originSectorId())
                .orElseThrow(() -> new RuntimeException("Origin sector not found with id: " + request.originSectorId()));
        Sector destinationSector = sectorRepository.findById(request.destinationSectorId())
                .orElseThrow(() -> new RuntimeException("Destination sector not found with id: " + request.destinationSectorId()));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.userId()));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.productId()));

        order.setOriginSector(originSector);
        order.setDestinationSector(destinationSector);
        order.setUser(user);
        order.setProduct(product);
        order.setStatus(MovementStatus.PENDING);
        order.setQuantity(request.quantity());
        order.setCreatedAt(request.createdAt());
        MovementOrder savedOrder = orderRepository.save(order);
        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getOriginSector().getId(),
                savedOrder.getDestinationSector().getId(),
                savedOrder.getUser().getId(),
                savedOrder.getProduct().getId(),
                savedOrder.getStatus(),
                savedOrder.getQuantity(),
                savedOrder.getCreatedAt());
    }

    public OrderResponseDTO findOrderById(Long id) {
        MovementOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
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

    public PageResponseDTO<OrderResponseDTO> findAllOrders(Pageable pageable) {
        Page<OrderResponseDTO> page = orderRepository.findAll(pageable)
                .map(order -> new OrderResponseDTO(
                        order.getId(),
                        order.getOriginSector().getId(),
                        order.getDestinationSector().getId(),
                        order.getUser().getId(),
                        order.getProduct().getId(),
                        order.getStatus(),
                        order.getQuantity(),
                        order.getCreatedAt()));
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO request) {
        MovementOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        Sector originSector = sectorRepository.findById(request.originSectorId())
                .orElseThrow(() -> new RuntimeException("Origin sector not found with id: " + request.originSectorId()));
        Sector destinationSector = sectorRepository.findById(request.destinationSectorId())
                .orElseThrow(() -> new RuntimeException("Destination sector not found with id: " + request.destinationSectorId()));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.userId()));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.productId()));

        order.setOriginSector(originSector);
        order.setDestinationSector(destinationSector);
        order.setUser(user);
        order.setProduct(product);
        order.setStatus(request.status());
        order.setQuantity(request.quantity());
        MovementOrder savedOrder = orderRepository.save(order);
        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getOriginSector().getId(),
                savedOrder.getDestinationSector().getId(),
                savedOrder.getUser().getId(),
                savedOrder.getProduct().getId(),
                savedOrder.getStatus(),
                savedOrder.getQuantity(),
                savedOrder.getCreatedAt());
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
}
