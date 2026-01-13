package com.example.project.repository;

import com.example.project.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    List<Purchase> findByUserId(Long userId);

    boolean existsByGameId(Long gameId);
}
