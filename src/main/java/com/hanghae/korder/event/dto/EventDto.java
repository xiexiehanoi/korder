package com.hanghae.korder.event.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EventDto {

    private Long eventId;
    private String eventName;
    private String eventDescription;
    private String eventPlace;
    private LocalDate eventDate;
    private Long seatId;
    private String seatNumber;
    private BigDecimal seatPrice;
    private String seatStatus;

    public EventDto(Long eventId, String eventName, String eventDescription, String eventPlace) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventPlace = eventPlace;
    }

    public EventDto(Long eventId, String eventName, String eventDescription, String eventPlace, LocalDate eventDate) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventPlace = eventPlace;
        this.eventDate = eventDate;
    }

    public EventDto(Long eventId, String eventName, String eventDescription, String eventPlace, LocalDate eventDate, Long seatId, String seatNumber, BigDecimal seatPrice, String seatStatus) {
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