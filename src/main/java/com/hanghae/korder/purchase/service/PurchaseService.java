package com.hanghae.korder.purchase.service;

import com.hanghae.korder.event.entity.EventSeatEntity;
import com.hanghae.korder.event.repository.EventSeatRepository;
import com.hanghae.korder.purchase.dto.PurchaseDto;
import com.hanghae.korder.purchase.entity.PurchaseEntity;
import com.hanghae.korder.purchase.repository.PurchaseRepository;
import com.hanghae.korder.reservation.entity.ReservationEntity;
import com.hanghae.korder.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ReservationRepository reservationRepository;
    private final EventSeatRepository eventSeatRepository;

    @Transactional
    public PurchaseDto confirmPurchase(Long purchaseId, Long userId) {
        PurchaseEntity purchase = purchaseRepository.findByIdAndUserId(purchaseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        // 구매 확정 로직
        purchase.setStatus("confirmed"); // 수정: "completed"에서 "confirmed"로 변경
        purchase.setPurchaseDate(LocalDateTime.now());
        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);

        return convertToDto(savedPurchase);
    }

    @Transactional
    public PurchaseDto cancelPurchase(Long purchaseId, Long userId) {
        PurchaseEntity purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        // 구매가 현재 사용자 소유인지 확인
        if (!purchase.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to cancel this purchase");
        }

        ReservationEntity reservation = reservationRepository.findById(purchase.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        EventSeatEntity seat = eventSeatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        seat.setQuantity(seat.getQuantity() + reservation.getQuantity());
        eventSeatRepository.save(seat);

        purchase.setStatus("cancelled");
        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);

        return convertToDto(savedPurchase);
    }

    @Transactional
    public List<PurchaseDto> getPurchasesByReservationId(Long purchaseId, Long userId) {
        ReservationEntity reservation = reservationRepository.findByIdAndUserId(purchaseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        List<PurchaseEntity> purchases = purchaseRepository.findByReservationId(purchaseId);
        if (purchases.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchases not found for the reservation");
        }
        return purchases.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public List<PurchaseDto> getPurchasesByUserId(Long userId) {
        List<PurchaseEntity> purchases = purchaseRepository.findByUserId(userId);
        if (purchases.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchases not found for the user");
        }
        return purchases.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private PurchaseDto convertToDto(PurchaseEntity purchase) {
        PurchaseDto response = new PurchaseDto();
        response.setId(purchase.getId());
        response.setUserId(purchase.getUserId());
        response.setReservationId(purchase.getReservationId());
        response.setStatus(purchase.getStatus());
        response.setPurchaseDate(purchase.getPurchaseDate());
        response.setCreatedAt(purchase.getCreatedAt());
        response.setUpdatedAt(purchase.getUpdatedAt());
        response.setDeletedAt(purchase.getDeletedAt());
        return response;
    }
}