package com.hanghae.korder.event.repository.querydsl;

import com.hanghae.korder.event.dto.EventDetailDto;

import java.util.List;

public interface EventRepositoryCustom {
    List<EventDetailDto> getEventDetails(Long eventId);
}
