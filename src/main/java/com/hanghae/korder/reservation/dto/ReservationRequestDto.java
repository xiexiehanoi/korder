package com.hanghae.korder.reservation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservationRequestDto {
    private Long userId;
    private Long eventId;
    private Long eventDateId;
    private Long seatId;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
