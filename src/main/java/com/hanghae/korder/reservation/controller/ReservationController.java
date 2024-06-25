package com.hanghae.korder.reservation.controller;

import com.hanghae.korder.reservation.dto.ReservationRequestDto;
import com.hanghae.korder.reservation.dto.ReservationResponseDto;
import com.hanghae.korder.reservation.dto.ReservationStatusResponseDto;
import com.hanghae.korder.reservation.service.ReservationService;
import com.hanghae.korder.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("")
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        ReservationResponseDto response = reservationService.createReservation(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDto> updateReservation(@PathVariable Long reservationId, @RequestBody ReservationRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ReservationResponseDto response = reservationService.updateReservation(reservationId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationStatusResponseDto> cancelReservation(@PathVariable Long reservationId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reservationService.cancelReservation(reservationId);
        ReservationStatusResponseDto response = new ReservationStatusResponseDto(reservationId, "canceled");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm/{reservationId}")
    public ResponseEntity<ReservationStatusResponseDto> confirmReservation(@PathVariable Long reservationId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reservationService.confirmReservation(reservationId);
        ReservationStatusResponseDto response = new ReservationStatusResponseDto(reservationId, "confirmed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ReservationResponseDto>> getUserReservations(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        List<ReservationResponseDto> response = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(response);
    }
}
