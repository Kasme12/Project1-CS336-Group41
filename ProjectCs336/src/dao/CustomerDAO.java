package dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public Customer authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE email = ? AND phone = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return parseCustomer(rs);
            }
        }
        return null;
    }
    
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(parseCustomer(rs));
            }
        }
        return customers;
    }
    
    public Customer getCustomerByID(int customerID) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return parseCustomer(rs);
            }
        }
        return null;
    }
    
    public boolean addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer (name, email, phone) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE Customer SET name = ?, email = ?, phone = ? WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setInt(4, customer.getCustomerID());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteCustomer(int customerID) throws SQLException {
        String sql = "DELETE FROM Customer WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerID);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public List<Customer> searchCustomers(String keyword) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE name LIKE ? OR email LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(parseCustomer(rs));
            }
        }
        return customers;
    }
    
    private Customer parseCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("customerID"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        return customer;
    }
}