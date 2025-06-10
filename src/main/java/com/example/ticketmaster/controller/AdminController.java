package com.example.ticketmaster.controller;

import com.example.ticketmaster.dto.UserDto;
import com.example.ticketmaster.mapper.UserMapper;
import com.example.ticketmaster.service.EventService;
import com.example.ticketmaster.service.TicketService;
import com.example.ticketmaster.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    public AdminController(UserService userService, EventService eventService, TicketService ticketService) {
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var pendingEventsList = eventService.getPendingEvents();

        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("pendingEvents", pendingEventsList.size());
        model.addAttribute("pendingEventsList", pendingEventsList);
        model.addAttribute("totalTickets", ticketService.getAllTickets().size());
        model.addAttribute("currentPage", "admin-dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        var users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(UserMapper::toDto)
                .toList();

        model.addAttribute("users", userDtos);
        model.addAttribute("currentPage", "admin-users");
        return "admin/users";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("pendingEvents", eventService.getPendingEvents());
        model.addAttribute("currentPage", "admin-events");
        return "admin/events";
    }

    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.approveEvent(id);
            redirectAttributes.addFlashAttribute("message", "Event approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.rejectEvent(id);
            redirectAttributes.addFlashAttribute("message", "Event rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("message", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}