package com.rotalog.api.repository;

import com.rotalog.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByDistributorId(Long distributorId);
    List<Order> findByStatus(Order.OrderStatus status);

    @Query("SELECT p FROM Order p JOIN FETCH p.customer JOIN FETCH p.distributor")
    List<Order> findAllWithDetails();
}
