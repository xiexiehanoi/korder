package com.hanghae.korder.event.repository;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.entity.QEventDateEntity;
import com.hanghae.korder.event.entity.QEventEntity;
import com.hanghae.korder.event.entity.QEventSeatEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventDetailDto> findEventDetailsByEventId(Long eventId) {
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
}
