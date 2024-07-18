package com.hanghae.reservationservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ReservationDto implements Serializable {

    private Long id;
    private Long userId;
    private Long eventId;
    private int quantity;
    private String status;
    private LocalDateTime reservationDate;

}
