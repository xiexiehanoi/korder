package com.hanghae.korder.purchase.dto;

import lombok.Data;

import java.time.LocalDateTime;

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
}