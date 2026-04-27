package model;

public class Employee {
    private int employeeID;
    private String name;
    private String role; // ADMIN or CUSTOMER_REPRESENTATIVE
    private String password;

    public Employee() {}

    public Employee(int employeeID, String name, String role) {
        this.employeeID = employeeID;
        this.name = name;
        this.role = role;
    }

    public int getEmployeeID() { return employeeID; }
    public void setEmployeeID(int employeeID) { this.employeeID = employeeID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() { return name + " (" + role + ")"; }
}