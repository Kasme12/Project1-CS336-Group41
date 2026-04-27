package dao;

import model.FlightInstance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightInstanceDAO {
    
    public int createFlightInstance(FlightInstance instance) throws SQLException {
        String sql = "INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, " +
                     "actual_departure_time, actual_arrival_time) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, instance.getAirlineID());
            pstmt.setString(2, instance.getFlightNumber());
            pstmt.setDate(3, instance.getDepartureDate());
            pstmt.setTime(4, instance.getActualDepartureTime());
            pstmt.setTime(5, instance.getActualArrivalTime());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    public FlightInstance getFlightInstanceByID(int flightInstanceID) throws SQLException {
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
                     "WHERE fi.flight_instance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, flightInstanceID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return parseFlightInstance(rs);
            }
        }
        return null;
    }
    
    public List<FlightInstance> getFlightInstancesByDate(Date departureDate) throws SQLException {
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
                     "WHERE fi.departure_date = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, departureDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                instances.add(parseFlightInstance(rs));
            }
        }
        return instances;
    }
    
    public List<FlightInstance> getFlightInstancesByAirport(String airportID, Date startDate, Date endDate) throws SQLException {
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
                     "WHERE (f.departure_AirportID = ? OR f.arrival_AirportID = ?) " +
                     "AND fi.departure_date BETWEEN ? AND ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airportID);
            pstmt.setString(2, airportID);
            pstmt.setDate(3, startDate);
            pstmt.setDate(4, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                instances.add(parseFlightInstance(rs));
            }
        }
        return instances;
    }
    
    public boolean updateFlightInstance(FlightInstance instance) throws SQLException {
        String sql = "UPDATE Flight_Instance SET departure_date = ?, " +
                     "actual_departure_time = ?, actual_arrival_time = ? " +
                     "WHERE flight_instance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, instance.getDepartureDate());
            pstmt.setTime(2, instance.getActualDepartureTime());
            pstmt.setTime(3, instance.getActualArrivalTime());
            pstmt.setInt(4, instance.getFlightInstanceID());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteFlightInstance(int flightInstanceID) throws SQLException {
        String sql = "DELETE FROM Flight_Instance WHERE flight_instance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, flightInstanceID);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private FlightInstance parseFlightInstance(ResultSet rs) throws SQLException {
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
        return instance;
    }
}