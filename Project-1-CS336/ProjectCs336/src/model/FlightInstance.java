package model;

import java.sql.Date;
import java.sql.Time;

public class FlightInstance {
    private int flightInstanceID;
    private String airlineID;
    private String flightNumber;
    private Date departureDate;
    private Time actualDepartureTime;
    private Time actualArrivalTime;
    
    // Additional info
    private String fullFlightNumber;
    private String departureAirportID;
    private String arrivalAirportID;
    private int availableSeats;

    public FlightInstance() {}

    public FlightInstance(int flightInstanceID, String airlineID, String flightNumber, 
                          Date departureDate, Time actualDepartureTime, Time actualArrivalTime) {
        this.flightInstanceID = flightInstanceID;
        this.airlineID = airlineID;
        this.flightNumber = flightNumber;
        this.departureDate = departureDate;
        this.actualDepartureTime = actualDepartureTime;
        this.actualArrivalTime = actualArrivalTime;
    }

    public int getFlightInstanceID() { return flightInstanceID; }
    public void setFlightInstanceID(int flightInstanceID) { this.flightInstanceID = flightInstanceID; }
    public String getAirlineID() { return airlineID; }
    public void setAirlineID(String airlineID) { this.airlineID = airlineID; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public Date getDepartureDate() { return departureDate; }
    public void setDepartureDate(Date departureDate) { this.departureDate = departureDate; }
    public Time getActualDepartureTime() { return actualDepartureTime; }
    public void setActualDepartureTime(Time actualDepartureTime) { this.actualDepartureTime = actualDepartureTime; }
    public Time getActualArrivalTime() { return actualArrivalTime; }
    public void setActualArrivalTime(Time actualArrivalTime) { this.actualArrivalTime = actualArrivalTime; }
    public String getFullFlightNumber() { return fullFlightNumber; }
    public void setFullFlightNumber(String fullFlightNumber) { this.fullFlightNumber = fullFlightNumber; }
    public String getDepartureAirportID() { return departureAirportID; }
    public void setDepartureAirportID(String departureAirportID) { this.departureAirportID = departureAirportID; }
    public String getArrivalAirportID() { return arrivalAirportID; }
    public void setArrivalAirportID(String arrivalAirportID) { this.arrivalAirportID = arrivalAirportID; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    @Override
    public String toString() {
        return fullFlightNumber + " on " + departureDate;
    }
}