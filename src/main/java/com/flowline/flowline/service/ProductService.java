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
        ProductDependencies deps = resolveDependencies(request);
        Product product = new Product();
        product.setName(request.name());
        product.setWeight(request.weight());
        product.setUnit(request.unit());
        product.setLocation(request.location());
        product.setWarehouse(deps.warehouse);
        return toResponse(productRepository.save(product));
    }

    public ProductResponseDTO findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toResponse(product);
    }

    public PageResponseDTO<ProductResponseDTO> findAllProducts(Pageable pageable) {
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
        ProductDependencies deps = resolveDependencies(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Product not found with id: " + id));
        product.setName(request.name());
        product.setWeight(request.weight());
        product.setUnit(request.unit());
        product.setLocation(request.location());
        product.setWarehouse(deps.warehouse);
        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}