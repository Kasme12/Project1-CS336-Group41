package dao;

import model.Airport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirportDAO {
    
    public List<Airport> getAllAirports() throws SQLException {
        List<Airport> airports = new ArrayList<>();
        String sql = "SELECT * FROM Airport ORDER BY city";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Airport airport = new Airport();
                airport.setAirportID(rs.getString("AirportID"));
                airport.setName(rs.getString("name"));
                airport.setCity(rs.getString("city"));
                airport.setCountry(rs.getString("country"));
                airports.add(airport);
            }
        }
        return airports;
    }
    
    public Airport getAirportByID(String airportID) throws SQLException {
        String sql = "SELECT * FROM Airport WHERE AirportID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airportID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Airport airport = new Airport();
                airport.setAirportID(rs.getString("AirportID"));
                airport.setName(rs.getString("name"));
                airport.setCity(rs.getString("city"));
                airport.setCountry(rs.getString("country"));
                return airport;
            }
        }
        return null;
    }
    
    public boolean addAirport(Airport airport) throws SQLException {
        String sql = "INSERT INTO Airport (AirportID, name, city, country) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airport.getAirportID());
            pstmt.setString(2, airport.getName());
            pstmt.setString(3, airport.getCity());
            pstmt.setString(4, airport.getCountry());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateAirport(Airport airport) throws SQLException {
        String sql = "UPDATE Airport SET name = ?, city = ?, country = ? WHERE AirportID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airport.getName());
            pstmt.setString(2, airport.getCity());
            pstmt.setString(3, airport.getCountry());
            pstmt.setString(4, airport.getAirportID());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAirport(String airportID) throws SQLException {
        String sql = "DELETE FROM Airport WHERE AirportID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airportID);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Airport> searchAirports(String keyword) throws SQLException {
        List<Airport> airports = new ArrayList<>();
        String sql = "SELECT * FROM Airport WHERE AirportID LIKE ? OR name LIKE ? OR city LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Airport airport = new Airport();
                airport.setAirportID(rs.getString("AirportID"));
                airport.setName(rs.getString("name"));
                airport.setCity(rs.getString("city"));
                airport.setCountry(rs.getString("country"));
                airports.add(airport);
            }
        }
        return airports;
    }
}