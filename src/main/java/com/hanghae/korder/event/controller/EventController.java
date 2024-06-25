package com.hanghae.korder.event.controller;

import com.hanghae.korder.event.dto.EventDetailDto;
import com.hanghae.korder.event.dto.EventDto;
import com.hanghae.korder.event.dto.EventRequestDto;
import com.hanghae.korder.event.dto.EventResponseDto;
import com.hanghae.korder.event.entity.EventEntity;
import com.hanghae.korder.event.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @PostMapping("")
    public ResponseEntity<EventResponseDto> addEvent(@RequestBody EventRequestDto request, @AuthenticationPrincipal UserDetails userDetails) {
        String createdBy = userDetails.getUsername();
        EventResponseDto response = eventService.addEvent(request, createdBy);
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, @AuthenticationPrincipal UserDetails userDetails) {
        String createdBy = userDetails.getUsername();
        eventService.deleteEvent(eventId, createdBy);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long eventId, @RequestBody EventRequestDto request, @AuthenticationPrincipal UserDetails userDetails) {
        String createdBy = userDetails.getUsername();
        EventResponseDto response = eventService.updateEvent(eventId, request, createdBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<List<EventResponseDto>> getAllListEvent() {
        List<EventResponseDto> response = eventService.getAllListEvent();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<EventDetailDto>> getDetailEvent(@PathVariable Long eventId) {
        List<EventDetailDto> response = eventService.getDetailEvent(eventId);
        return ResponseEntity.ok(response);
    }
}