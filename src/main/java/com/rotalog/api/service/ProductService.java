package com.rotalog.api.service;

import com.rotalog.api.dto.ProductDTO;
import com.rotalog.api.exception.BusinessException;
import com.rotalog.api.exception.ResourceNotFoundException;
import com.rotalog.api.model.Product;
import com.rotalog.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO.Response> listAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDTO.Response> listActive() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(ProductDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO.Response findById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO.Response::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductDTO.Response> findByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductDTO.Response::fromEntity)
                .toList();
    }

    @Transactional
    public ProductDTO.Response create(ProductDTO.Request request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .unitOfMeasure(request.getUnitOfMeasure())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return ProductDTO.Response.fromEntity(productRepository.save(product));
    }

    @Transactional
    public ProductDTO.Response update(Long id, ProductDTO.Request request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        if (request.getActive() != null) product.setActive(request.getActive());

        return ProductDTO.Response.fromEntity(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductDTO.Response toggleStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(!product.getActive());
        return ProductDTO.Response.fromEntity(productRepository.save(product));
    }

    // Internal method to fetch the entity
    public Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
