package com.example.ticketmaster.repository;

import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(Event.EventStatus status);
    List<Event> findByOrganizer(User organizer);
    List<Event> findByEventDateAfter(LocalDateTime date);
    List<Event> findByStatusAndEventDateAfter(Event.EventStatus status, LocalDateTime date);

    @Query("SELECT e FROM Event e WHERE e.status = 'APPROVED' AND e.eventDate > :now AND e.availableSeats > 0")
    List<Event> findAvailableEvents(LocalDateTime now);
}