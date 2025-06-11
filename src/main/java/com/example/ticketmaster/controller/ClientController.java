package com.example.ticketmaster.controller;

import com.example.ticketmaster.dto.EventDto;
import com.example.ticketmaster.dto.TicketDto;
import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.User;
import com.example.ticketmaster.mapper.TicketMapper;
import com.example.ticketmaster.service.EventService;
import com.example.ticketmaster.service.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final EventService eventService;
    private final TicketService ticketService;

    public ClientController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User client = (User) authentication.getPrincipal();
        var myTickets = ticketService.getTicketsByUser(client);

        List<TicketDto> ticketDtos = myTickets.stream()
                .map(TicketMapper::toDto)
                .toList();

        model.addAttribute("totalTickets", ticketDtos.size());
        model.addAttribute("recentTickets", ticketDtos.stream()
                .limit(5)
                .toList());
        model.addAttribute("availableEvents", eventService.getAvailableEventsDto().size());
        model.addAttribute("currentPage", "client-dashboard");

        return "client/dashboard";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.getAvailableEventsDto());
        model.addAttribute("categories", Event.EventCategory.values());
        model.addAttribute("currentPage", "client-events");
        return "client/events";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        EventDto event = eventService.findByIdDto(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        model.addAttribute("event", event);
        model.addAttribute("currentPage", "client-events");
        return "client/event-details";
    }

    @PostMapping("/events/{id}/purchase")
    public String purchaseTicket(@PathVariable Long id,
                                 @RequestParam(required = false) String seatNumber,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            User client = (User) authentication.getPrincipal();

            if (seatNumber == null || seatNumber.trim().isEmpty()) {
                seatNumber = "General Admission";
            }

            ticketService.purchaseTicket(event, client, seatNumber);
            redirectAttributes.addFlashAttribute("message",
                    "Ticket purchased successfully!");
            return "redirect:/client/tickets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to purchase ticket: " + e.getMessage());
            return "redirect:/client/events/" + id;
        }
    }

    @PostMapping("/events/{id}/reserve")
    public String reserveTicket(@PathVariable Long id,
                                @RequestParam(required = false) String seatNumber,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            User client = (User) authentication.getPrincipal();

            if (seatNumber == null || seatNumber.trim().isEmpty()) {
                seatNumber = "General Admission";
            }

            ticketService.reserveTicket(event, client, seatNumber);
            redirectAttributes.addFlashAttribute("message",
                    "Ticket reserved successfully! Please complete payment.");
            return "redirect:/client/tickets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to reserve ticket: " + e.getMessage());
            return "redirect:/client/events/" + id;
        }
    }

    @GetMapping("/tickets")
    public String tickets(Authentication authentication, Model model) {
        User client = (User) authentication.getPrincipal();
        var myTickets = ticketService.getTicketsByUser(client);

        List<TicketDto> ticketDtos = myTickets.stream()
                .map(TicketMapper::toDto)
                .toList();

        model.addAttribute("tickets", ticketDtos);
        model.addAttribute("currentPage", "client-tickets");
        return "client/tickets";
    }

    @PostMapping("/tickets/{id}/pay")
    public String payForTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.confirmPayment(id);
            redirectAttributes.addFlashAttribute("message", "Payment confirmed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to confirm payment: " + e.getMessage());
        }
        return "redirect:/client/tickets";
    }

    @PostMapping("/tickets/{id}/cancel")
    public String cancelTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.cancelTicket(id);
            redirectAttributes.addFlashAttribute("message", "Ticket cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel ticket: " + e.getMessage());
        }
        return "redirect:/client/tickets";
    }
}