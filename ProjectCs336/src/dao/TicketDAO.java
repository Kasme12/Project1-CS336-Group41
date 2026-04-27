package dao;

import model.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketDAO {
    
    public String createTicket(Ticket ticket, int flightInstanceID) throws SQLException {
        String ticketNumber = "TK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert ticket
            String sql = "INSERT INTO Ticket (ticket_number, reservationID, customerID, total_fare, " +
                         "booking_fee, purchase_datetime, class, seat_number, special_meal) " +
                         "VALUES (?, ?, ?, ?, ?, NOW(), ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ticketNumber);
                pstmt.setInt(2, ticket.getReservationID());
                pstmt.setInt(3, ticket.getCustomerID());
                pstmt.setDouble(4, ticket.getTotalFare());
                pstmt.setDouble(5, ticket.getBookingFee());
                pstmt.setString(6, ticket.getTicketClass());
                pstmt.setString(7, ticket.getSeatNumber());
                pstmt.setString(8, ticket.getSpecialMeal());
                pstmt.executeUpdate();
            }
            
            // Insert into includes table
            String sql2 = "INSERT INTO includes (ticket_number, flight_instance_id, sequence_number) " +
                         "VALUES (?, ?, 1)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setString(1, ticketNumber);
                pstmt.setInt(2, flightInstanceID);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return ticketNumber;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public Ticket getTicketByID(String ticketNumber) throws SQLException {
        String sql = "SELECT t.*, c.name as customer_name, " +
                     "fi.flight_instance_id, fi.AirlineID, fi.flight_number, fi.departure_date, " +
                     "f.departure_AirportID, f.arrival_AirportID " +
                     "FROM Ticket t " +
                     "JOIN Customer c ON t.customerID = c.customerID " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Flight f ON fi.AirlineID = f.AirlineID AND fi.flight_number = f.flight_number " +
                     "WHERE t.ticket_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticketNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return parseTicket(rs);
            }
        }
        return null;
    }
    
    public List<Ticket> getTicketsByReservation(int reservationID) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, c.name as customer_name, " +
                     "fi.flight_instance_id, fi.AirlineID, fi.flight_number, fi.departure_date, " +
                     "f.departure_AirportID, f.arrival_AirportID " +
                     "FROM Ticket t " +
                     "JOIN Customer c ON t.customerID = c.customerID " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Flight f ON fi.AirlineID = f.AirlineID AND fi.flight_number = f.flight_number " +
                     "WHERE t.reservationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reservationID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tickets.add(parseTicket(rs));
            }
        }
        return tickets;
    }
    
    public List<Ticket> getTicketsByCustomer(int customerID) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, c.name as customer_name, " +
                     "fi.flight_instance_id, fi.AirlineID, fi.flight_number, fi.departure_date, " +
                     "f.departure_AirportID, f.arrival_AirportID " +
                     "FROM Ticket t " +
                     "JOIN Customer c ON t.customerID = c.customerID " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Flight f ON fi.AirlineID = f.AirlineID AND fi.flight_number = f.flight_number " +
                     "WHERE t.customerID = ? " +
                     "ORDER BY fi.departure_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tickets.add(parseTicket(rs));
            }
        }
        return tickets;
    }
    
    public boolean updateTicket(Ticket ticket) throws SQLException {
        String sql = "UPDATE Ticket SET total_fare = ?, booking_fee = ?, class = ?, " +
                     "seat_number = ?, special_meal = ? WHERE ticket_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, ticket.getTotalFare());
            pstmt.setDouble(2, ticket.getBookingFee());
            pstmt.setString(3, ticket.getTicketClass());
            pstmt.setString(4, ticket.getSeatNumber());
            pstmt.setString(5, ticket.getSpecialMeal());
            pstmt.setString(6, ticket.getTicketNumber());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteTicket(String ticketNumber) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete from includes first
            String sql1 = "DELETE FROM includes WHERE ticket_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setString(1, ticketNumber);
                pstmt.executeUpdate();
            }
            
            // Delete ticket
            String sql2 = "DELETE FROM Ticket WHERE ticket_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setString(1, ticketNumber);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public double getTotalRevenueByFlight(String airlineID, String flightNumber) throws SQLException {
        String sql = "SELECT SUM(t.total_fare + t.booking_fee) as revenue " +
                     "FROM Ticket t " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "WHERE fi.AirlineID = ? AND fi.flight_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            pstmt.setString(2, flightNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        }
        return 0;
    }
    
    public double getTotalRevenueByCustomer(int customerID) throws SQLException {
        String sql = "SELECT SUM(total_fare + booking_fee) as revenue FROM Ticket WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        }
        return 0;
    }
    
    public double getTotalRevenueByAirline(String airlineID) throws SQLException {
        String sql = "SELECT SUM(t.total_fare + t.booking_fee) as revenue " +
                     "FROM Ticket t " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "WHERE fi.AirlineID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        }
        return 0;
    }
    
    public int getTicketCountByFlight(String airlineID, String flightNumber) throws SQLException {
        String sql = "SELECT COUNT(*) as count " +
                     "FROM Ticket t " +
                     "JOIN includes i ON t.ticket_number = i.ticket_number " +
                     "JOIN Flight_Instance fi ON i.flight_instance_id = fi.flight_instance_id " +
                     "JOIN Reservation r ON t.reservationID = r.reservationID " +
                     "WHERE fi.AirlineID = ? AND fi.flight_number = ? AND r.status = 'CONFIRMED'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, airlineID);
            pstmt.setString(2, flightNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    
    private Ticket parseTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(rs.getString("ticket_number"));
        ticket.setReservationID(rs.getInt("reservationID"));
        ticket.setCustomerID(rs.getInt("customerID"));
        ticket.setTotalFare(rs.getDouble("total_fare"));
        ticket.setBookingFee(rs.getDouble("booking_fee"));
        ticket.setPurchaseDatetime(rs.getTimestamp("purchase_datetime"));
        ticket.setTicketClass(rs.getString("class"));
        ticket.setSeatNumber(rs.getString("seat_number"));
        ticket.setSpecialMeal(rs.getString("special_meal"));
        ticket.setCustomerName(rs.getString("customer_name"));
        
        try {
            ticket.setFlightInstanceID(rs.getInt("flight_instance_id"));
            String flightInfo = rs.getString("AirlineID") + rs.getString("flight_number") + 
                                " on " + rs.getDate("departure_date") + " (" + 
                                rs.getString("departure_AirportID") + " -> " + 
                                rs.getString("arrival_AirportID") + ")";
            ticket.setFlightInfo(flightInfo);
        } catch (SQLException e) {
            // Additional columns not present
        }
        
        return ticket;
    }
}