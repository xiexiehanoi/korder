package com.hanghae.reservationservice.service.impl;

import com.hanghae.reservationservice.dto.ReservationDto;
import com.hanghae.reservationservice.entity.ReservationEntity;
import com.hanghae.reservationservice.feign.EventFeignClient;
import com.hanghae.reservationservice.queue.ReservationQueue;
import com.hanghae.reservationservice.repository.ReservationRepository;
import com.hanghae.reservationservice.service.ReservationService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventFeignClient eventFeignClient;
    private final ReservationQueue reservationQueue;
    private final TransactionTemplate transactionTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Override
    public Mono<String> createReservation(ReservationDto dto) {
        return Mono.fromCallable(() -> {
            try {
                // 큐에 예약 요청 추가
                reservationQueue.enqueue(dto);
                logger.info("Reservation request added to queue for event: {}", dto.getEventId());
                return "예약 요청이 큐에 추가되었습니다. 잠시 후 처리됩니다.";
            } catch (Exception e) {
                logger.error("Error adding reservation to queue: {}", e.getMessage());
                throw new RuntimeException("예약 요청 추가 중 오류가 발생했습니다.");
            }
        });
    }

    @Scheduled(fixedRate = 100)
    public void processReservationQueue() {
        ReservationDto dto = reservationQueue.dequeue();
        if (dto != null) {
            logger.info("큐에서 예약 처리 중. 이벤트: {}, 사용자: {}", dto.getEventId(), dto.getUserId());
            try {
                transactionTemplate.execute((status) -> {
                    try {
                        processReservation(dto);
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        logger.error("예약 처리 실패. 이벤트: {}, 사용자: {}, 오류: {}",
                                dto.getEventId(), dto.getUserId(), e.getMessage(), e);
                        saveFailedReservation(dto, "처리_실패");
                    }
                    return null;
                });
            } catch (Exception e) {
                logger.error("트랜잭션 중 예상치 못한 오류 발생. 이벤트: {}, 사용자: {}, 오류: {}",
                        dto.getEventId(), dto.getUserId(), e.getMessage(), e);
            }
        }
    }

    private void processReservation(ReservationDto dto) throws Exception {
        logger.info("예약 처리 시작. 이벤트: {}, 사용자: {}", dto.getEventId(), dto.getUserId());

        try {
            eventFeignClient.getEventById(dto.getEventId());
            logger.info("이벤트 확인 성공. 이벤트: {}", dto.getEventId());
        } catch (Exception e) {
            logger.error("이벤트 확인 실패. 이벤트: {}, 오류: {}", dto.getEventId(), e.getMessage(), e);
            throw e;
        }

        try {
            eventFeignClient.updateEventInventory(dto.getEventId(), -dto.getQuantity());
            logger.info("이벤트 재고 업데이트 성공. 이벤트: {}, 수량: {}", dto.getEventId(), dto.getQuantity());
        } catch (Exception e) {
            logger.error("이벤트 재고 업데이트 실패. 이벤트: {}, 수량: {}, 오류: {}",
                    dto.getEventId(), dto.getQuantity(), e.getMessage(), e);
            throw e;
        }

        ReservationEntity reservation = ReservationEntity.builder()
                .userId(dto.getUserId())
                .eventId(dto.getEventId())
                .quantity(dto.getQuantity())
                .status("pending")
                .reservationDate(LocalDateTime.now())
                .build();

        try {
            reservation = reservationRepository.save(reservation);
            logger.info("예약 데이터베이스 저장 성공. ID: {}, 이벤트: {}, 사용자: {}",
                    reservation.getId(), dto.getEventId(), dto.getUserId());
        } catch (Exception e) {
            logger.error("예약 데이터베이스 저장 실패. 이벤트: {}, 사용자: {}, 오류: {}",
                    dto.getEventId(), dto.getUserId(), e.getMessage(), e);
            throw e;
        }
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
        logger.warn("예약 저장  실패: {}", failedReservation.getId());
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
