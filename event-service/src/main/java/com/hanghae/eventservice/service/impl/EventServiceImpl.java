package com.hanghae.eventservice.service.impl;

import com.hanghae.eventservice.dto.EventDto;
import com.hanghae.eventservice.entity.EventEntity;
import com.hanghae.eventservice.entity.EventInventory;
import com.hanghae.eventservice.repository.EventInventoryRepository;
import com.hanghae.eventservice.repository.EventRepository;
import com.hanghae.eventservice.service.EventService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public EventDto getEventById(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));
        return EventDto.fromEntity(event);
    }

    @Override
    @Retryable(value = {ObjectOptimisticLockingFailureException.class, OptimisticLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2)) //낙관적 Lock 사용시
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW) //격리 레벨
    @CachePut(value = "eventReservations", key = "#eventId")
//    @Transactional
    public EventDto updateEventInventory(Long eventId, int quantity) {
        EventInventory inventory = eventInventoryRepository.findByEventIdWithLock(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트 인벤토리 정보를 찾을 수 없습니다."));

        int newRemainingQuantity = inventory.getRemainingQuantity() + quantity;
        if (newRemainingQuantity < 0) {
            throw new RuntimeException("티켓 재고가 부족합니다.");
        }

        inventory.setRemainingQuantity(newRemainingQuantity);
        eventInventoryRepository.save(inventory);

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));


        if (newRemainingQuantity == 0) {
            event.setStatus("sold out");
            logger.info("Event ID: {} is now sold out", eventId);
        } else if (event.getStatus().equals("sold out") && newRemainingQuantity > 0) {
            event.setStatus("available");
            logger.info("Event ID: {} is now available again", eventId);
        }

        event = eventRepository.save(event);
        logger.info("Event inventory 업데이트 for event ID: {}, new remaining quantity: {}", eventId, newRemainingQuantity);

        return EventDto.fromEntity(event);
    }
}