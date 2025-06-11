package com.example.ticketmaster.controller;

import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.Ticket;
import com.example.ticketmaster.service.EventService;
import com.example.ticketmaster.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SeatApiController {

    private final EventService eventService;
    private final TicketService ticketService;

    public SeatApiController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @GetMapping("/events/{eventId}/occupied-seats")
    public ResponseEntity<List<String>> getOccupiedSeats(@PathVariable Long eventId) {
        try {
            Event event = eventService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            List<Ticket> tickets = ticketService.getTicketsByEvent(event);
            
            // Pobierz tylko bilety, które nie są anulowane
            List<String> occupiedSeats = tickets.stream()
                    .filter(ticket -> ticket.getStatus() != Ticket.TicketStatus.CANCELLED)
                    .map(Ticket::getSeatNumber)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(occupiedSeats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/events/{eventId}/check-seat")
    public ResponseEntity<Boolean> checkSeatAvailability(@PathVariable Long eventId, 
                                                        @RequestBody String seatNumber) {
        try {
            Event event = eventService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            List<Ticket> tickets = ticketService.getTicketsByEvent(event);
            
            boolean isOccupied = tickets.stream()
                    .anyMatch(ticket -> ticket.getSeatNumber().equals(seatNumber) 
                             && ticket.getStatus() != Ticket.TicketStatus.CANCELLED);

            return ResponseEntity.ok(!isOccupied);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}