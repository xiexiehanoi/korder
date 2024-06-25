package com.hanghae.korder.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationStatusResponseDto {
    private Long reservationId;
    private String status;
}