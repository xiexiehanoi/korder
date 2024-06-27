package com.hanghae.korder.event.service;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.dto.EventRequestDto;
import com.hanghae.korder.event.dto.EventResponseDto;
import com.hanghae.korder.event.entity.EventDateEntity;
import com.hanghae.korder.event.entity.EventEntity;
import com.hanghae.korder.event.entity.EventSeatEntity;
import com.hanghae.korder.event.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public EventResponseDto addEvent(EventRequestDto request, String createdBy) {
        EventEntity event = new EventEntity();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setPlace(request.getPlace());
        event.setCreatedBy(createdBy);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        List<EventDateEntity> eventDates = request.getEventDates().stream().map(eventDateDTO -> {
            EventDateEntity eventDate = new EventDateEntity();
            eventDate.setDate(LocalDate.parse(eventDateDTO.getDate()));
            eventDate.setCreatedAt(LocalDateTime.now());
            eventDate.setUpdatedAt(LocalDateTime.now());
            eventDate.setEvent(event);

            List<EventSeatEntity> seats = eventDateDTO.getSeats().stream().map(seatDTO -> {
                EventSeatEntity seat = new EventSeatEntity();
                seat.setSeatNumber(seatDTO.getSeatNumber());
                seat.setPrice(seatDTO.getPrice());
                seat.setQuantity(seatDTO.getQuantity());
                seat.setStatus("available");
                seat.setCreatedAt(LocalDateTime.now());
                seat.setUpdatedAt(LocalDateTime.now());
                seat.setEventDate(eventDate);
                return seat;
            }).collect(Collectors.toList());
            eventDate.setSeats(seats);

            return eventDate;
        }).collect(Collectors.toList());

        event.setEventDates(eventDates);

        EventEntity savedEvent = eventRepository.save(event);
        return convertToResponse(savedEvent);
    }

    @Transactional
    public void deleteEvent(Long eventId, String createdBy) {
        EventEntity event = eventRepository.findByIdAndCreatedBy(eventId, createdBy)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found or user not authorized"));
        eventRepository.delete(event);
    }

    @Transactional
    public EventResponseDto updateEvent(Long eventId, EventRequestDto request, String createdBy) {
        EventEntity event = eventRepository.findByIdAndCreatedBy(eventId, createdBy)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found or user not authorized"));
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setPlace(request.getPlace());
        event.setUpdatedAt(LocalDateTime.now());

        List<EventDateEntity> eventDates = request.getEventDates().stream().map(eventDateDTO -> {
            EventDateEntity eventDate = new EventDateEntity();
            eventDate.setDate(LocalDate.parse(eventDateDTO.getDate()));
            eventDate.setCreatedAt(LocalDateTime.now());
            eventDate.setUpdatedAt(LocalDateTime.now());
            eventDate.setEvent(event);

            List<EventSeatEntity> seats = eventDateDTO.getSeats().stream().map(seatDTO -> {
                EventSeatEntity seat = new EventSeatEntity();
                seat.setSeatNumber(seatDTO.getSeatNumber());
                seat.setPrice(seatDTO.getPrice());
                seat.setQuantity(seatDTO.getQuantity());
                seat.setStatus("available");
                seat.setCreatedAt(LocalDateTime.now());
                seat.setUpdatedAt(LocalDateTime.now());
                seat.setEventDate(eventDate);
                return seat;
            }).collect(Collectors.toList());
            eventDate.setSeats(seats);

            return eventDate;
        }).collect(Collectors.toList());

        event.setEventDates(eventDates);

        EventEntity savedEvent = eventRepository.save(event);
        return convertToResponse(savedEvent);
    }

    public List<EventResponseDto> getAllListEvent() {
        return eventRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<EventDetailDto> getDetailEvent(Long eventId) {
        return eventRepository.getEventDetails(eventId);
    }

    //콘서트 기본 내용
    private EventResponseDto convertToSimpleResponse(EventEntity event) {
        EventResponseDto response = new EventResponseDto();
        response.setId(event.getId());
        response.setName(event.getName());
        response.setDescription(event.getDescription());
        response.setPlace(event.getPlace());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        response.setDeletedAt(event.getDeletedAt());
        return response;
    }

    //콘서트 디테일 내용
    private EventResponseDto convertToResponse(EventEntity event) {
        EventResponseDto response = convertToSimpleResponse(event);
        response.setEventDates(event.getEventDates().stream()
                .map(eventDate -> {
                    EventResponseDto.EventDateResponseDTO eventDateResponse = new EventResponseDto.EventDateResponseDTO();
                    eventDateResponse.setId(eventDate.getId());
                    eventDateResponse.setDate(eventDate.getDate().toString());
                    eventDateResponse.setSeats(eventDate.getSeats().stream()
                            .map(seat -> {
                                EventResponseDto.EventDateResponseDTO.SeatResponseDTO seatResponse = new EventResponseDto.EventDateResponseDTO.SeatResponseDTO();
                                seatResponse.setId(seat.getId());
                                seatResponse.setSeatNumber(seat.getSeatNumber());
                                seatResponse.setPrice(seat.getPrice());
                                seatResponse.setQuantity(seat.getQuantity());
                                seatResponse.setStatus(seat.getStatus());
                                return seatResponse;
                            })
                            .collect(Collectors.toList()));
                    return eventDateResponse;
                })
                .collect(Collectors.toList()));
        return response;
    }
}
