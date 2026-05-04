package dao;

import model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    public Employee authenticate(String name, String password, String role) throws SQLException {
    String sql = "SELECT e.* FROM Employee e ";

    if (role.equals("ADMIN")) {
        sql += "JOIN Admin a ON e.employeeID = a.employeeID ";
    } else if (role.equals("CUSTOMER_REPRESENTATIVE")) {
        sql += "JOIN Customer_Representative cr ON e.employeeID = cr.employeeID ";
    }

    sql += "WHERE e.name = ? AND e.password = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, name);
        pstmt.setString(2, password);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            Employee employee = new Employee();
            employee.setEmployeeID(rs.getInt("employeeID"));
            employee.setName(rs.getString("name"));
            employee.setRole(role);
            return employee;
        }
    }

    return null;
}
    
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeID(rs.getInt("employeeID"));
                employee.setName(rs.getString("name"));
                employees.add(employee);
            }
        }
        return employees;
    }
    
    public Employee getEmployeeByID(int employeeID) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE employeeID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeID(rs.getInt("employeeID"));
                employee.setName(rs.getString("name"));
                return employee;
            }
        }
        return null;
    }
    
    public boolean addEmployee(Employee employee, String role) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into Employee table
            String sql1 = "INSERT INTO Employee (name) VALUES (?)";
            int employeeID = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, employee.getName());
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    employeeID = rs.getInt(1);
                }
            }
            
            // Insert into Admin or Customer_Representative table
            String sql2;
            if (role.equals("ADMIN")) {
                sql2 = "INSERT INTO Admin (employeeID) VALUES (?)";
            } else {
                sql2 = "INSERT INTO Customer_Representative (employeeID) VALUES (?)";
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setInt(1, employeeID);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            employee.setEmployeeID(employeeID);
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
    
    public boolean updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE Employee SET name = ? WHERE employeeID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getName());
            pstmt.setInt(2, employee.getEmployeeID());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteEmployee(int employeeID, String role) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete from role table
            String sql1;
            if (role.equals("ADMIN")) {
                sql1 = "DELETE FROM Admin WHERE employeeID = ?";
            } else {
                sql1 = "DELETE FROM Customer_Representative WHERE employeeID = ?";
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setInt(1, employeeID);
                pstmt.executeUpdate();
            }
            
            // Delete from Employee table
            String sql2 = "DELETE FROM Employee WHERE employeeID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setInt(1, employeeID);
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
    
    public List<Employee> getCustomerRepresentatives() throws SQLException {
        List<Employee> representatives = new ArrayList<>();
        String sql = "SELECT e.* FROM Employee e " +
                     "JOIN Customer_Representative cr ON e.employeeID = cr.employeeID";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeID(rs.getInt("employeeID"));
                employee.setName(rs.getString("name"));
                employee.setRole("CUSTOMER_REPRESENTATIVE");
                representatives.add(employee);
            }
        }
        return representatives;
    }
}