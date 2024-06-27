package com.hanghae.korder.event.repository.querydsl.impl;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.entity.EventEntity;
import com.hanghae.korder.event.entity.QEventDateEntity;
import com.hanghae.korder.event.entity.QEventEntity;
import com.hanghae.korder.event.entity.QEventSeatEntity;
import com.hanghae.korder.event.repository.querydsl.EventRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventDetailDto> getEventDetails(Long eventId) {
        QEventEntity event = QEventEntity.eventEntity;
        QEventDateEntity eventDate = QEventDateEntity.eventDateEntity;
        QEventSeatEntity seat = QEventSeatEntity.eventSeatEntity;

        return queryFactory.select(Projections.constructor(EventDetailDto.class,
                        event.id,
                        event.name,
                        event.description,
                        event.place,
                        eventDate.date,
                        seat.id,
                        seat.seatNumber,
                        seat.price,
                        seat.status))
                .from(event)
                .join(event.eventDates, eventDate)
                .join(eventDate.seats, seat)
                .where(event.id.eq(eventId))
                .fetch();
    }


    @Override
    public Optional<EventEntity> findByIdAndCreatedBy(Long id, String createdBy) {
        QEventEntity event = QEventEntity.eventEntity;

        EventEntity foundEvent = queryFactory.selectFrom(event)
                .where(event.id.eq(id)
                        .and(event.createdBy.eq(createdBy)))
                .fetchOne();

        return Optional.ofNullable(foundEvent);
    }
}
