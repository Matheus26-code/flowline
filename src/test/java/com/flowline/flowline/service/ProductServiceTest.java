package com.flowline.flowline.service;

import com.flowline.flowline.dto.*;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.model.Product;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.WarehouseRepository;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private Warehouse warehouse;
    private Product product;
    private ProductRequestDTO productRequestDTO;

    @Mock
    ProductRepository productRepository;

    @Mock
    WarehouseRepository warehouseRepository;

    @InjectMocks
    ProductService productService;

    @BeforeEach
    public void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Empresa teste mock");

        product = new Product();
        product.setId(1L);
        product.setName("Product Mock");
        product.setWeight(new BigDecimal("100"));
        product.setUnit("Unit Mock");
        product.setLocation("Location mock");
        product.setWarehouse(warehouse);

        productRequestDTO = new ProductRequestDTO(
                "Product Mock", new BigDecimal("100"), "Unit Mock",
                "Location mock", 1L);
        }

    @Test
    public void mustSuccessfullyCreateProduct() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Product Mock", result.name());
        assertEquals("100", result.weight().toString());
    }

    @Test
    public void mustFindProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductResponseDTO result = productService.findProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    public void mustReturnErrorWithProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findProductById(1L)
        );
    }

    @Test
    public void mustFindAllProduct() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        PageResponseDTO<ProductResponseDTO> result = productService.findAllProducts(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void mustSuccessfullyUpdateProduct() {

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponseDTO result = productService.updateProduct(1L, productRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Product Mock", result.name());
    }

    @Test
    public void mustSuccessfullyDeleteProduct() {
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

}
