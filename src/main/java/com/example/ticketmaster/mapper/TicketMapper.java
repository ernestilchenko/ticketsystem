package com.example.ticketmaster.mapper;

import com.example.ticketmaster.dto.TicketDto;
import com.example.ticketmaster.entity.Ticket;

public class TicketMapper {

    public static TicketDto toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setPrice(ticket.getPrice());
        dto.setSeatNumber(ticket.getSeatNumber());
        dto.setStatus(ticket.getStatus().name());
        dto.setPurchaseDate(ticket.getPurchaseDate());

        if (ticket.getEvent() != null) {
            dto.setEventId(ticket.getEvent().getId());
            dto.setEventName(ticket.getEvent().getName());
            dto.setEventDate(ticket.getEvent().getEventDate());
            dto.setEventLocation(ticket.getEvent().getLocation());
        }

        if (ticket.getUser() != null) {
            dto.setUserId(ticket.getUser().getId());
            dto.setUserName(ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName());
            dto.setUserEmail(ticket.getUser().getEmail());
        }

        return dto;
    }
}