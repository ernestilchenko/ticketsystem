package com.example.ticketmaster.service;

import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.User;
import com.example.ticketmaster.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAvailableEvents() {
        return eventRepository.findAvailableEvents(LocalDateTime.now());
    }

    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    public List<Event> getPendingEvents() {
        return eventRepository.findByStatus(Event.EventStatus.PENDING_APPROVAL);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event createEvent(Event event, User organizer) {
        event.setOrganizer(organizer);
        event.setAvailableSeats(event.getTotalSeats());
        event.setStatus(Event.EventStatus.PENDING_APPROVAL);
        return eventRepository.save(event);
    }

    public Event approveEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(Event.EventStatus.APPROVED);
        return eventRepository.save(event);
    }

    public Event rejectEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(Event.EventStatus.REJECTED);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public boolean canUserModifyEvent(Event event, User user) {
        return event.getOrganizer().getId().equals(user.getId())
                || user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public Event updateEvent(Event event) {
        Event existingEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        existingEvent.setName(event.getName());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setPrice(event.getPrice());
        existingEvent.setCategory(event.getCategory());

        // Aktualizacja dostępnych miejsc tylko jeśli zwiększamy całkowitą liczbę
        if (event.getTotalSeats() > existingEvent.getTotalSeats()) {
            int difference = event.getTotalSeats() - existingEvent.getTotalSeats();
            existingEvent.setAvailableSeats(existingEvent.getAvailableSeats() + difference);
        }
        existingEvent.setTotalSeats(event.getTotalSeats());

        return eventRepository.save(existingEvent);
    }

    public boolean reserveSeat(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() > 0) {
            event.setAvailableSeats(event.getAvailableSeats() - 1);
            eventRepository.save(event);
            return true;
        }
        return false;
    }

    public void releaseSeat(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() < event.getTotalSeats()) {
            event.setAvailableSeats(event.getAvailableSeats() + 1);
            eventRepository.save(event);
        }
    }
}