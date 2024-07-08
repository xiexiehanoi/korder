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

    @GetMapping("/{id}")
    public ResponseEntity<Void> getEventById(@PathVariable Long id) {
        eventService.getEventById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/version")
    public ResponseEntity<Void> updateEventVersion(@PathVariable Long id) {
        eventService.updateEventVersion(id);
        return ResponseEntity.ok().build();
    }

}
