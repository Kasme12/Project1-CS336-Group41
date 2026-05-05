package dao;

import model.Airline;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirlineDAO {
    
    public List<Airline> getAllAirlines() throws SQLException {
        List<Airline> airlines = new ArrayList<>();
        String sql = "SELECT * FROM Airline";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Airline airline = new Airline();
                airline.setAirlineID(rs.getString("AirlineID"));
                airline.setName(rs.getString("name"));
                airlines.add(airline);
            }
        }
        return airlines;
    }
    
    public Airline getAirlineByID(String airlineID) throws SQLException {
        String sql = "SELECT * FROM Airline WHERE AirlineID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Airline airline = new Airline();
                airline.setAirlineID(rs.getString("AirlineID"));
                airline.setName(rs.getString("name"));
                return airline;
            }
        }
        return null;
    }
    
    public boolean addAirline(Airline airline) throws SQLException {
        String sql = "INSERT INTO Airline (AirlineID, name) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airline.getAirlineID());
            pstmt.setString(2, airline.getName());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateAirline(Airline airline) throws SQLException {
        String sql = "UPDATE Airline SET name = ? WHERE AirlineID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airline.getName());
            pstmt.setString(2, airline.getAirlineID());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAirline(String airlineID) throws SQLException {
        String sql = "DELETE FROM Airline WHERE AirlineID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            return pstmt.executeUpdate() > 0;
        }
    }
}