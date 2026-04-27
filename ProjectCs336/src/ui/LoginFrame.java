package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import dao.*;

public class LoginFrame extends JFrame {
    private JComboBox<String> userTypeCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    public LoginFrame() {
        setTitle("Travel Reservation System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Travel Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // User Type
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("User Type:"), gbc);
        
        gbc.gridx = 1;
        userTypeCombo = new JComboBox<>(new String[]{"Customer", "Customer Representative", "Admin"});
        mainPanel.add(userTypeCombo, gbc);
        
        // Username
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Email/Name:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Password/Phone:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        registerButton = new JButton("Register as Customer");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        // Info label
        JLabel infoLabel = new JLabel("<html><small>Customer: use email and phone as password<br>Employee: use name and employee ID as password</small></html>");
        gbc.gridy = 5;
        mainPanel.add(infoLabel, gbc);
        
        add(mainPanel);
        
        // Action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> showRegistration());
        
        passwordField.addActionListener(e -> handleLogin());
    }
    
    private void handleLogin() {
        String userType = (String) userTypeCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        
        try {
            if (userType.equals("Customer")) {
                CustomerDAO customerDAO = new CustomerDAO();
                model.Customer customer = customerDAO.authenticate(username, password);
                if (customer != null) {
                    dispose();
                    new CustomerFrame(customer).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            } else if (userType.equals("Customer Representative")) {
                EmployeeDAO employeeDAO = new EmployeeDAO();
                model.Employee employee = employeeDAO.authenticate(username, password, "CUSTOMER_REPRESENTATIVE");
                if (employee != null) {
                    dispose();
                    new RepresentativeFrame(employee).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            } else if (userType.equals("Admin")) {
                EmployeeDAO employeeDAO = new EmployeeDAO();
                model.Employee employee = employeeDAO.authenticate(username, password, "ADMIN");
                if (employee != null) {
                    dispose();
                    new AdminFrame(employee).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void showRegistration() {
        new RegisterFrame(this).setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}