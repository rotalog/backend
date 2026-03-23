package com.rotalog.api.repository;

import com.rotalog.api.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
    boolean existsByProductId(Long productId);

    @Query("SELECT e FROM Inventory e WHERE e.currentQuantity <= e.minimumQuantity")
    List<Inventory> findLowStock();
}
