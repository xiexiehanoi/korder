package com.hanghae.korder.event.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventDateDto {
    private Long id;
    private Long eventId;
    private LocalDate date;
}
