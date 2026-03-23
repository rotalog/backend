package com.rotalog.api.repository;

import com.rotalog.api.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<InventoryMovement> findByType(InventoryMovement.MovementType type);
}
