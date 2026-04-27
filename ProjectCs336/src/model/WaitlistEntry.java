package model;

import java.sql.Timestamp;

public class WaitlistEntry {
    private int customerID;
    private int flightInstanceID;
    private Timestamp requestTime;
    
    // Additional info
    private String customerName;
    private String flightInfo;

    public WaitlistEntry() {}

    public WaitlistEntry(int customerID, int flightInstanceID, Timestamp requestTime) {
        this.customerID = customerID;
        this.flightInstanceID = flightInstanceID;
        this.requestTime = requestTime;
    }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public int getFlightInstanceID() { return flightInstanceID; }
    public void setFlightInstanceID(int flightInstanceID) { this.flightInstanceID = flightInstanceID; }
    public Timestamp getRequestTime() { return requestTime; }
    public void setRequestTime(Timestamp requestTime) { this.requestTime = requestTime; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getFlightInfo() { return flightInfo; }
    public void setFlightInfo(String flightInfo) { this.flightInfo = flightInfo; }
}