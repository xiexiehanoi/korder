package com.hanghae.reservationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "event-service")
public interface EventFeignClient {
    @GetMapping("/event/{id}")
    void getEventById(@PathVariable("id") Long id);

    @PutMapping("/event/{id}/inventory")
    void updateEventInventory(@PathVariable("id") Long id, @RequestParam("quantityChange") int quantityChange);
}
