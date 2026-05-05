package model;

import java.sql.Timestamp;

public class Reservation {
    private int reservationID;
    private int customerID;
    private Timestamp createdAt;
    private String status; // CONFIRMED, CANCELLED, WAITLISTED
    
    // Additional info
    private String customerName;
    private int ticketCount;
    private double totalFare;

    public Reservation() {}

    public Reservation(int reservationID, int customerID, Timestamp createdAt, String status) {
        this.reservationID = reservationID;
        this.customerID = customerID;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getReservationID() { return reservationID; }
    public void setReservationID(int reservationID) { this.reservationID = reservationID; }
    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public int getTicketCount() { return ticketCount; }
    public void setTicketCount(int ticketCount) { this.ticketCount = ticketCount; }
    public double getTotalFare() { return totalFare; }
    public void setTotalFare(double totalFare) { this.totalFare = totalFare; }

    @Override
    public String toString() {
        return "Reservation #" + reservationID + " - " + status;
    }
}