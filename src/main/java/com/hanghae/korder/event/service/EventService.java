package com.hanghae.korder.event.service;

import com.hanghae.korder.event.dto.EventDto;
import com.hanghae.korder.event.dto.EventRequestDTO;
import com.hanghae.korder.event.entity.EventDateEntity;
import com.hanghae.korder.event.entity.EventEntity;
import com.hanghae.korder.event.entity.EventSeatEntity;
import com.hanghae.korder.event.repository.EventDateRepository;
import com.hanghae.korder.event.repository.EventRepository;
import com.hanghae.korder.event.repository.EventSeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventDateRepository eventDateRepository;
    private final EventSeatRepository seatRepository;

    public EventService(EventRepository eventRepository, EventDateRepository eventDateRepository, EventSeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.eventDateRepository = eventDateRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public EventDto createEventWithDatesAndSeats(EventRequestDTO eventRequestDTO, String email) {
        EventEntity eventEntity = new EventEntity(eventRequestDTO.getName(),
                eventRequestDTO.getDescription(),
                eventRequestDTO.getPlace());
        EventEntity savedEventEntity = eventRepository.save(eventEntity);

        eventRequestDTO.getEventDates().forEach(eventDateDTO -> {
            EventDateEntity eventDateEntity = new EventDateEntity(savedEventEntity,
                    eventDateDTO.getDate());
            EventDateEntity savedEventDateEntity = eventDateRepository.save(eventDateEntity);

            List<EventSeatEntity> seatEntities = eventDateDTO.getSeats().stream()
                    .map(seatDTO -> new EventSeatEntity(savedEventDateEntity,
                            seatDTO.getSeatNumber(),
                            seatDTO.getPrice(),
                            "available"))
                    .collect(Collectors.toList());
            seatRepository.saveAll(seatEntities);
        });

        return new EventDto(savedEventEntity.getId(),
                savedEventEntity.getName(),
                savedEventEntity.getDescription(),
                savedEventEntity.getPlace());
    }

    @Transactional
    public void deleteEvent(Long id, String email) {
        eventRepository.deleteById(id);
    }

    @Transactional
    public EventDto updateEvent(EventRequestDTO eventRequestDTO, String email) {
        Optional<EventEntity> optionalEvent = eventRepository.findById(eventRequestDTO.getId());
        if (optionalEvent.isPresent()) {
            EventEntity eventEntity = optionalEvent.get();
            eventEntity.setName(eventRequestDTO.getName());
            eventEntity.setDescription(eventRequestDTO.getDescription());
            eventEntity.setPlace(eventRequestDTO.getPlace());
            EventEntity updatedEventEntity = eventRepository.save(eventEntity);

            eventDateRepository.deleteByEventId(updatedEventEntity.getId());
            seatRepository.deleteByEventDateEventId(updatedEventEntity.getId());

            eventRequestDTO.getEventDates().forEach(eventDateDTO -> {
                EventDateEntity eventDateEntity = new EventDateEntity(updatedEventEntity,
                        eventDateDTO.getDate());
                EventDateEntity savedEventDateEntity = eventDateRepository.save(eventDateEntity);

                List<EventSeatEntity> seatEntities = eventDateDTO.getSeats().stream()
                        .map(seatDTO -> new EventSeatEntity(savedEventDateEntity,
                                seatDTO.getSeatNumber(),
                                seatDTO.getPrice(),
                                "available"))
                        .collect(Collectors.toList());
                seatRepository.saveAll(seatEntities);
            });

            return new EventDto(updatedEventEntity.getId(),
                    updatedEventEntity.getName(),
                    updatedEventEntity.getDescription(),
                    updatedEventEntity.getPlace());
        }
        return null;
    }

    @Transactional
    public List<EventDto> getAllEvents() {
        List<EventEntity> events = eventRepository.findAll();
        return events.stream()
                .map(event -> new EventDto(event.getId(),
                        event.getName(),
                        event.getDescription(),
                        event.getPlace()))
                .collect(Collectors.toList());
    }
}
