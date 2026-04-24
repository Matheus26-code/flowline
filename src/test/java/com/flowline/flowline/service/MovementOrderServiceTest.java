package com.flowline.flowline.service;

import com.flowline.flowline.dto.OrderRequestDTO;
import com.flowline.flowline.dto.OrderResponseDTO;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.*;
import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.repository.OrderRepository;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.SectorRepository;
import com.flowline.flowline.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovementOrderServiceTest {

    private Sector sector;
    private User user;
    private Product product;
    private MovementOrder movementOrder;
    private OrderRequestDTO orderRequestDTO;

    @InjectMocks
    MovementOrderService movementOrderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    UserRepository  userRepository;
    @Mock
    SectorRepository sectorRepository;

    @BeforeEach
    public void setUp() {
        sector = new Sector();
        sector.setId(1L);
        sector.setName("Sector mock");

        user = new User();
        user.setId(1L);
        user.setUsername("User mock");
        user.setRole(UserRole.ADMIN);

        product = new Product();
        product.setId(1L);
        product.setName("Product mock");

        movementOrder = new MovementOrder();
        movementOrder.setId(1L);
        movementOrder.setUser(user);
        movementOrder.setProduct(product);
        movementOrder.setOriginSector(sector);
        movementOrder.setDestinationSector(sector);
        movementOrder.setStatus(MovementStatus.PENDING);
        movementOrder.setQuantity(new BigDecimal("100"));
        movementOrder.setCreatedAt(LocalDateTime.now());

        orderRequestDTO = new OrderRequestDTO(
                sector.getId(),
                sector.getId(),
                user.getId(),
                product.getId(),
                new BigDecimal("100")
        );
    }

    @Test
    public void mustSuccessfullyCreateOrder() {
        when(sectorRepository.findById(1L)).thenReturn(Optional.of(sector));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(MovementOrder.class))).thenReturn(movementOrder);

        OrderResponseDTO result = movementOrderService.createOrder(orderRequestDTO);

        assertNotNull(result);
        assertEquals(1, result.id());
    }

    @Test
    public void mustFindOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(movementOrder));

        OrderResponseDTO result = movementOrderService.findOrderById(1L);

        assertNotNull(result);
        assertEquals(1, result.id());
    }

    @Test
    public void mustFindOrderByIdWhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                () -> movementOrderService.findOrderById(99L)
        );
    }

    @Test
    public void mustFindAllOrders() {
        Page<MovementOrder> orderPage = new PageImpl<>(Collections.singletonList(movementOrder));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        PageResponseDTO<OrderResponseDTO> result = movementOrderService.findAllOrders(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void mustSuccessfullyUpdateOrder () {
        when(sectorRepository.findById(1L)).thenReturn(Optional.of(sector));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(movementOrder));
        when(orderRepository.save(any(MovementOrder.class))).thenReturn(movementOrder);

        OrderResponseDTO result = movementOrderService.updateOrder(1L, orderRequestDTO);

        assertNotNull(result);
        assertEquals(1, result.id());
    }

    @Test
    public void mustSuccessfullyDeleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        movementOrderService.deleteOrderById(1L);
        verify(orderRepository).deleteById(1L);
    }
}
