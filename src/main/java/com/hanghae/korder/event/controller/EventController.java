package com.hanghae.korder.event.controller;

import com.hanghae.korder.user.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/event")
public class EventController {

//    @PostMapping("addEvent"){
//
//    }


    @GetMapping("/lists")
    public String getProducts(HttpServletRequest req) {
        System.out.println("EventController.getProducts : 인증 완료");
        UserEntity user = (UserEntity) req.getAttribute("user");
        System.out.println("user.getEmail() = " + user.getEmail());

        return "redirect:/";
    }
}