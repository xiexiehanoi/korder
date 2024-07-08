package com.hanghae.eventservice.service.impl;

import com.hanghae.eventservice.dto.EventDto;
import com.hanghae.eventservice.entity.EventEntity;
import com.hanghae.eventservice.repository.EventRepository;
import com.hanghae.eventservice.service.EventService;
import jakarta.transaction.Transactional;
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

    @Override
    public EventDto getEventById(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));
        return EventDto.fromEntity(event);
    }

    @Override
    @Transactional
    public void updateEventVersion(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));
        // 버전만 증가
        event.setVersion(event.getVersion() + 1);
    }
}