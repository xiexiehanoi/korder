package com.hanghae.korder.event.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EventDetailDto {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private String eventPlace;
    private LocalDate eventDate;
    private Long seatId;
    private String seatNumber;
    private BigDecimal seatPrice;  // BigDecimal로 유지
    private String seatStatus;

    public EventDetailDto(Long eventId, String eventName, String eventDescription, String eventPlace, LocalDate eventDate, Long seatId, String seatNumber, BigDecimal seatPrice, String seatStatus) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventPlace = eventPlace;
        this.eventDate = eventDate;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;
        this.seatStatus = seatStatus;
    }
}
