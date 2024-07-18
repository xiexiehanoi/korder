package com.hanghae.eventservice.repository.impl;

import com.hanghae.eventservice.entity.EventInventory;

import java.util.Optional;

public interface EventInventoryRepositoryCustom {

    Optional<EventInventory> findByEventIdWithLock(Long eventId);

}
