package com.hanghae.reservationservice.service;

import com.hanghae.reservationservice.dto.ReservationDto;

import reactor.core.publisher.Mono;


public interface ReservationService {
    Mono<String> createReservation(ReservationDto dto);
    Mono<String> confirmReservation(Long reservationId, Long userId);
}
