package com.hanghae.reservationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "event-service")
public interface EventFeignClient {
    @GetMapping("/event/{id}")
    void getEventById(@PathVariable("id") Long id);

    @PutMapping("/event/{id}/version")
    void updateEventVersion(@PathVariable("id") Long id);
}
