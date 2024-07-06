package com.hanghae.eventservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "Event service is running";
    }
    @GetMapping("/")
    public String home() {
        return "Welcome to Event Service";
    }
}