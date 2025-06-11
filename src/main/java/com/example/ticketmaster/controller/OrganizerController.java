package com.example.ticketmaster.controller;

import com.example.ticketmaster.dto.CreateEventDto;
import com.example.ticketmaster.dto.EventDto;
import com.example.ticketmaster.dto.TicketDto;
import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.User;
import com.example.ticketmaster.mapper.EventMapper;
import com.example.ticketmaster.mapper.TicketMapper;
import com.example.ticketmaster.service.EventService;
import com.example.ticketmaster.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/organizer")
public class OrganizerController {

    private final EventService eventService;
    private final TicketService ticketService;

    public OrganizerController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User organizer = (User) authentication.getPrincipal();
        var myEvents = eventService.getEventsByOrganizerDto(organizer);

        model.addAttribute("myEvents", myEvents.size());
        model.addAttribute("approvedEvents", myEvents.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.APPROVED).count());
        model.addAttribute("pendingEvents", myEvents.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.PENDING_APPROVAL).count());

        var recentEvents = myEvents.stream()
                .sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()))
                .limit(5)
                .toList();
        model.addAttribute("recentEvents", recentEvents);

        model.addAttribute("currentPage", "organizer-dashboard");

        return "organizer/dashboard";
    }

    @GetMapping("/events")
    public String events(Authentication authentication, Model model) {
        User organizer = (User) authentication.getPrincipal();
        model.addAttribute("events", eventService.getEventsByOrganizerDto(organizer));
        model.addAttribute("currentPage", "organizer-events");
        return "organizer/events";
    }

    @GetMapping("/events/create")
    public String createEventForm(Model model) {
        model.addAttribute("event", new CreateEventDto());
        model.addAttribute("categories", Event.EventCategory.values());
        model.addAttribute("currentPage", "organizer-create-event");
        return "organizer/create-event";
    }

    @PostMapping("/events/create")
    public String createEvent(@Valid @ModelAttribute("event") CreateEventDto createEventDto, BindingResult result,
                              Authentication authentication, Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", Event.EventCategory.values());
            model.addAttribute("currentPage", "organizer-create-event");
            return "organizer/create-event";
        }

        try {
            User organizer = (User) authentication.getPrincipal();
            Event event = EventMapper.toEntity(createEventDto);
            eventService.createEvent(event, organizer);
            redirectAttributes.addFlashAttribute("message",
                    "Event created successfully! It's pending approval.");
            return "redirect:/organizer/events";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create event: " + e.getMessage());
            model.addAttribute("categories", Event.EventCategory.values());
            model.addAttribute("currentPage", "organizer-create-event");
            return "organizer/create-event";
        }
    }

    @GetMapping("/events/{id}/edit")
    public String editEventForm(@PathVariable Long id, Authentication authentication, Model model) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User organizer = (User) authentication.getPrincipal();
        if (!eventService.canUserModifyEvent(event, organizer)) {
            throw new RuntimeException("You don't have permission to edit this event");
        }

        CreateEventDto createEventDto = EventMapper.toCreateDto(event);
        EventDto eventDto = EventMapper.toDto(event);
        model.addAttribute("event", createEventDto);
        model.addAttribute("eventEntity", eventDto);
        model.addAttribute("categories", Event.EventCategory.values());
        model.addAttribute("currentPage", "organizer-events");
        return "organizer/edit-event";
    }

    @PostMapping("/events/{id}/edit")
    public String editEvent(@PathVariable Long id, @Valid @ModelAttribute("event") CreateEventDto createEventDto, BindingResult result,
                            Authentication authentication, Model model,
                            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            EventDto eventDto = eventService.findByIdDto(id)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            model.addAttribute("eventEntity", eventDto);
            model.addAttribute("categories", Event.EventCategory.values());
            model.addAttribute("currentPage", "organizer-events");
            return "organizer/edit-event";
        }

        try {
            User organizer = (User) authentication.getPrincipal();
            Event existingEvent = eventService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            if (!eventService.canUserModifyEvent(existingEvent, organizer)) {
                throw new RuntimeException("You don't have permission to edit this event");
            }

            Event event = EventMapper.toEntity(createEventDto);
            event.setId(id);
            eventService.updateEvent(event);
            redirectAttributes.addFlashAttribute("message", "Event updated successfully!");
            return "redirect:/organizer/events";
        } catch (Exception e) {
            EventDto eventDto = eventService.findByIdDto(id)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            model.addAttribute("eventEntity", eventDto);
            model.addAttribute("error", "Failed to update event: " + e.getMessage());
            model.addAttribute("categories", Event.EventCategory.values());
            model.addAttribute("currentPage", "organizer-events");
            return "organizer/edit-event";
        }
    }

    @GetMapping("/events/{id}/tickets")
    public String eventTickets(@PathVariable Long id, Authentication authentication, Model model) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User organizer = (User) authentication.getPrincipal();
        if (!eventService.canUserModifyEvent(event, organizer)) {
            throw new RuntimeException("You don't have permission to view tickets for this event");
        }

        var tickets = ticketService.getTicketsByEvent(event);
        List<TicketDto> ticketDtos = tickets.stream()
                .map(TicketMapper::toDto)
                .toList();

        EventDto eventDto = EventMapper.toDto(event);
        model.addAttribute("event", eventDto);
        model.addAttribute("tickets", ticketDtos);
        model.addAttribute("currentPage", "organizer-events");
        return "organizer/event-tickets";
    }
}