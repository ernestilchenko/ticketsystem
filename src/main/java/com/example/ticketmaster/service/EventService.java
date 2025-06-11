package com.example.ticketmaster.service;

import com.example.ticketmaster.dto.CreateEventDto;
import com.example.ticketmaster.dto.EventDto;
import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.User;
import com.example.ticketmaster.mapper.EventMapper;
import com.example.ticketmaster.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<EventDto> getAllEventsDto() {
        return EventMapper.toDtoList(eventRepository.findAll());
    }

    public List<Event> getAvailableEvents() {
        return eventRepository.findAvailableEvents(LocalDateTime.now());
    }

    public List<EventDto> getAvailableEventsDto() {
        return EventMapper.toDtoList(eventRepository.findAvailableEvents(LocalDateTime.now()));
    }

    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    public List<EventDto> getEventsByOrganizerDto(User organizer) {
        return EventMapper.toDtoList(eventRepository.findByOrganizer(organizer));
    }

    public List<Event> getPendingEvents() {
        return eventRepository.findByStatus(Event.EventStatus.PENDING_APPROVAL);
    }

    public List<EventDto> getPendingEventsDto() {
        return EventMapper.toDtoList(eventRepository.findByStatus(Event.EventStatus.PENDING_APPROVAL));
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public Optional<EventDto> findByIdDto(Long id) {
        return eventRepository.findById(id).map(EventMapper::toDto);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event createEvent(Event event, User organizer) {
        logger.debug("Creating event: {} for organizer: {}", event.getName(), organizer.getUsername());

        event.setOrganizer(organizer);
        event.setAvailableSeats(event.getTotalSeats());
        event.setStatus(Event.EventStatus.PENDING_APPROVAL);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        logger.debug("Event details: name={}, category={}, totalSeats={}, price={}, date={}",
                event.getName(), event.getCategory(), event.getTotalSeats(),
                event.getPrice(), event.getEventDate());

        Event savedEvent = eventRepository.save(event);
        logger.info("Event saved with ID: {}", savedEvent.getId());

        return savedEvent;
    }

    public EventDto createEventDto(CreateEventDto createEventDto, User organizer) {
        Event event = EventMapper.toEntity(createEventDto);
        Event savedEvent = createEvent(event, organizer);
        return EventMapper.toDto(savedEvent);
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
        if (event.getOrganizer() == null) {
            return user.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        }

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

        if (event.getTotalSeats() > existingEvent.getTotalSeats()) {
            int difference = event.getTotalSeats() - existingEvent.getTotalSeats();
            existingEvent.setAvailableSeats(existingEvent.getAvailableSeats() + difference);
        }
        existingEvent.setTotalSeats(event.getTotalSeats());
        existingEvent.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(existingEvent);
    }

    public EventDto updateEventDto(Long id, CreateEventDto createEventDto) {
        Event event = EventMapper.toEntity(createEventDto);
        event.setId(id);
        Event updatedEvent = updateEvent(event);
        return EventMapper.toDto(updatedEvent);
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