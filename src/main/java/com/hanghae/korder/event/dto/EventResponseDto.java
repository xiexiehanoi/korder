package com.hanghae.korder.event.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponseDto {
    private Long id;
    private String name;
    private String description;
    private String place;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<EventDateResponseDTO> eventDates;

    @Data
    public static class EventDateResponseDTO {
        private Long id;
        private String date;
        private List<SeatResponseDTO> seats;

        @Data
        public static class SeatResponseDTO {
            private Long id;
            private String seatNumber;
            private Double price;
            private String status;
        }
    }
}
