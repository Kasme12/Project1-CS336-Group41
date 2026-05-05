package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import dao.*;
import model.*;

public class RegisterFrame extends JFrame {
    private JFrame parentFrame;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton cancelButton;
    
    public RegisterFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Register New Customer");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("New Customer Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Name
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        
        // Email
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);
        
        // Phone (will be used as password)
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Phone (Password):"), gbc);
        
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
        
        // Action listeners
        registerButton.addActionListener(e -> handleRegister());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }
        
        try {
            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            
            CustomerDAO customerDAO = new CustomerDAO();
            boolean success = customerDAO.addCustomer(customer);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!\nYou can now login with your email and phone.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}