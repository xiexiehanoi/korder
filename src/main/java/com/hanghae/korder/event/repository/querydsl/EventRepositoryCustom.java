package com.hanghae.korder.event.repository.querydsl;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.entity.EventEntity;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryCustom {
    List<EventDetailDto> getEventDetails(Long eventId);

    Optional<EventEntity> findByIdAndCreatedBy(Long id, String createdBy);
}
