package com.flowline.flowline.repository;

import com.flowline.flowline.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByWarehouseId(Long warehouseId);
    Page<Product> findByWarehouseId(Long warehouseId, Pageable pageable);
}
