package com.hanghae.korder.purchase.service;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.repository.EventRepository;
import com.hanghae.korder.purchase.dto.PurchaseDto;
import com.hanghae.korder.purchase.entity.PurchaseEntity;
import com.hanghae.korder.purchase.repository.PurchaseRepository;
import com.hanghae.korder.reservation.entity.ReservationEntity;
import com.hanghae.korder.reservation.repository.ReservationRepository;
import com.hanghae.korder.user.entity.UserEntity;
import com.hanghae.korder.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    @Transactional
    public PurchaseDto confirmPurchase(Long purchaseId, Long userId) {
        PurchaseEntity purchase = purchaseRepository.findByIdAndUserId(purchaseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        if ("cancel".equals(purchase.getStatus())) {
            logger.warn("Cannot confirm a cancelled purchase, purchaseId: {}, userId: {}", purchaseId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot confirm a cancelled purchase");
        }

        ReservationEntity reservation = reservationRepository.findById(purchase.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        EventDetailDto eventDetail = eventRepository.getEventDetails(reservation.getEventId())
                .stream()
                .filter(detail -> detail.getSeatId().equals(reservation.getSeatId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event detail not found"));

        BigDecimal totalCost = eventDetail.getSeatPrice().multiply(BigDecimal.valueOf(reservation.getQuantity()));
        BigDecimal userPoints = BigDecimal.valueOf(user.getPoints());

        if (userPoints.compareTo(totalCost) < 0) {
            logger.warn("Insufficient points to complete the purchase, userId: {}, required: {}, available: {}", userId, totalCost, userPoints);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient points to complete the purchase");
        }

        // 구매 확정 로직
        user.setPoints(userPoints.subtract(totalCost).intValue());
        userRepository.save(user);

        purchase.setStatus("confirmed");
        purchase.setPurchaseDate(LocalDateTime.now());
        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);

        return convertToDto(savedPurchase, reservation, eventDetail);
    }

    @Transactional
    public PurchaseDto cancelPurchase(Long purchaseId, Long userId) {
        PurchaseEntity purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        if (!purchase.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to cancel this purchase");
        }

        ReservationEntity reservation = reservationRepository.findById(purchase.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        EventDetailDto eventDetail = eventRepository.getEventDetails(reservation.getEventId())
                .stream()
                .filter(detail -> detail.getSeatId().equals(reservation.getSeatId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event detail not found"));

        purchase.setStatus("cancelled");
        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);

        return convertToDto(savedPurchase, reservation, eventDetail);
    }

    @Transactional
    public PurchaseDto getPurchaseById(Long purchaseId, Long userId) {
        PurchaseEntity purchase = purchaseRepository.findByIdAndUserId(purchaseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        ReservationEntity reservation = reservationRepository.findById(purchase.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        EventDetailDto eventDetail = eventRepository.getEventDetails(reservation.getEventId())
                .stream()
                .filter(detail -> detail.getSeatId().equals(reservation.getSeatId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event detail not found"));

        return convertToDto(purchase, reservation, eventDetail);
    }

    @Transactional
    public List<PurchaseDto> getPurchasesByUserId(Long userId) {
        List<PurchaseEntity> purchases = purchaseRepository.findByUserId(userId);
        if (purchases.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchases not found for the user");
        }
        return purchases.stream().map(purchase -> {
            ReservationEntity reservation = reservationRepository.findById(purchase.getReservationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

            EventDetailDto eventDetail = eventRepository.getEventDetails(reservation.getEventId())
                    .stream()
                    .filter(detail -> detail.getSeatId().equals(reservation.getSeatId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event detail not found"));

            return convertToDto(purchase, reservation, eventDetail);
        }).collect(Collectors.toList());
    }

    private PurchaseDto convertToDto(PurchaseEntity purchase, ReservationEntity reservation, EventDetailDto eventDetail) {
        PurchaseDto response = new PurchaseDto();
        response.setId(purchase.getId());
        response.setUserId(purchase.getUserId());
        response.setReservationId(purchase.getReservationId());
        response.setStatus(purchase.getStatus());
        response.setPurchaseDate(purchase.getPurchaseDate());
        response.setCreatedAt(purchase.getCreatedAt());
        response.setUpdatedAt(purchase.getUpdatedAt());
        response.setDeletedAt(purchase.getDeletedAt());

        // 추가 정보 설정
        response.setEventName(eventDetail.getEventName());
        response.setEventDescription(eventDetail.getEventDescription());
        response.setEventPlace(eventDetail.getEventPlace());
        response.setEventDate(eventDetail.getEventDate().atStartOfDay());
        response.setSeatNumber(eventDetail.getSeatNumber());
        response.setSeatQuantity(reservation.getQuantity());
        response.setSeatPrice(eventDetail.getSeatPrice());

        return response;
    }
}