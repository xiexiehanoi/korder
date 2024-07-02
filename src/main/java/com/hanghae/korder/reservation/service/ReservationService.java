package com.hanghae.korder.reservation.service;

import com.hanghae.korder.event.entity.EventSeatEntity;
import com.hanghae.korder.event.repository.EventSeatRepository;
import com.hanghae.korder.purchase.entity.PurchaseEntity;
import com.hanghae.korder.purchase.repository.PurchaseRepository;
import com.hanghae.korder.reservation.dto.ReservationRequestDto;
import com.hanghae.korder.reservation.dto.ReservationResponseDto;
import com.hanghae.korder.reservation.entity.ReservationEntity;
import com.hanghae.korder.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final EventSeatRepository eventSeatRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto request, Long userId) {
        EventSeatEntity seat = eventSeatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        if (seat.getQuantity() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient seat quantity");
        }

        eventSeatRepository.save(seat);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(userId);
        reservation.setEventId(request.getEventId());
        reservation.setEventDateId(request.getEventDateId());
        reservation.setSeatId(request.getSeatId());
        reservation.setQuantity(request.getQuantity());

//        BigDecimal totalPrice = seat.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
//        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("pending");

        ReservationEntity savedReservation = reservationRepository.save(reservation);
        return convertToDto(savedReservation);
    }

    @Transactional
    public ReservationResponseDto updateReservation(Long reservationId, ReservationRequestDto request, Long userId) {
        ReservationEntity reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        EventSeatEntity seat = eventSeatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        // 특정 좌석을 ID로 조회하고, 좌석이 존재하지 않을 경우 404 예외를 발생시킴
        EventSeatEntity oldSeat = eventSeatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not available"));
        oldSeat.setQuantity(oldSeat.getQuantity() + reservation.getQuantity());
        eventSeatRepository.save(oldSeat);

        if (seat.getQuantity() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient seat quantity");
        }

        eventSeatRepository.save(seat);

        reservation.setEventId(request.getEventId());
        reservation.setEventDateId(request.getEventDateId());
        reservation.setSeatId(request.getSeatId());
        reservation.setQuantity(request.getQuantity());

        BigDecimal totalPrice = seat.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
//        reservation.setTotalPrice(totalPrice);

        ReservationEntity updatedReservation = reservationRepository.save(reservation);
        return convertToDto(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        ReservationEntity reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        EventSeatEntity seat = eventSeatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        seat.setQuantity(seat.getQuantity() + reservation.getQuantity());
        eventSeatRepository.save(seat);

        reservation.setStatus("canceled");
        reservationRepository.save(reservation);
    }

    @Transactional
    public void confirmReservation(Long reservationId, Long userId) {
        ReservationEntity reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        reservation.setStatus("confirmed");
        reservationRepository.save(reservation);
        System.out.println("reservation = " + reservation);

        // 예약을 구매로 확정하는 로직
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setUserId(reservation.getUserId());
        purchase.setReservationId(reservation.getId());
        purchase.setStatus("pending"); // 예약 확인 후 상태를 pending로 설정
        purchase.setPurchaseDate(LocalDateTime.now());
        purchaseRepository.save(purchase);
        System.out.println("purchase = " + purchase);
    }

    @Transactional
    public List<ReservationResponseDto> getUserReservations(Long userId) {
        List<ReservationEntity> reservations = reservationRepository.findByUserId(userId);
        if (reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User reservations not found");
        }
        return reservations.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ReservationResponseDto convertToDto(ReservationEntity reservation) {
        ReservationResponseDto response = new ReservationResponseDto();
        response.setId(reservation.getId());
        response.setUserId(reservation.getUserId());
        response.setEventId(reservation.getEventId());
        response.setEventDateId(reservation.getEventDateId());
        response.setSeatId(reservation.getSeatId());
        response.setQuantity(reservation.getQuantity());
        response.setStatus(reservation.getStatus());
        response.setReservationDate(reservation.getReservationDate());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());
        response.setDeletedAt(reservation.getDeletedAt());
        return response;
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @Transactional
    public void cancelExpiredReservations() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<ReservationEntity> expiredReservations = reservationRepository.findByStatusAndCreatedAtBefore("pending", twentyFourHoursAgo);

        for (ReservationEntity reservation : expiredReservations) {
            EventSeatEntity seat = eventSeatRepository.findById(reservation.getSeatId())
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found"));
            seat.setQuantity(seat.getQuantity() + reservation.getQuantity());
            eventSeatRepository.save(seat);

            reservation.setStatus("canceled");
            reservationRepository.save(reservation);
        }
    }
}
