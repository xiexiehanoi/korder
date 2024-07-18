package com.hanghae.reservationservice.queue;

import com.hanghae.reservationservice.dto.ReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationQueue {
    private final RedisTemplate<String, ReservationDto> redisTemplate;
    private static final String QUEUE_KEY = "reservation:queue";

    public void enqueue(ReservationDto dto) {
        redisTemplate.opsForList().rightPush(QUEUE_KEY, dto);
    }

    public ReservationDto dequeue() {
        return redisTemplate.opsForList().leftPop(QUEUE_KEY);
    }
}