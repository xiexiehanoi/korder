package com.hanghae.eventservice.service.impl;

import com.hanghae.eventservice.dto.EventDto;
import com.hanghae.eventservice.entity.EventEntity;
import com.hanghae.eventservice.entity.EventInventory;
import com.hanghae.eventservice.repository.EventInventoryRepository;
import com.hanghae.eventservice.repository.EventRepository;
import com.hanghae.eventservice.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventInventoryRepository eventInventoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Override
    @Transactional
    public Mono<String> createEvent(EventDto eventDto, Long userId) {
        return Mono.fromCallable(() -> {
            EventEntity event = EventEntity.builder()
                    .name(eventDto.getName())
                    .description(eventDto.getDescription())
                    .place(eventDto.getPlace())
                    .userId(userId)
                    .date(eventDto.getDate())
                    .price(eventDto.getPrice())
                    .quantity(eventDto.getQuantity())
                    .status("available")  // 초기 상태는 "available"로 설정
                    .build();

            event = eventRepository.save(event);

            // 이벤트 생성 후 인벤토리 자동 생성
            EventInventory inventory = EventInventory.builder()
                    .eventId(event.getId())
                    .totalQuantity(event.getQuantity())
                    .remainingQuantity(event.getQuantity())
                    .build();

            eventInventoryRepository.save(inventory);

            logger.info("Event created with ID: {} and Inventory initialized", event.getId());
            return "이벤트가 생성되었고, 인벤토리가 초기화되었습니다.";
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
    public void updateEventInventory(Long eventId, int quantityChange) {
        EventInventory inventory = eventInventoryRepository.findByEventIdWithLock(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트 인벤토리 정보를 찾을 수 없습니다."));

        int newRemainingQuantity = inventory.getRemainingQuantity() + quantityChange;
        if (newRemainingQuantity < 0) {
            throw new RuntimeException("티켓 재고가 부족합니다.");
        }

        inventory.setRemainingQuantity(newRemainingQuantity);
        eventInventoryRepository.save(inventory);

        if (newRemainingQuantity == 0) {
            // 재고가 소진되면 이벤트 상태를 'sold out'으로 변경
            EventEntity event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));
            event.setStatus("sold out");
            eventRepository.save(event);
            logger.info("Event ID: {} is now sold out", eventId);
        }

        logger.info("Event inventory updated for event ID: {}, new remaining quantity: {}", eventId, newRemainingQuantity);
    }
}