package com.hanghae.korder.reservation.service;

import com.hanghae.korder.event.entity.EventSeatEntity;
import com.hanghae.korder.event.repository.EventSeatRepository;
import com.hanghae.korder.reservation.dto.ReservationRequestDto;
import com.hanghae.korder.reservation.dto.ReservationResponseDto;
import com.hanghae.korder.reservation.entity.ReservationEntity;
import com.hanghae.korder.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final EventSeatRepository eventSeatRepository;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto request, Long userId) {
        EventSeatEntity seat = eventSeatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        if (seat.getQuantity() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient seat quantity");
        }

        seat.setQuantity(seat.getQuantity() - request.getQuantity());
        eventSeatRepository.save(seat);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(userId);
        reservation.setEventId(request.getEventId());
        reservation.setEventDateId(request.getEventDateId());
        reservation.setSeatId(request.getSeatId());
        reservation.setQuantity(request.getQuantity());

        BigDecimal totalPrice = seat.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("pending");

        ReservationEntity savedReservation = reservationRepository.save(reservation);
        return convertToDto(savedReservation);
    }

    @Transactional
    public ReservationResponseDto updateReservation(Long reservationId, ReservationRequestDto request) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        EventSeatEntity seat = eventSeatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        EventSeatEntity oldSeat = eventSeatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Old seat not found"));
        oldSeat.setQuantity(oldSeat.getQuantity() + reservation.getQuantity());
        eventSeatRepository.save(oldSeat);

        if (seat.getQuantity() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient seat quantity");
        }

        seat.setQuantity(seat.getQuantity() - request.getQuantity());
        eventSeatRepository.save(seat);

        reservation.setEventId(request.getEventId());
        reservation.setEventDateId(request.getEventDateId());
        reservation.setSeatId(request.getSeatId());
        reservation.setQuantity(request.getQuantity());

        BigDecimal totalPrice = seat.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        reservation.setTotalPrice(totalPrice);

        ReservationEntity updatedReservation = reservationRepository.save(reservation);
        return convertToDto(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        EventSeatEntity seat = eventSeatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        seat.setQuantity(seat.getQuantity() + reservation.getQuantity());
        eventSeatRepository.save(seat);

        reservation.setStatus("canceled");
        reservationRepository.save(reservation);
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        reservation.setStatus("confirmed");
        reservationRepository.save(reservation);
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

}
