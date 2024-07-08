package com.hanghae.reservationservice.controller;

import com.hanghae.reservationservice.dto.ReservationDto;
import com.hanghae.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("")
    public ResponseEntity<Mono<String>> createReservation(@RequestBody ReservationDto dto) {
        return ResponseEntity.ok(reservationService.createReservation(dto));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Mono<String>> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }
}
