package dao;

import model.Reservation;
import model.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    
    public int createReservation(int customerID) throws SQLException {
        String sql = "INSERT INTO Reservation (customerID, created_at, status) VALUES (?, NOW(), 'CONFIRMED')";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, customerID);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    public boolean updateReservationStatus(int reservationID, String status) throws SQLException {
        String sql = "UPDATE Reservation SET status = ? WHERE reservationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, reservationID);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public Reservation getReservationByID(int reservationID) throws SQLException {
        String sql = "SELECT r.*, c.name as customer_name, " +
                     "(SELECT COUNT(*) FROM Ticket t WHERE t.reservationID = r.reservationID) as ticket_count, " +
                     "(SELECT SUM(total_fare + booking_fee) FROM Ticket t WHERE t.reservationID = r.reservationID) as total_fare " +
                     "FROM Reservation r " +
                     "JOIN Customer c ON r.customerID = c.customerID " +
                     "WHERE r.reservationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reservationID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return parseReservation(rs);
            }
        }
        return null;
    }
    
    public List<Reservation> getReservationsByCustomer(int customerID) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.name as customer_name, " +
                     "(SELECT COUNT(*) FROM Ticket t WHERE t.reservationID = r.reservationID) as ticket_count, " +
                     "(SELECT SUM(total_fare + booking_fee) FROM Ticket t WHERE t.reservationID = r.reservationID) as total_fare " +
                     "FROM Reservation r " +
                     "JOIN Customer c ON r.customerID = c.customerID " +
                     "WHERE r.customerID = ? " +
                     "ORDER BY r.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(parseReservation(rs));
            }
        }
        return reservations;
    }
    
    public List<Reservation> getReservationsByFlight(String airlineID, String flightNumber, Date departureDate) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT DISTINCT r.*, c.name as customer_name, " +
                     "(SELECT COUNT(*) FROM Ticket t WHERE t.reservationID = r.reservationID) as ticket_count, " +
                     "(SELECT SUM(total_fare + booking_fee) FROM Ticket t WHERE t.reservationID = r.reservationID) as total_fare " +
                     "FROM Reservation r " +
                     "JOIN Ticket t ON r.reservationID = t.reservationID " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Customer c ON r.customerID = c.customerID " +
                     "WHERE fi.AirlineID = ? AND fi.flight_number = ? AND fi.departure_date = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            pstmt.setString(2, flightNumber);
            pstmt.setDate(3, departureDate);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservations.add(parseReservation(rs));
            }
        }
        return reservations;
    }

    public List<Reservation> getReservationsByFlightNumber(String airlineID, String flightNumber) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT DISTINCT r.*, c.name as customer_name, " +
                     "(SELECT COUNT(*) FROM Ticket t WHERE t.reservationID = r.reservationID) as ticket_count, " +
                     "(SELECT SUM(total_fare + booking_fee) FROM Ticket t WHERE t.reservationID = r.reservationID) as total_fare " +
                     "FROM Reservation r " +
                     "JOIN Ticket t ON r.reservationID = t.reservationID " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Customer c ON r.customerID = c.customerID " +
                     "WHERE fi.AirlineID = ? AND fi.flight_number = ? " +
                     "ORDER BY r.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, airlineID);
            pstmt.setString(2, flightNumber);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reservations.add(parseReservation(rs));
            }
        }
        return reservations;
    }
    
    public List<Reservation> getReservationsByCustomerName(String customerName) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.name as customer_name, " +
                     "(SELECT COUNT(*) FROM Ticket t WHERE t.reservationID = r.reservationID) as ticket_count, " +
                     "(SELECT SUM(total_fare + booking_fee) FROM Ticket t WHERE t.reservationID = r.reservationID) as total_fare " +
                     "FROM Reservation r " +
                     "JOIN Customer c ON r.customerID = c.customerID " +
                     "WHERE c.name LIKE ? " +
                     "ORDER BY r.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + customerName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(parseReservation(rs));
            }
        }
        return reservations;
    }
    
    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.name as customer_name, " +
                     "(SELECT COUNT(*) FROM Ticket t WHERE t.reservationID = r.reservationID) as ticket_count, " +
                     "(SELECT SUM(total_fare + booking_fee) FROM Ticket t WHERE t.reservationID = r.reservationID) as total_fare " +
                     "FROM Reservation r " +
                     "JOIN Customer c ON r.customerID = c.customerID " +
                     "ORDER BY r.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(parseReservation(rs));
            }
        }
        return reservations;
    }
    
    private Reservation parseReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationID(rs.getInt("reservationID"));
        reservation.setCustomerID(rs.getInt("customerID"));
        reservation.setCreatedAt(rs.getTimestamp("created_at"));
        reservation.setStatus(rs.getString("status"));
        reservation.setCustomerName(rs.getString("customer_name"));
        reservation.setTicketCount(rs.getInt("ticket_count"));
        reservation.setTotalFare(rs.getDouble("total_fare"));
        return reservation;
    }
}