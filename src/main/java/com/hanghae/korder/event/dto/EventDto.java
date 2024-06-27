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

}