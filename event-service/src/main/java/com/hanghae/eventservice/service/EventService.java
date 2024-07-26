package com.hanghae.eventservice.service;

import com.hanghae.eventservice.dto.EventDto;
import reactor.core.publisher.Mono;

public interface EventService {

    Mono<String> createEvent(EventDto dto, Long userId);

    EventDto getEventById(Long id);

    EventDto updateEventInventory(Long eventId, int quantity);

}
