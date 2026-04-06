package com.flowline.flowline.service;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.ProductRequestDTO;
import com.flowline.flowline.dto.ProductResponseDTO;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.Product;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        Product product = new Product();
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + request.warehouseId()));

        product.setName(request.name());
        product.setWeight(request.weight());
        product.setUnit(request.unit());
        product.setLocation(request.location());
        product.setWarehouse(warehouse);
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDTO(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getWeight(),
                savedProduct.getUnit(),
                savedProduct.getLocation(),
                savedProduct.getWarehouse().getId());
    }

    public ProductResponseDTO findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getWeight(),
                product.getUnit(),
                product.getLocation(),
                product.getWarehouse().getId());
    }

    public PageResponseDTO<ProductResponseDTO> findAllProducts(Pageable pageable) {
        Page<ProductResponseDTO> page = productRepository.findAll(pageable)
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getWeight(),
                        product.getUnit(),
                        product.getLocation(),
                        product.getWarehouse().getId()));
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + request.warehouseId()));
        product.setName(request.name());
        product.setWeight(request.weight());
        product.setUnit(request.unit());
        product.setLocation(request.location());
        product.setWarehouse(warehouse);
        Product updatedProduct = productRepository.save(product);
        return new ProductResponseDTO(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getWeight(),
                updatedProduct.getUnit(),
                updatedProduct.getLocation(),
                updatedProduct.getWarehouse().getId());
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}