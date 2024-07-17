package com.hanghae.reservationservice.feign;

import com.hanghae.reservationservice.config.FeignClientConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "event-service", configuration = FeignClientConfig.class)
public interface EventFeignClient {
    @Cacheable(value = "events", key = "#id")
    @GetMapping("/event/{id}")
    void getEventById(@PathVariable("id") Long id);

    @CachePut(value = "events", key = "#id")
    @PutMapping("/event/{id}/inventory")
    void updateEventInventory(@PathVariable("id") Long id, @RequestParam("quantityChange") int quantityChange);
}
