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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private record ProductDependencies( Warehouse warehouse ) {}

    private ProductDependencies resolveDependencies(ProductRequestDTO request) {
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Warehouse not found with id: " + request.warehouseId()));
        return new ProductDependencies(warehouse);
    }

    private ProductResponseDTO toResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getWeight(),
                product.getUnit(),
                product.getLocation(),
                product.getWarehouse().getId());
    }

    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        log.info("Creating product: name={}, warehouseId={}",
                request.name(), request.warehouseId());

        ProductDependencies deps = resolveDependencies(request);
        Product product = new Product();
        product.setName(request.name());
        product.setWeight(request.weight());
        product.setUnit(request.unit());
        product.setLocation(request.location());
        product.setWarehouse(deps.warehouse);
        ProductResponseDTO result = toResponse(productRepository.save(product));

        log.info("Product created successfully: id={}, name={}",
                result.id(), result.name());
        return result;
    }

    public ProductResponseDTO findProductById(Long id) {
        log.info("Finding product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found: id={}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        ProductResponseDTO result = toResponse(product);
        log.info("Product find successfully: id={}, name={}",
                result.id(), result.name());
        return result;
    }

    public PageResponseDTO<ProductResponseDTO> findAllProducts(Pageable pageable) {
        log.info("Finding all products in page: {}", pageable);
        Page<ProductResponseDTO> page = productRepository.findAll(pageable)
                .map(this::toResponse);
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        log.info("Update product: id={}, name={}", id, request.name());
        ProductDependencies deps = resolveDependencies(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found: id={}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        product.setName(request.name());
        product.setWeight(request.weight());
        product.setUnit(request.unit());
        product.setLocation(request.location());
        product.setWarehouse(deps.warehouse);
        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product by id: {}", id);
        productRepository.deleteById(id);
        log.info("Product deleted: id={}", id);
    }
}