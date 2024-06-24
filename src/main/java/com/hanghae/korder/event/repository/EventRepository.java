package com.hanghae.korder.event.repository;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.dto.EventDto;
import com.hanghae.korder.event.entity.EventEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT new com.hanghae.korder.event.dto.EventDetailDto(e.id, e.name, e.description, e.place, ed.date, s.id, s.seatNumber, s.price, s.status) " +
            "FROM EventEntity e " +
            "JOIN e.eventDates ed " +
            "JOIN ed.seats s " +
            "WHERE e.id = :eventId")
    List<EventDetailDto> findEventDetailsByEventId(@Param("eventId") Long eventId);

    Optional<EventEntity> findByIdAndCreatedBy(Long id, String createdBy);

    List<EventEntity> findAll();
}