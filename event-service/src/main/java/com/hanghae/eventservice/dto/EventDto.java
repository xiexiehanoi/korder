package com.hanghae.eventservice.dto;

import com.hanghae.eventservice.entity.EventEntity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EventDto {

    private String name;
    private Long userId;
    private String description;
    private String place;
    private LocalDate date;
    private BigDecimal price;
    private int quantity;
    private String status;


    // EventEntity를 EventDto로 변환하는 정적 메서드 추가
    public static EventDto fromEntity(EventEntity event) {
        return EventDto.builder()
                .name(event.getName())
                .userId(event.getUserId())
                .description(event.getDescription())
                .place(event.getPlace())
                .date(event.getDate())
                .price(event.getPrice())
                .quantity(event.getQuantity())
                .status(event.getStatus())
                .build();
    }
}
