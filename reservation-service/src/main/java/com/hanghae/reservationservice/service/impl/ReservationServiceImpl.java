package com.hanghae.reservationservice.service.impl;

import com.hanghae.reservationservice.dto.ReservationDto;
import com.hanghae.reservationservice.entity.ReservationEntity;
import com.hanghae.reservationservice.feign.EventFeignClient;
import com.hanghae.reservationservice.feign.UserFeignClient;
import com.hanghae.reservationservice.repository.ReservationRepository;
import com.hanghae.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserFeignClient userFeignClient;
    private final EventFeignClient eventFeignClient;

    @Override
    public Mono<String> createReservation(ReservationDto dto) {
        return Mono.fromCallable(() -> {
            // User 확인
            userFeignClient.getUserById(dto.getUserId());

            // Event 확인 및 버전 업데이트
            eventFeignClient.getEventById(dto.getEventId());
            eventFeignClient.updateEventVersion(dto.getEventId());

            ReservationEntity reservation = ReservationEntity.builder()
                    .userId(dto.getUserId())
                    .eventId(dto.getEventId())
                    .quantity(1)
                    .status("pending")
                    .reservationDate(LocalDateTime.now())
                    .build();

            reservationRepository.save(reservation);
            return "예약이 생성되었습니다.";
        });
    }

    @Override
    public Mono<String> confirmReservation(Long reservationId) {
        return Mono.fromCallable(() -> {
            ReservationEntity reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

            reservation.setStatus("confirmed");
            reservationRepository.save(reservation);

            return "예약이 확정되었습니다.";
        });
    }
}
