package com.flowline.flowline.controller;

import com.flowline.flowline.dto.*;
import com.flowline.flowline.service.MovementOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class MovementOrderController {

    private final MovementOrderService movementOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR', 'ASSISTANT')")
    public ResponseEntity<OrderResponseDTO> createProduct(@RequestBody @Valid OrderRequestDTO request) {
        OrderResponseDTO result = movementOrderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR', 'ASSISTANT')")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO result = movementOrderService.findOrderById(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR', 'ASSISTANT')")
    public ResponseEntity<PageResponseDTO<OrderResponseDTO>> getAllOrders(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponseDTO<OrderResponseDTO> result = movementOrderService.findAllOrders(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id, @RequestBody @Valid OrderRequestDTO request) {
        OrderResponseDTO result = movementOrderService.updateOrder(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        movementOrderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}