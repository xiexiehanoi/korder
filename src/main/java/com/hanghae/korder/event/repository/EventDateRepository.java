package com.hanghae.korder.event.repository;

import com.hanghae.korder.event.entity.EventDateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDateRepository extends JpaRepository<EventDateEntity, Long> {

    void deleteByEventId(Long eventId);

}
