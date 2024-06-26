package com.hanghae.korder.purchase.controller;

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

    /**
     * 구매를 확정합니다.
     *
     * @param purchaseId 구매 ID
     * @param userDetails 사용자 정보
     * @return 응답 엔티티
     */
    @PutMapping("/confirm/{purchaseId}")
    public ResponseEntity<PurchaseDto> confirmPurchase(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        PurchaseDto response = purchaseService.confirmPurchase(purchaseId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 구매를 취소합니다.
     *
     * @param purchaseId 구매 ID
     * @param userDetails 사용자 정보
     * @return 응답 엔티티
     */
    @PutMapping("/cancel/{purchaseId}")
    public ResponseEntity<PurchaseDto> cancelPurchase(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        PurchaseDto response = purchaseService.cancelPurchase(purchaseId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 특정 예약 ID에 해당하는 구매 정보를 조회합니다.
     *
     * @param purchaseId 예약 ID
     * @param userDetails 사용자 정보
     * @return 구매 정보 목록
     */
    @GetMapping("/{purchaseId}")
    public ResponseEntity<List<PurchaseDto>> getPurchasesByReservationId(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        List<PurchaseDto> response = purchaseService.getPurchasesByReservationId(purchaseId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자 ID에 해당하는 구매 정보를 조회합니다.
     *
     * @param userDetails 사용자 정보
     * @return 구매 정보 목록
     */
    @GetMapping("/user")
    public ResponseEntity<List<PurchaseDto>> getPurchasesByUserId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        List<PurchaseDto> response = purchaseService.getPurchasesByUserId(userId);
        return ResponseEntity.ok(response);
    }
}