package com.example.ticketmaster.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketDto {
    private Long id;
    private String ticketNumber;
    private Long eventId;
    private String eventName;
    private LocalDateTime eventDate;
    private String eventLocation;
    private Long userId;
    private String userName;
    private String userEmail;
    private BigDecimal price;
    private String seatNumber;
    private String status;
    private LocalDateTime purchaseDate;

    public TicketDto() {
    }

    public TicketDto(Long id, String ticketNumber, Long eventId, String eventName, LocalDateTime eventDate,
                     String eventLocation, Long userId, String userName, String userEmail, BigDecimal price,
                     String seatNumber, String status, LocalDateTime purchaseDate) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.price = price;
        this.seatNumber = seatNumber;
        this.status = status;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}