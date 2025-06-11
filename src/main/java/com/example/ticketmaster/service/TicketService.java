package com.example.ticketmaster.service;

import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.Ticket;
import com.example.ticketmaster.entity.User;
import com.example.ticketmaster.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventService eventService;

    public TicketService(TicketRepository ticketRepository, EventService eventService) {
        this.ticketRepository = ticketRepository;
        this.eventService = eventService;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByUserOrderByPurchaseDateDesc(user);
    }

    public List<Ticket> getTicketsByEvent(Event event) {
        return ticketRepository.findByEvent(event);
    }

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket purchaseTicket(Event event, User user, String seatNumber) {
        // Sprawdź czy miejsce nie jest już zajęte
        if (isSeatOccupied(event, seatNumber)) {
            throw new RuntimeException("Seat " + seatNumber + " is already occupied");
        }

        // Sprawdź czy są dostępne miejsca
        if (!eventService.reserveSeat(event.getId())) {
            throw new RuntimeException("No available seats for this event");
        }

        try {
            Ticket ticket = new Ticket(event, user, event.getPrice(), seatNumber);
            ticket.setStatus(Ticket.TicketStatus.PAID);
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            // W przypadku błędu zwolnij miejsce
            eventService.releaseSeat(event.getId());
            throw new RuntimeException("Failed to purchase ticket: " + e.getMessage());
        }
    }

    public Ticket reserveTicket(Event event, User user, String seatNumber) {
        // Sprawdź czy miejsce nie jest już zajęte
        if (isSeatOccupied(event, seatNumber)) {
            throw new RuntimeException("Seat " + seatNumber + " is already occupied");
        }

        if (!eventService.reserveSeat(event.getId())) {
            throw new RuntimeException("No available seats for this event");
        }

        try {
            Ticket ticket = new Ticket(event, user, event.getPrice(), seatNumber);
            ticket.setStatus(Ticket.TicketStatus.RESERVED);
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            eventService.releaseSeat(event.getId());
            throw new RuntimeException("Failed to reserve ticket: " + e.getMessage());
        }
    }

    public Ticket confirmPayment(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() == Ticket.TicketStatus.RESERVED) {
            ticket.setStatus(Ticket.TicketStatus.PAID);
            return ticketRepository.save(ticket);
        }

        throw new RuntimeException("Ticket is not in reserved status");
    }

    public void cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() == Ticket.TicketStatus.RESERVED ||
                ticket.getStatus() == Ticket.TicketStatus.PAID) {

            ticket.setStatus(Ticket.TicketStatus.CANCELLED);
            ticketRepository.save(ticket);

            // Zwolnij miejsce w wydarzeniu
            eventService.releaseSeat(ticket.getEvent().getId());
        } else {
            throw new RuntimeException("Cannot cancel ticket with status: " + ticket.getStatus());
        }
    }

    public Ticket useTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() == Ticket.TicketStatus.PAID) {
            ticket.setStatus(Ticket.TicketStatus.USED);
            return ticketRepository.save(ticket);
        }

        throw new RuntimeException("Ticket must be paid to be used");
    }

    public void deleteTicket(Long id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            // Zwolnij miejsce jeśli bilet nie był anulowany
            if (ticket.get().getStatus() != Ticket.TicketStatus.CANCELLED) {
                eventService.releaseSeat(ticket.get().getEvent().getId());
            }
            ticketRepository.deleteById(id);
        }
    }
        // Dodaj te metody do TicketService.java

    public boolean isSeatOccupied(Event event, String seatNumber) {
        List<Ticket> tickets = ticketRepository.findByEvent(event);
        return tickets.stream()
                .anyMatch(ticket -> ticket.getSeatNumber().equals(seatNumber) 
                        && ticket.getStatus() != Ticket.TicketStatus.CANCELLED);
    }

    
}