package com.hanghae.korder.event.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EventRequestDto {
    private String name;
    private String description;
    private String place;
    private List<EventDateDTO> eventDates;

    @Data
    public static class EventDateDTO {
        private String date;
        private List<SeatDTO> seats;

        @Data
        public static class SeatDTO {
            private String seatNumber;
            private BigDecimal price;
        }
    }
}