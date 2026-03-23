package com.rotalog.api.service;

import com.rotalog.api.dto.InventoryDTO;
import com.rotalog.api.exception.BusinessException;
import com.rotalog.api.exception.ResourceNotFoundException;
import com.rotalog.api.model.Inventory;
import com.rotalog.api.model.InventoryMovement;
import com.rotalog.api.model.Order;
import com.rotalog.api.model.Product;
import com.rotalog.api.repository.InventoryRepository;
import com.rotalog.api.repository.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<InventoryDTO.Response> listAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryDTO.Response findByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(InventoryDTO.Response::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO.Response> listLowStock() {
        return inventoryRepository.findLowStock()
                .stream()
                .map(InventoryDTO.Response::fromEntity)
                .toList();
    }

    @Transactional
    public InventoryDTO.Response create(InventoryDTO.Request request) {
        Product product = productService.findEntityById(request.getProductId());

        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new BusinessException("An inventory record already exists for this product.");
        }

        Inventory inventory = Inventory.builder()
                .product(product)
                .currentQuantity(request.getCurrentQuantity())
                .minimumQuantity(request.getMinimumQuantity() != null ? request.getMinimumQuantity() : 0)
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        // Register initial movement if quantity is greater than zero
        if (request.getCurrentQuantity() > 0) {
            registerMovementInternal(product, InventoryMovement.MovementType.INBOUND,
                    request.getCurrentQuantity(), "Initial stock", 0, request.getCurrentQuantity(), null);
        }

        return InventoryDTO.Response.fromEntity(saved);
    }

    @Transactional
    public InventoryDTO.MovementResponse move(InventoryDTO.MovementRequest request) {
        Product product = productService.findEntityById(request.getProductId());

        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + request.getProductId()));

        int previousBalance = inventory.getCurrentQuantity();
        int currentBalance;

        switch (request.getType()) {
            case INBOUND -> currentBalance = previousBalance + request.getQuantity();
            case OUTBOUND -> {
                if (previousBalance < request.getQuantity()) {
                    throw new BusinessException("Insufficient stock. Available: " + previousBalance
                            + ", Requested: " + request.getQuantity());
                }
                currentBalance = previousBalance - request.getQuantity();
            }
            case ADJUSTMENT -> currentBalance = request.getQuantity();
            default -> throw new BusinessException("Invalid movement type.");
        }

        inventory.setCurrentQuantity(currentBalance);
        inventoryRepository.save(inventory);

        InventoryMovement movement = registerMovementInternal(
                product, request.getType(), request.getQuantity(),
                request.getReason(), previousBalance, currentBalance, null);

        return InventoryDTO.MovementResponse.fromEntity(movement);
    }

    // Internal method called by OrderService when confirming an order
    @Transactional
    public void deductStockForOrder(Product product, int quantity, Order order) {
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + product.getName()));

        if (inventory.getCurrentQuantity() < quantity) {
            throw new BusinessException("Insufficient stock for product '" + product.getName()
                    + "'. Available: " + inventory.getCurrentQuantity() + ", Required: " + quantity);
        }

        int previousBalance = inventory.getCurrentQuantity();
        int currentBalance = previousBalance - quantity;

        inventory.setCurrentQuantity(currentBalance);
        inventoryRepository.save(inventory);

        registerMovementInternal(product, InventoryMovement.MovementType.OUTBOUND,
                quantity, "Outbound for order #" + order.getId(), previousBalance, currentBalance, order);
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO.MovementResponse> listMovementsByProduct(Long productId) {
        return movementRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(InventoryDTO.MovementResponse::fromEntity)
                .toList();
    }

    private InventoryMovement registerMovementInternal(Product product,
            InventoryMovement.MovementType type, int quantity,
            String reason, int previousBalance, int currentBalance, Order order) {

        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .type(type)
                .quantity(quantity)
                .reason(reason)
                .previousBalance(previousBalance)
                .currentBalance(currentBalance)
                .order(order)
                .build();

        return movementRepository.save(movement);
    }
}
