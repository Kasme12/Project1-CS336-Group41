package dao;

import model.WaitlistEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitlistDAO {
    
    public boolean addToWaitlist(int customerID, int flightInstanceID) throws SQLException {
        String sql = "INSERT INTO waits_for (customerID, flight_instance_id, request_time) " +
                     "VALUES (?, ?, NOW())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            pstmt.setInt(2, flightInstanceID);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean removeFromWaitlist(int customerID, int flightInstanceID) throws SQLException {
        String sql = "DELETE FROM waits_for WHERE customerID = ? AND flight_instance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            pstmt.setInt(2, flightInstanceID);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<WaitlistEntry> getWaitlistByFlight(int flightInstanceID) throws SQLException {
        List<WaitlistEntry> entries = new ArrayList<>();
        String sql = "SELECT w.*, c.name as customer_name, " +
                     "fi.AirlineID, fi.flight_number, fi.departure_date, " +
                     "f.departure_AirportID, f.arrival_AirportID " +
                     "FROM waits_for w " +
                     "JOIN Customer c ON w.customerID = c.customerID " +
                     "JOIN Flight_Instance fi ON w.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Flight f ON fi.AirlineID = f.AirlineID AND fi.flight_number = f.flight_number " +
                     "WHERE w.flight_instance_id = ? " +
                     "ORDER BY w.request_time";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, flightInstanceID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                WaitlistEntry entry = new WaitlistEntry();
                entry.setCustomerID(rs.getInt("customerID"));
                entry.setFlightInstanceID(rs.getInt("flight_instance_id"));
                entry.setRequestTime(rs.getTimestamp("request_time"));
                entry.setCustomerName(rs.getString("customer_name"));
                
                String flightInfo = rs.getString("AirlineID") + rs.getString("flight_number") + 
                                   " on " + rs.getDate("departure_date") + " (" + 
                                   rs.getString("departure_AirportID") + " -> " + 
                                   rs.getString("arrival_AirportID") + ")";
                entry.setFlightInfo(flightInfo);
                
                entries.add(entry);
            }
        }
        return entries;
    }
    
    public boolean isOnWaitlist(int customerID, int flightInstanceID) throws SQLException {
        String sql = "SELECT * FROM waits_for WHERE customerID = ? AND flight_instance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            pstmt.setInt(2, flightInstanceID);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        }
    }
    
    public List<WaitlistEntry> getWaitlistByCustomer(int customerID) throws SQLException {
        List<WaitlistEntry> entries = new ArrayList<>();
        String sql = "SELECT w.*, c.name as customer_name, " +
                     "fi.AirlineID, fi.flight_number, fi.departure_date, " +
                     "f.departure_AirportID, f.arrival_AirportID " +
                     "FROM waits_for w " +
                     "JOIN Customer c ON w.customerID = c.customerID " +
                     "JOIN Flight_Instance fi ON w.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Flight f ON fi.AirlineID = f.AirlineID AND fi.flight_number = f.flight_number " +
                     "WHERE w.customerID = ? " +
                     "ORDER BY w.request_time";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                WaitlistEntry entry = new WaitlistEntry();
                entry.setCustomerID(rs.getInt("customerID"));
                entry.setFlightInstanceID(rs.getInt("flight_instance_id"));
                entry.setRequestTime(rs.getTimestamp("request_time"));
                entry.setCustomerName(rs.getString("customer_name"));
                
                String flightInfo = rs.getString("AirlineID") + rs.getString("flight_number") + 
                                   " on " + rs.getDate("departure_date") + " (" + 
                                   rs.getString("departure_AirportID") + " -> " + 
                                   rs.getString("arrival_AirportID") + ")";
                entry.setFlightInfo(flightInfo);
                
                entries.add(entry);
            }
        }
        return entries;
    }
}