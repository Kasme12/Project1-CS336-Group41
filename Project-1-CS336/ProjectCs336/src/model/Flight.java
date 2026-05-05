package model;

import java.sql.Time;
import java.sql.Date;

public class Flight {
    private String airlineID;
    private String flightNumber;
    private String departureAirportID;
    private String arrivalAirportID;
    private int aircraftID;
    private Time departureTime;
    private Time arrivalTime;
    private String daysOfWeek;
    private String type; // domestic or international
    
    // Additional info for display
    private String airlineName;
    private String departureAirportName;
    private String arrivalAirportName;
    private String aircraftModel;

    public Flight() {}

    public Flight(String airlineID, String flightNumber, String departureAirportID, 
                  String arrivalAirportID, int aircraftID, Time departureTime, Time arrivalTime,
                  String daysOfWeek, String type) {
        this.airlineID = airlineID;
        this.flightNumber = flightNumber;
        this.departureAirportID = departureAirportID;
        this.arrivalAirportID = arrivalAirportID;
        this.aircraftID = aircraftID;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.daysOfWeek = daysOfWeek;
        this.type = type;
    }

    // Getters and Setters
    public String getAirlineID() { return airlineID; }
    public void setAirlineID(String airlineID) { this.airlineID = airlineID; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getDepartureAirportID() { return departureAirportID; }
    public void setDepartureAirportID(String departureAirportID) { this.departureAirportID = departureAirportID; }
    public String getArrivalAirportID() { return arrivalAirportID; }
    public void setArrivalAirportID(String arrivalAirportID) { this.arrivalAirportID = arrivalAirportID; }
    public int getAircraftID() { return aircraftID; }
    public void setAircraftID(int aircraftID) { this.aircraftID = aircraftID; }
    public Time getDepartureTime() { return departureTime; }
    public void setDepartureTime(Time departureTime) { this.departureTime = departureTime; }
    public Time getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Time arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }
    public String getDepartureAirportName() { return departureAirportName; }
    public void setDepartureAirportName(String departureAirportName) { this.departureAirportName = departureAirportName; }
    public String getArrivalAirportName() { return arrivalAirportName; }
    public void setArrivalAirportName(String arrivalAirportName) { this.arrivalAirportName = arrivalAirportName; }
    public String getAircraftModel() { return aircraftModel; }
    public void setAircraftModel(String aircraftModel) { this.aircraftModel = aircraftModel; }

    public String getFullFlightNumber() {
        return airlineID + flightNumber;
    }

    @Override
    public String toString() {
        return airlineID + flightNumber + ": " + departureAirportID + " -> " + arrivalAirportID;
    }
}