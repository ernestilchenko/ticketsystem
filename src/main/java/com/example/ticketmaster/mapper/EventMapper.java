package com.example.ticketmaster.mapper;

import com.example.ticketmaster.dto.CreateEventDto;
import com.example.ticketmaster.dto.EventDto;
import com.example.ticketmaster.entity.Event;

import java.util.List;

public class EventMapper {

    public static Event toEntity(EventDto dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setPrice(dto.getPrice());
        event.setAvailableSeats(dto.getAvailableSeats());
        event.setTotalSeats(dto.getTotalSeats());
        event.setCategory(dto.getCategory());
        event.setStatus(dto.getStatus());
        event.setCreatedAt(dto.getCreatedAt());
        event.setUpdatedAt(dto.getUpdatedAt());
        return event;
    }

    public static EventDto toDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setPrice(event.getPrice());
        dto.setAvailableSeats(event.getAvailableSeats());
        dto.setTotalSeats(event.getTotalSeats());
        dto.setCategory(event.getCategory());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        if (event.getOrganizer() != null) {
            dto.setOrganizerName(event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName());
        }
        return dto;
    }

    public static Event toEntity(CreateEventDto dto) {
        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setPrice(dto.getPrice());
        event.setTotalSeats(dto.getTotalSeats());
        event.setCategory(Event.EventCategory.valueOf(dto.getCategory()));
        return event;
    }

    public static CreateEventDto toCreateDto(Event event) {
        CreateEventDto dto = new CreateEventDto();
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setPrice(event.getPrice());
        dto.setTotalSeats(event.getTotalSeats());
        dto.setCategory(event.getCategory().name());
        return dto;
    }

    public static List<EventDto> toDtoList(List<Event> events) {
        return events.stream().map(EventMapper::toDto).toList();
    }
}