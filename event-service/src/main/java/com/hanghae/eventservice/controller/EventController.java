package com.hanghae.eventservice.controller;

import com.hanghae.eventservice.dto.EventDto;
import com.hanghae.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @PostMapping("")
    public ResponseEntity<Mono<String>> eventCreate(@RequestBody EventDto dto, @RequestHeader("X-User-id") Long userId) {
        return ResponseEntity.ok(eventService.createEvent(dto, userId));
    }

}
