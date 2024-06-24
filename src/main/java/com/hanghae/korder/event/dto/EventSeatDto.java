package com.hanghae.korder.event.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EventSeatDto {
    private Long id;
    private Long eventDateId;
    private String seatNumber;
    private BigDecimal price;
    private String status;
}
