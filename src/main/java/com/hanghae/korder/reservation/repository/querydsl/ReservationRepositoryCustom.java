package com.hanghae.korder.reservation.repository.querydsl;

import com.hanghae.korder.reservation.entity.ReservationEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepositoryCustom {
    List<ReservationEntity> findByUserId(Long userId);
    List<ReservationEntity> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAt);
    Optional<ReservationEntity> findByIdAndUserId(Long id, Long userId);
}
