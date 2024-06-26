package com.hanghae.korder.purchase.dto;

import com.hanghae.korder.event.dto.EventDetailDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseDto {
    private Long id;
    private Long userId;
    private Long reservationId;
    private String status;
    private LocalDateTime purchaseDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String eventName;
    private String eventDescription;
    private String eventPlace;
    private LocalDateTime eventDate;
    private String seatNumber;
    private int seatQuantity;
    private BigDecimal seatPrice;
}