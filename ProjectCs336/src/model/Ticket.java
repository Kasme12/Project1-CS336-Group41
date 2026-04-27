package model;

import java.sql.Timestamp;

public class Ticket {
    private String ticketNumber;
    private int reservationID;
    private int customerID;
    private double totalFare;
    private double bookingFee;
    private Timestamp purchaseDatetime;
    private String ticketClass; // ECONOMY, BUSINESS, FIRST
    private String seatNumber;
    private String specialMeal;
    
    // Additional info
    private String customerName;
    private int flightInstanceID;
    private String flightInfo;

    public Ticket() {}

    public Ticket(String ticketNumber, int reservationID, int customerID, double totalFare,
                  double bookingFee, Timestamp purchaseDatetime, String ticketClass,
                  String seatNumber, String specialMeal) {
        this.ticketNumber = ticketNumber;
        this.reservationID = reservationID;
        this.customerID = customerID;
        this.totalFare = totalFare;
        this.bookingFee = bookingFee;
        this.purchaseDatetime = purchaseDatetime;
        this.ticketClass = ticketClass;
        this.seatNumber = seatNumber;
        this.specialMeal = specialMeal;
    }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public int getReservationID() { return reservationID; }
    public void setReservationID(int reservationID) { this.reservationID = reservationID; }
    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public double getTotalFare() { return totalFare; }
    public void setTotalFare(double totalFare) { this.totalFare = totalFare; }
    public double getBookingFee() { return bookingFee; }
    public void setBookingFee(double bookingFee) { this.bookingFee = bookingFee; }
    public Timestamp getPurchaseDatetime() { return purchaseDatetime; }
    public void setPurchaseDatetime(Timestamp purchaseDatetime) { this.purchaseDatetime = purchaseDatetime; }
    public String getTicketClass() { return ticketClass; }
    public void setTicketClass(String ticketClass) { this.ticketClass = ticketClass; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public String getSpecialMeal() { return specialMeal; }
    public void setSpecialMeal(String specialMeal) { this.specialMeal = specialMeal; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public int getFlightInstanceID() { return flightInstanceID; }
    public void setFlightInstanceID(int flightInstanceID) { this.flightInstanceID = flightInstanceID; }
    public String getFlightInfo() { return flightInfo; }
    public void setFlightInfo(String flightInfo) { this.flightInfo = flightInfo; }

    @Override
    public String toString() {
        return ticketNumber + " - " + ticketClass + " - $" + totalFare;
    }
}