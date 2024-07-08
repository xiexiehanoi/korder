package com.hanghae.eventservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventDto {

    private String name;
    private Long userId;
    private String description;
    private String place;
    private LocalDate date;
    private BigDecimal price;
    private int quantity;
    private String status;

}
