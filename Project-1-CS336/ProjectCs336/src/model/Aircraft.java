package model;

public class Aircraft {
    private int aircraftID;
    private String model;
    private int capacity;
    private String airlineID;

    public Aircraft() {}

    public Aircraft(int aircraftID, String model, int capacity, String airlineID) {
        this.aircraftID = aircraftID;
        this.model = model;
        this.capacity = capacity;
        this.airlineID = airlineID;
    }

    public int getAircraftID() { return aircraftID; }
    public void setAircraftID(int aircraftID) { this.aircraftID = aircraftID; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getAirlineID() { return airlineID; }
    public void setAirlineID(String airlineID) { this.airlineID = airlineID; }

    @Override
    public String toString() { return model + " (Capacity: " + capacity + ")"; }
}