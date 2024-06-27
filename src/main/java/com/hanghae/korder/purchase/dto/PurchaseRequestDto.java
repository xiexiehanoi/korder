package com.hanghae.korder.purchase.dto;

import lombok.Data;

@Data
public class PurchaseRequestDto {
    private Long userId;
    private Long reservationId;
    private String status;
}