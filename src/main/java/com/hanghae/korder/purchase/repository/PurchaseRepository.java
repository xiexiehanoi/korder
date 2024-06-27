package com.hanghae.korder.purchase.repository;

import com.hanghae.korder.purchase.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {
    List<PurchaseEntity> findByReservationId(Long reservationId);
    List<PurchaseEntity> findByUserId(Long userId);

    Optional<PurchaseEntity> findByIdAndUserId(Long purchaseId, Long userId);
}