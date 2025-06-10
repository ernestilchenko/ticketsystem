package com.example.ticketmaster.controller;

import com.example.ticketmaster.dto.EventDto;
import com.example.ticketmaster.mapper.EventMapper;
import com.example.ticketmaster.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final EventService eventService;

    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String home(Model model) {
        var events = eventService.getAvailableEvents();
        List<EventDto> eventDtos = events.stream()
                .map(EventMapper::toDto)
                .toList();
        model.addAttribute("events", eventDtos);
        return "index";
    }
}