package com.rotalog.api.repository;

import com.rotalog.api.model.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Long> {
    List<Distributor> findByActiveTrue();
    Optional<Distributor> findByTaxId(String taxId);
    boolean existsByTaxId(String taxId);
}
