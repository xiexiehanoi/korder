package com.hanghae.eventservice.repository.impl;


import com.hanghae.eventservice.entity.EventInventory;
import com.hanghae.eventservice.entity.QEventInventory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventInventoryRepositoryImpl implements EventInventoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<EventInventory> findByEventIdWithLock(Long eventId) {
        QEventInventory eventInventory = QEventInventory.eventInventory;

        EventInventory result = queryFactory
                .selectFrom(eventInventory)
                .where(eventInventory.eventId.eq(eventId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
