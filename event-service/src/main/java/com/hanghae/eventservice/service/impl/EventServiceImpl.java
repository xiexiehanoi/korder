package com.hanghae.eventservice.service.impl;

import com.hanghae.eventservice.dto.EventDto;
import com.hanghae.eventservice.entity.EventEntity;
import com.hanghae.eventservice.repository.EventRepository;
import com.hanghae.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Mono<String> createEvent(EventDto eventDto, Long userId) {
        EventEntity event = EventEntity.builder()
                .name(eventDto.getName())
                .description(eventDto.getDescription())
                .place(eventDto.getPlace())
                .userId(userId)
                .date(eventDto.getDate())
                .price(eventDto.getPrice())
                .quantity(eventDto.getQuantity())
                .status(eventDto.getStatus())
                .build();

        return Mono.fromCallable(() -> {
            eventRepository.save(event);
            return "이벤트가 생성되었습니다.";
        });
    }
}