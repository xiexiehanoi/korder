package com.hanghae.korder.reservation.repository.querydsl.impl;

import com.hanghae.korder.reservation.entity.QReservationEntity;
import com.hanghae.korder.reservation.entity.ReservationEntity;
import com.hanghae.korder.reservation.repository.querydsl.ReservationRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReservationEntity> findByUserId(Long userId) {
        QReservationEntity reservation = QReservationEntity.reservationEntity;
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.userId.eq(userId))
                .fetch();
    }

    @Override
    public List<ReservationEntity> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAt) {
        QReservationEntity reservation = QReservationEntity.reservationEntity;
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.status.eq(status)
                        .and(reservation.createdAt.before(createdAt)))
                .fetch();
    }

    @Override
    public Optional<ReservationEntity> findByIdAndUserId(Long id, Long userId) {
        QReservationEntity reservation = QReservationEntity.reservationEntity;
        ReservationEntity result = queryFactory
                .selectFrom(reservation)
                .where(reservation.id.eq(id)
                        .and(reservation.userId.eq(userId)))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}