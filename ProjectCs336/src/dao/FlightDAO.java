package dao;

import java.util.Calendar;
import model.Flight;
import model.FlightInstance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {
    
    public List<Flight> getAllFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT f.*, a.name as airline_name, " +
                     "dep.name as dep_airport_name, arr.name as arr_airport_name, " +
                     "ac.model as aircraft_model " +
                     "FROM Flight f " +
                     "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                     "JOIN Airport dep ON f.departure_AirportID = dep.AirportID " +
                     "JOIN Airport arr ON f.arrival_AirportID = arr.AirportID " +
                     "JOIN Aircraft ac ON f.aircraftID = ac.aircraftID";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            flights = parseFlights(rs);
        }
        return flights;
    }
    
    public List<Flight> searchFlights(String fromAirport, String toAirport, 
                                       Date departureDate, boolean flexible) throws SQLException {
        List<Flight> flights = new ArrayList<>();
        
        // Calculate date range for flexible dates
        Date startDate = departureDate;
        Date endDate = departureDate;
        
        if (flexible) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(departureDate);
            cal.add(Calendar.DAY_OF_MONTH, -3);
            startDate = new Date(cal.getTimeInMillis());
            cal.add(Calendar.DAY_OF_MONTH, 6);
            endDate = new Date(cal.getTimeInMillis());
        }
        
        String sql = "SELECT f.*, a.name as airline_name, " +
                     "dep.name as dep_airport_name, arr.name as arr_airport_name, " +
                     "ac.model as aircraft_model, fi.flight_instance_id, fi.departure_date, " +
                     "fi.actual_departure_time, fi.actual_arrival_time, " +
                     "(ac.capacity - COALESCE((SELECT COUNT(*) FROM includes i " +
                     "JOIN Ticket t ON i.ticket_number = t.ticket_number " +
                     "JOIN Reservation r ON t.reservationID = r.reservationID " +
                     "WHERE i.flight_instance_id = fi.flight_instance_id " +
                     "AND r.status = 'CONFIRMED'), 0)) as available_seats " +
                     "FROM Flight f " +
                     "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                     "JOIN Airport dep ON f.departure_AirportID = dep.AirportID " +
                     "JOIN Airport arr ON f.arrival_AirportID = arr.AirportID " +
                     "JOIN Aircraft ac ON f.aircraftID = ac.aircraftID " +
                     "LEFT JOIN Flight_Instance fi ON f.AirlineID = fi.AirlineID " +
                     "AND f.flight_number = fi.flight_number " +
                     "AND fi.departure_date BETWEEN ? AND ? " +
                     "WHERE f.departure_AirportID = ? AND f.arrival_AirportID = ? " +
                     "ORDER BY fi.departure_date, f.departure_time";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            pstmt.setString(3, fromAirport);
            pstmt.setString(4, toAirport);
            
            ResultSet rs = pstmt.executeQuery();
            flights = parseFlights(rs);
        }
        return flights;
    }
    
    public List<FlightInstance> getFlightInstances(String airlineID, String flightNumber, 
                                                     Date departureDate) throws SQLException {
        List<FlightInstance> instances = new ArrayList<>();
        String sql = "SELECT fi.*, f.departure_AirportID, f.arrival_AirportID, " +
                     "ac.capacity, " +
                     "(ac.capacity - COALESCE((SELECT COUNT(*) FROM includes i " +
                     "JOIN Ticket t ON i.ticket_number = t.ticket_number " +
                     "JOIN Reservation r ON t.reservationID = r.reservationID " +
                     "WHERE i.flight_instance_id = fi.flight_instance_id " +
                     "AND r.status = 'CONFIRMED'), 0)) as available_seats " +
                     "FROM Flight_Instance fi " +
                     "JOIN Flight f ON fi.AirlineID = f.AirlineID AND fi.flight_number = f.flight_number " +
                     "JOIN Aircraft ac ON f.aircraftID = ac.aircraftID " +
                     "WHERE fi.AirlineID = ? AND fi.flight_number = ? AND fi.departure_date = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            pstmt.setString(2, flightNumber);
            pstmt.setDate(3, departureDate);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                FlightInstance instance = new FlightInstance();
                instance.setFlightInstanceID(rs.getInt("flight_instance_id"));
                instance.setAirlineID(rs.getString("AirlineID"));
                instance.setFlightNumber(rs.getString("flight_number"));
                instance.setDepartureDate(rs.getDate("departure_date"));
                instance.setActualDepartureTime(rs.getTime("actual_departure_time"));
                instance.setActualArrivalTime(rs.getTime("actual_arrival_time"));
                instance.setFullFlightNumber(rs.getString("AirlineID") + rs.getString("flight_number"));
                instance.setDepartureAirportID(rs.getString("departure_AirportID"));
                instance.setArrivalAirportID(rs.getString("arrival_AirportID"));
                instance.setAvailableSeats(rs.getInt("available_seats"));
                instances.add(instance);
            }
        }
        return instances;
    }
    
    public boolean addFlight(Flight flight) throws SQLException {
        String sql = "INSERT INTO Flight (AirlineID, flight_number, departure_AirportID, " +
                     "arrival_AirportID, aircraftID, departure_time, arrival_time, " +
                     "days_of_week, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flight.getAirlineID());
            pstmt.setString(2, flight.getFlightNumber());
            pstmt.setString(3, flight.getDepartureAirportID());
            pstmt.setString(4, flight.getArrivalAirportID());
            pstmt.setInt(5, flight.getAircraftID());
            pstmt.setTime(6, flight.getDepartureTime());
            pstmt.setTime(7, flight.getArrivalTime());
            pstmt.setString(8, flight.getDaysOfWeek());
            pstmt.setString(9, flight.getType());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateFlight(Flight flight) throws SQLException {
        String sql = "UPDATE Flight SET departure_AirportID = ?, arrival_AirportID = ?, " +
                     "aircraftID = ?, departure_time = ?, arrival_time = ?, " +
                     "days_of_week = ?, type = ? WHERE AirlineID = ? AND flight_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flight.getDepartureAirportID());
            pstmt.setString(2, flight.getArrivalAirportID());
            pstmt.setInt(3, flight.getAircraftID());
            pstmt.setTime(4, flight.getDepartureTime());
            pstmt.setTime(5, flight.getArrivalTime());
            pstmt.setString(6, flight.getDaysOfWeek());
            pstmt.setString(7, flight.getType());
            pstmt.setString(8, flight.getAirlineID());
            pstmt.setString(9, flight.getFlightNumber());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteFlight(String airlineID, String flightNumber) throws SQLException {
        String sql = "DELETE FROM Flight WHERE AirlineID = ? AND flight_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            pstmt.setString(2, flightNumber);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Flight> getFlightsByAirport(String airportID) throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT f.*, a.name as airline_name, " +
                     "dep.name as dep_airport_name, arr.name as arr_airport_name " +
                     "FROM Flight f " +
                     "JOIN Airline a ON f.AirlineID = a.AirlineID " +
                     "JOIN Airport dep ON f.departure_AirportID = dep.AirportID " +
                     "JOIN Airport arr ON f.arrival_AirportID = arr.AirportID " +
                     "WHERE f.departure_AirportID = ? OR f.arrival_AirportID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airportID);
            pstmt.setString(2, airportID);
            
            ResultSet rs = pstmt.executeQuery();
            flights = parseFlights(rs);
        }
        return flights;
    }
    
    private List<Flight> parseFlights(ResultSet rs) throws SQLException {
        List<Flight> flights = new ArrayList<>();
        while (rs.next()) {
            Flight flight = new Flight();
            flight.setAirlineID(rs.getString("AirlineID"));
            flight.setFlightNumber(rs.getString("flight_number"));
            flight.setDepartureAirportID(rs.getString("departure_AirportID"));
            flight.setArrivalAirportID(rs.getString("arrival_AirportID"));
            flight.setAircraftID(rs.getInt("aircraftID"));
            flight.setDepartureTime(rs.getTime("departure_time"));
            flight.setArrivalTime(rs.getTime("arrival_time"));
            flight.setDaysOfWeek(rs.getString("days_of_week"));
            flight.setType(rs.getString("type"));
            
            // Additional info
            try {
                flight.setAirlineName(rs.getString("airline_name"));
                flight.setDepartureAirportName(rs.getString("dep_airport_name"));
                flight.setArrivalAirportName(rs.getString("arr_airport_name"));
                flight.setAircraftModel(rs.getString("aircraft_model"));
            } catch (SQLException e) {
                // Additional columns not present
            }
            
            flights.add(flight);
        }
        return flights;
    }
}