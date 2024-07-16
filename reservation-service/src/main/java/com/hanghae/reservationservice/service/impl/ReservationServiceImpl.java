package com.hanghae.reservationservice.service.impl;

import com.hanghae.reservationservice.dto.ReservationDto;
import com.hanghae.reservationservice.entity.ReservationEntity;
import com.hanghae.reservationservice.feign.EventFeignClient;
import com.hanghae.reservationservice.feign.UserFeignClient;
import com.hanghae.reservationservice.repository.ReservationRepository;
import com.hanghae.reservationservice.service.ReservationService;
import feign.FeignException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserFeignClient userFeignClient;
    private final EventFeignClient eventFeignClient;

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Override
    @Transactional
    public Mono<String> createReservation(ReservationDto dto) {
        return Mono.fromCallable(() -> {
            try {
                // Event 확인 및 재고 업데이트
                eventFeignClient.getEventById(dto.getEventId());
                eventFeignClient.updateEventInventory(dto.getEventId(), -dto.getQuantity());

                ReservationEntity reservation = ReservationEntity.builder()
                        .userId(dto.getUserId())
                        .eventId(dto.getEventId())
                        .status("pending")
                        .reservationDate(LocalDateTime.now())
                        .build();

                reservationRepository.save(reservation);
                return "예약이 생성되었습니다.";
            } catch (OptimisticLockException e) {
                logger.error("Optimistic lock exception: {}", e.getMessage());
                throw new RuntimeException("다른 사용자가 이미 이 이벤트를 수정했습니다. 다시 시도해주세요.");
            } catch (FeignException e) {
                logger.error("Feign client error: {}", e.getMessage());
                throw new RuntimeException("서비스가 현재 이용 불가합니다. 나중에 다시 시도해주세요.");
            } catch (Exception e) {
                logger.error("Error creating reservation: {}", e.getMessage());
                saveFailedReservation(dto, "failed_unknown");
                throw new RuntimeException("예약 생성 중 오류가 발생했습니다.");
            }
        });
    }

    private void saveFailedReservation(ReservationDto dto, String status) {
        ReservationEntity failedReservation = ReservationEntity.builder()
                .userId(dto.getUserId())
                .eventId(dto.getEventId())
                .quantity(dto.getQuantity())
                .status(status)
                .reservationDate(LocalDateTime.now())
                .build();
        reservationRepository.save(failedReservation);
    }


    @Override
    @Transactional
    public Mono<String> confirmReservation(Long reservationId, Long userId) {
        return Mono.fromCallable(() -> {
            try {
                ReservationEntity reservation = reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

                if (!reservation.getUserId().equals(userId)) {
                    throw new RuntimeException("해당 예약에 대한 권한이 없습니다.");
                }

                if ("confirmed".equals(reservation.getStatus())) {
                    return "이미 확정된 예약입니다.";
                }

                reservation.setStatus("confirmed");
                reservationRepository.save(reservation);

                return "예약이 확정되었습니다.";
            } catch (Exception e) {
                logger.error("Error confirming reservation: {}", e.getMessage());
                throw new RuntimeException("예약 확정 중 오류가 발생했습니다.");
            }
        });
    }
}
