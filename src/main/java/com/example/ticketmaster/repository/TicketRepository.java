package com.example.ticketmaster.repository;

import com.example.ticketmaster.entity.Event;
import com.example.ticketmaster.entity.Ticket;
import com.example.ticketmaster.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);
    List<Ticket> findByEvent(Event event);
    List<Ticket> findByUserOrderByPurchaseDateDesc(User user);
}