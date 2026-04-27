package model;

public class Airport {
    private String airportID;
    private String name;
    private String city;
    private String country;

    public Airport() {}

    public Airport(String airportID, String name, String city, String country) {
        this.airportID = airportID;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public String getAirportID() { return airportID; }
    public void setAirportID(String airportID) { this.airportID = airportID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() { return airportID + " - " + city; }
}