package com.rotalog.api.service;

import com.rotalog.api.dto.OrderDTO;
import com.rotalog.api.exception.BusinessException;
import com.rotalog.api.exception.ResourceNotFoundException;
import com.rotalog.api.model.*;
import com.rotalog.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final DistributorService distributorService;
    private final ProductService productService;
    private final InventoryService inventoryService;

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> listAll() {
        return orderRepository.findAllWithDetails()
                .stream()
                .map(OrderDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO.Response findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderDTO.Response.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(OrderDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findByDistributor(Long distributorId) {
        return orderRepository.findByDistributorId(distributorId)
                .stream()
                .map(OrderDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(OrderDTO.Response::fromEntity)
                .toList();
    }

    @Transactional
    public OrderDTO.Response create(OrderDTO.Request request) {
        Customer customer = customerService.findEntityById(request.getCustomerId());
        Distributor distributor = distributorService.findEntityById(request.getDistributorId());

        if (!customer.getActive()) {
            throw new BusinessException("Inactive customer. Cannot create order.");
        }
        if (!distributor.getActive()) {
            throw new BusinessException("Inactive distributor. Cannot create order.");
        }

        Order order = Order.builder()
                .customer(customer)
                .distributor(distributor)
                .notes(request.getNotes())
                .status(Order.OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        for (OrderDTO.OrderItemRequest itemReq : request.getItems()) {
            Product product = productService.findEntityById(itemReq.getProductId());

            if (!product.getActive()) {
                throw new BusinessException("Product '" + product.getName() + "' is inactive.");
            }

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .subtotal(itemReq.getUnitPrice().multiply(java.math.BigDecimal.valueOf(itemReq.getQuantity())))
                    .build();

            order.getItems().add(item);
        }

        order.calculateTotal();
        return OrderDTO.Response.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO.Response updateStatus(Long id, OrderDTO.StatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        validateStatusTransition(order.getStatus(), request.getStatus());

        // Deduct stock when confirming the order
        if (request.getStatus() == Order.OrderStatus.CONFIRMED
                && order.getStatus() == Order.OrderStatus.PENDING) {
            for (OrderItem item : order.getItems()) {
                inventoryService.deductStockForOrder(item.getProduct(), item.getQuantity(), order);
            }
        }

        order.setStatus(request.getStatus());
        return OrderDTO.Response.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public void cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BusinessException("Delivered orders cannot be cancelled.");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == Order.OrderStatus.CONFIRMED || next == Order.OrderStatus.CANCELLED;
            case CONFIRMED -> next == Order.OrderStatus.IN_TRANSIT || next == Order.OrderStatus.CANCELLED;
            case IN_TRANSIT -> next == Order.OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BusinessException("Invalid status transition: " + current + " → " + next);
        }
    }
}
