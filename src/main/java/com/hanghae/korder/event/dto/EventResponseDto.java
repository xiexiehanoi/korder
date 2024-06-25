package com.hanghae.korder.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponseDto {
    private Long id;
    private String name;
    private String description;
    private String place;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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
            private BigDecimal price;
            private int quantity;
            private String status;
        }
    }
}
