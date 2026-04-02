package com.flowline.flowline.repository;

import com.flowline.flowline.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
