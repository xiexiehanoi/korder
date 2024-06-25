package com.hanghae.korder.reservation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservationResponseDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long eventDateId;
    private Long seatId;
    private int quantity;
    private String status;
    private LocalDateTime reservationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}

