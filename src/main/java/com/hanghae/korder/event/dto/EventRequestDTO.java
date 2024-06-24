package com.hanghae.korder.event.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EventRequestDTO {
    private Long id;
    private String name;
    private String description;
    private String place;
    private List<EventDateDTO> eventDates;

    @Getter
    @Setter
    public static class EventDateDTO {
        private LocalDate date;
        private List<SeatDTO> seats;

        @Getter
        @Setter
        public static class SeatDTO {
            private String seatNumber;
            private BigDecimal price;
        }
    }
}