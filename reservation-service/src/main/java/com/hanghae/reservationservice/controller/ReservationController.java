package com.hanghae.reservationservice.controller;

import com.hanghae.reservationservice.dto.ReservationDto;
import com.hanghae.reservationservice.service.ReservationService;
import com.hanghae.reservationservice.service.impl.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @PostMapping("")
    public ResponseEntity<Mono<String>> createReservation(@RequestBody ReservationDto dto, @RequestHeader("X-User-Id") Long userId) {
        dto.setUserId(userId);
        return ResponseEntity.ok(reservationService.createReservation(dto));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Mono<String>> confirmReservation(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reservationService.confirmReservation(id, userId));
    }
}
