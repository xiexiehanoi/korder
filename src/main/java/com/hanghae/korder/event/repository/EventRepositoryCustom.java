package com.hanghae.korder.event.repository;

import com.hanghae.korder.event.dto.EventDetailDto;

import java.util.List;

public interface EventRepositoryCustom {
    List<EventDetailDto> findEventDetailsByEventId(Long eventId);
}
