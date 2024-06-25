package com.hanghae.korder.event.repository;

import com.hanghae.korder.event.entity.EventDateEntity;
import com.hanghae.korder.event.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDateRepository extends JpaRepository<EventEntity, Long> {
}