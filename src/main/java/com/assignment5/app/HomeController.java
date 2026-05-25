package com.assignment5.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "This is my Assignment 5 deployed using Jenkins Pipeline";
    }
}
