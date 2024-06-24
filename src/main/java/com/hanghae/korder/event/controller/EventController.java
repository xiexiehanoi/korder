package com.hanghae.korder.event.controller;

import com.hanghae.korder.event.dto.EventDto;
import com.hanghae.korder.event.dto.EventRequestDTO;
import com.hanghae.korder.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/add")
    public ResponseEntity<EventDto> addEvent(@RequestBody EventRequestDTO eventRequestDTO,
                                             @AuthenticationPrincipal String email) {
        // 현재 인증된 사용자의 이메일을 사용할 수 있습니다.
        EventDto createdEvent = eventService.createEventWithDatesAndSeats(eventRequestDTO, email);
        return ResponseEntity.ok(createdEvent);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal String email) {
        eventService.deleteEvent(id, email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventRequestDTO eventRequestDTO,
                                                @AuthenticationPrincipal String email) {
        EventDto updatedEvent = eventService.updateEvent(eventRequestDTO, email);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/list")
    public ResponseEntity<List<EventDto>> getListEvent() {
        List<EventDto> eventList = eventService.getAllEvents();
        return ResponseEntity.ok(eventList);
    }

}