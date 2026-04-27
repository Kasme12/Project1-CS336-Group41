package dao;

import model.Aircraft;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AircraftDAO {
    
    public List<Aircraft> getAllAircraft() throws SQLException {
        List<Aircraft> aircrafts = new ArrayList<>();
        String sql = "SELECT a.*, al.name as airline_name FROM Aircraft a " +
                     "JOIN Airline al ON a.AirlineID = al.AirlineID";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Aircraft aircraft = new Aircraft();
                aircraft.setAircraftID(rs.getInt("aircraftID"));
                aircraft.setModel(rs.getString("model"));
                aircraft.setCapacity(rs.getInt("capacity"));
                aircraft.setAirlineID(rs.getString("AirlineID"));
                aircrafts.add(aircraft);
            }
        }
        return aircrafts;
    }
    
    public Aircraft getAircraftByID(int aircraftID) throws SQLException {
        String sql = "SELECT * FROM Aircraft WHERE aircraftID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, aircraftID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Aircraft aircraft = new Aircraft();
                aircraft.setAircraftID(rs.getInt("aircraftID"));
                aircraft.setModel(rs.getString("model"));
                aircraft.setCapacity(rs.getInt("capacity"));
                aircraft.setAirlineID(rs.getString("AirlineID"));
                return aircraft;
            }
        }
        return null;
    }
    
    public boolean addAircraft(Aircraft aircraft) throws SQLException {
        String sql = "INSERT INTO Aircraft (model, capacity, AirlineID) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, aircraft.getModel());
            pstmt.setInt(2, aircraft.getCapacity());
            pstmt.setString(3, aircraft.getAirlineID());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateAircraft(Aircraft aircraft) throws SQLException {
        String sql = "UPDATE Aircraft SET model = ?, capacity = ?, AirlineID = ? WHERE aircraftID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, aircraft.getModel());
            pstmt.setInt(2, aircraft.getCapacity());
            pstmt.setString(3, aircraft.getAirlineID());
            pstmt.setInt(4, aircraft.getAircraftID());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAircraft(int aircraftID) throws SQLException {
        String sql = "DELETE FROM Aircraft WHERE aircraftID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, aircraftID);
            return pstmt.executeUpdate() > 0;
        }
    }
}