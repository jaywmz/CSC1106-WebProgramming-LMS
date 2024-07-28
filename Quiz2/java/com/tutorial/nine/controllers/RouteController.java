package com.tutorial.nine.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RouteController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/camera")
    public String camera() {
        return "camera";
    }

    @GetMapping("/canvas")
    public String canvas() {
        return "canvas";
    }

    @GetMapping("/geolocation")
    public String geolocation() {
        return "geolocation";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("/audio")
    public String media() {
        return "audio";
    }

    @GetMapping("/video")
    public String video() {
        return "video";
    }
    
    @GetMapping("/pay")
    public String pay() {
        return "pay";
    }

}
