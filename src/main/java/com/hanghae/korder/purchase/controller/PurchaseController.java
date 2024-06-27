package com.hanghae.korder.purchase.controller;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.purchase.dto.PurchaseDto;
import com.hanghae.korder.purchase.service.PurchaseService;
import com.hanghae.korder.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PutMapping("/confirm/{purchaseId}")
    public ResponseEntity<PurchaseDto> confirmPurchase(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        PurchaseDto response = purchaseService.confirmPurchase(purchaseId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/cancel/{purchaseId}")
    public ResponseEntity<PurchaseDto> cancelPurchase(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        PurchaseDto response = purchaseService.cancelPurchase(purchaseId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseDto> getPurchaseById(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        PurchaseDto response = purchaseService.getPurchaseById(purchaseId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PurchaseDto>> getPurchasesByUserId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        List<PurchaseDto> response = purchaseService.getPurchasesByUserId(userId);
        return ResponseEntity.ok(response);
    }
}