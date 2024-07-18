package com.hanghae.eventservice.repository;

import com.hanghae.eventservice.entity.EventInventory;
import com.hanghae.eventservice.repository.impl.EventInventoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventInventoryRepository extends JpaRepository<EventInventory,Long>,EventInventoryRepositoryCustom {

    //낙관적 락 사용
    Optional<EventInventory> findByEventIdWithLock(Long eventId);

}
