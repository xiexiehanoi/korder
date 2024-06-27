package com.hanghae.korder.event.service;


import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.dto.EventRequestDto;
import com.hanghae.korder.event.dto.EventResponseDto;
import com.hanghae.korder.event.entity.EventEntity;
import com.hanghae.korder.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddEvent() {
        EventRequestDto requestDto = createEventRequestDto();
        EventEntity savedEvent = createEventEntity();

        when(eventRepository.save(any(EventEntity.class))).thenReturn(savedEvent);

        EventResponseDto responseDto = eventService.addEvent(requestDto, "testUser");

        assertNotNull(responseDto);
        assertEquals("Concert", responseDto.getName());
        verify(eventRepository, times(1)).save(any(EventEntity.class));
    }

    @Test
    public void testDeleteEvent() {
        EventEntity event = createEventEntity();
        when(eventRepository.findByIdAndCreatedBy(anyLong(), anyString())).thenReturn(Optional.of(event));

        eventService.deleteEvent(1L, "testUser");

        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    public void testDeleteEvent_NotFound() {
        when(eventRepository.findByIdAndCreatedBy(anyLong(), anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> eventService.deleteEvent(1L, "testUser"));
    }

    @Test
    public void testUpdateEvent() {
        EventRequestDto requestDto = createEventRequestDto();
        EventEntity existingEvent = createEventEntity();
        EventEntity updatedEvent = createEventEntity();
        updatedEvent.setName("Updated Concert");

        when(eventRepository.findByIdAndCreatedBy(anyLong(), anyString())).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(EventEntity.class))).thenReturn(updatedEvent);

        EventResponseDto responseDto = eventService.updateEvent(1L, requestDto, "testUser");

        assertNotNull(responseDto);
        assertEquals("Updated Concert", responseDto.getName());
        verify(eventRepository, times(1)).save(any(EventEntity.class));
    }

    @Test
    public void testUpdateEvent_NotFound() {
        EventRequestDto requestDto = createEventRequestDto();
        when(eventRepository.findByIdAndCreatedBy(anyLong(), anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> eventService.updateEvent(1L, requestDto, "testUser"));
    }

    @Test
    public void testGetAllListEvent() {
        EventEntity event = createEventEntity();
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event));

        List<EventResponseDto> responseDtoList = eventService.getAllListEvent();

        assertNotNull(responseDtoList);
        assertEquals(1, responseDtoList.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testGetDetailEvent() {
        EventDetailDto eventDetail = new EventDetailDto(1L, "Concert", "Music event", "Place", LocalDate.now(), 1L, "A1", BigDecimal.valueOf(100), "available");
        when(eventRepository.getEventDetails(anyLong())).thenReturn(Arrays.asList(eventDetail));

        List<EventDetailDto> detailDtoList = eventService.getDetailEvent(1L);

        assertNotNull(detailDtoList);
        assertEquals(1, detailDtoList.size());
        verify(eventRepository, times(1)).getEventDetails(anyLong());
    }

    private EventRequestDto createEventRequestDto() {
        EventRequestDto requestDto = new EventRequestDto();
        requestDto.setName("Concert");
        requestDto.setDescription("Music event");
        requestDto.setPlace("Place");
        // Add event dates and seats to the requestDto as needed
        return requestDto;
    }

    private EventEntity createEventEntity() {
        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setName("Concert");
        event.setDescription("Music event");
        event.setPlace("Place");
        event.setCreatedBy("testUser");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        // Add event dates and seats to the event as needed
        return event;
    }
}