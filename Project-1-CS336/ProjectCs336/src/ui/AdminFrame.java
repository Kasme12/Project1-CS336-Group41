package ui;

import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import dao.*;
import model.*;

public class AdminFrame extends JFrame {
    private Employee currentEmployee;
    private JTabbedPane tabbedPane;
    
    // Manage Users components
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    
    // Sales Report components
    private JComboBox<String> monthCombo;
    private JTextArea reportArea;
    
    // Revenue components
    private JTable revenueTable;
    private DefaultTableModel revenueTableModel;
    
    public AdminFrame(Employee employee) {
        this.currentEmployee = employee;
        setTitle("Travel Reservation - Admin: " + employee.getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Manage Users Tab
        tabbedPane.addTab("Manage Users", createManageUsersPanel());
        
        // Sales Report Tab
        tabbedPane.addTab("Sales Report", createSalesReportPanel());
        
        // Revenue Summary Tab
        tabbedPane.addTab("Revenue Summary", createRevenuePanel());
        
        // Most Active Flights Tab
        tabbedPane.addTab("Most Active Flights", createActiveFlightsPanel());
        
        // Reservations by Flight/Customer Tab
        tabbedPane.addTab("Reservations Lookup", createReservationsLookupPanel());
        
        add(tabbedPane);
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private JPanel createManageUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Manage Customers and Representatives");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "Email/Role", "Type"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAllUsers());
        JButton addButton = new JButton("Add User");
        addButton.addActionListener(e -> showAddUserDialog());
        JButton editButton = new JButton("Edit User");
        editButton.addActionListener(e -> showEditUserDialog());
        JButton deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(e -> deleteUser());
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load users
        loadAllUsers();
        
        return panel;
    }
    
    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Sales Report by Month");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Month selection
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel monthLabel = new JLabel("Select Month:");
        monthCombo = new JComboBox<>(new String[]{
            "2026-01", "2026-02", "2026-03", "2026-04", "2025-01", "2025-02", "2025-03", "2025-04", "2025-05"
        });
        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateSalesReport());
        
        filterPanel.add(monthLabel);
        filterPanel.add(monthCombo);
        filterPanel.add(generateButton);
        panel.add(filterPanel, BorderLayout.CENTER);
        
        // Report area
        reportArea = new JTextArea(20, 50);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Revenue Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Type", "ID/Name", "Revenue"};
        revenueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        revenueTable = new JTable(revenueTableModel);
        revenueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(revenueTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton byFlightButton = new JButton("Revenue by Flight");
        byFlightButton.addActionListener(e -> showRevenueByFlight());
        JButton byAirlineButton = new JButton("Revenue by Airline");
        byAirlineButton.addActionListener(e -> showRevenueByAirline());
        JButton byCustomerButton = new JButton("Revenue by Customer");
        byCustomerButton.addActionListener(e -> showRevenueByCustomer());
        JButton topCustomerButton = new JButton("Top Customer");
        topCustomerButton.addActionListener(e -> showTopCustomer());
        buttonPanel.add(byFlightButton);
        buttonPanel.add(byAirlineButton);
        buttonPanel.add(byCustomerButton);
        buttonPanel.add(topCustomerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActiveFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Most Active Flights (Most Tickets Sold)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Flight", "Tickets Sold", "Revenue"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Load Most Active Flights");
        refreshButton.addActionListener(e -> {
            try {
                FlightDAO flightDAO = new FlightDAO();
                TicketDAO ticketDAO = new TicketDAO();
                List<Flight> flights = flightDAO.getAllFlights();
                
                model.setRowCount(0);
                List<Object[]> flightStats = new ArrayList<>();
                
                for (Flight flight : flights) {
                    int count = ticketDAO.getTicketCountByFlight(flight.getAirlineID(), flight.getFlightNumber());
                    double revenue = ticketDAO.getTotalRevenueByFlight(flight.getAirlineID(), flight.getFlightNumber());
                    if (count > 0) {
                        flightStats.add(new Object[]{
                            flight.getAirlineID() + flight.getFlightNumber(),
                            count,
                            revenue
                        });
                    }
                }
                
                // Sort by tickets sold
                flightStats.sort((a, b) -> Integer.compare((int) b[1], (int) a[1]));
                
                for (Object[] stat : flightStats) {
                    model.addRow(new Object[]{
                        stat[0],
                        stat[1],
                        "$" + stat[2]
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createReservationsLookupPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Reservations Lookup");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Search form
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchByFlightButton = new JButton("Search by Flight");
        JButton searchByCustomerButton = new JButton("Search by Customer");
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchByFlightButton);
        searchPanel.add(searchByCustomerButton);
        panel.add(searchPanel, BorderLayout.CENTER);
        
        // Table
        String[] columns = {"Reservation ID", "Customer", "Date", "Status", "Tickets", "Total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        searchByFlightButton.addActionListener(e -> {
            try {
                String search = searchField.getText().trim();
                if (search.length() < 3) {
                    JOptionPane.showMessageDialog(this, "Please enter flight number like AA100");
                    return;
                }
                
                // Parse flight number (e.g., AA123)
                String airlineID = search.substring(0, 2);
                String flightNumber = search.substring(2);
                
                ReservationDAO reservationDAO = new ReservationDAO();
                List<Reservation> reservations = reservationDAO.getReservationsByFlightNumber(airlineID, flightNumber);
                
                model.setRowCount(0);
                for (Reservation r : reservations) {
                    model.addRow(new Object[]{
                        r.getReservationID(),
                        r.getCustomerName(),
                        r.getCreatedAt(),
                        r.getStatus(),
                        r.getTicketCount(),
                        "$" + r.getTotalFare()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        searchByCustomerButton.addActionListener(e -> {
            try {
                String search = searchField.getText().trim();
                if (search.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a customer name");
                    return;
                }
                
                ReservationDAO reservationDAO = new ReservationDAO();
                List<Reservation> reservations = reservationDAO.getReservationsByCustomerName(search);
                
                model.setRowCount(0);
                for (Reservation r : reservations) {
                    model.addRow(new Object[]{
                        r.getReservationID(),
                        r.getCustomerName(),
                        r.getCreatedAt(),
                        r.getStatus(),
                        r.getTicketCount(),
                        "$" + r.getTotalFare()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        return panel;
    }
    
    private void loadAllUsers() {
        try {
            usersTableModel.setRowCount(0);
            
            // Load customers
            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer c : customers) {
                usersTableModel.addRow(new Object[]{
                    c.getCustomerID(),
                    c.getName(),
                    c.getEmail(),
                    "Customer"
                });
            }
            
            // Load representatives
            EmployeeDAO employeeDAO = new EmployeeDAO();
            List<Employee> representatives = employeeDAO.getCustomerRepresentatives();
            for (Employee e : representatives) {
                usersTableModel.addRow(new Object[]{
                    e.getEmployeeID(),
                    e.getName(),
                    "Customer Rep",
                    "Representative"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }
    
    private void generateSalesReport() {
        String month = (String) monthCombo.getSelectedItem();
        StringBuilder report = new StringBuilder();
        report.append("Sales Report for ").append(month).append("\n");
        report.append("================================\n\n");
        
        try {
            ReservationDAO reservationDAO = new ReservationDAO();
            List<Reservation> reservations = reservationDAO.getAllReservations();
            
            int totalReservations = 0;
            double totalRevenue = 0;
            int confirmedCount = 0;
            int cancelledCount = 0;
            
            for (Reservation r : reservations) {
                if (r.getCreatedAt() != null && r.getCreatedAt().toString().startsWith(month)) {
                    totalReservations++;
                    totalRevenue += r.getTotalFare();
                    if (r.getStatus().equals("CONFIRMED")) {
                        confirmedCount++;
                    } else if (r.getStatus().equals("CANCELLED")) {
                        cancelledCount++;
                    }
                }
            }
            
            report.append("Total Reservations: ").append(totalReservations).append("\n");
            report.append("Confirmed: ").append(confirmedCount).append("\n");
            report.append("Cancelled: ").append(cancelledCount).append("\n");
            report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
            report.append("Average Reservation Value: $").append(
                totalReservations > 0 ? String.format("%.2f", totalRevenue / totalReservations) : "0.00");
            
            reportArea.setText(report.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }
    
    private void showRevenueByFlight() {
        try {
            FlightDAO flightDAO = new FlightDAO();
            TicketDAO ticketDAO = new TicketDAO();
            List<Flight> flights = flightDAO.getAllFlights();
            
            revenueTableModel.setRowCount(0);
            for (Flight flight : flights) {
                double revenue = ticketDAO.getTotalRevenueByFlight(flight.getAirlineID(), flight.getFlightNumber());
                if (revenue > 0) {
                    revenueTableModel.addRow(new Object[]{
                        "Flight",
                        flight.getAirlineID() + flight.getFlightNumber(),
                        "$" + revenue
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void showRevenueByAirline() {
        try {
            AirlineDAO airlineDAO = new AirlineDAO();
            TicketDAO ticketDAO = new TicketDAO();
            List<Airline> airlines = airlineDAO.getAllAirlines();
            
            revenueTableModel.setRowCount(0);
            for (Airline airline : airlines) {
                double revenue = ticketDAO.getTotalRevenueByAirline(airline.getAirlineID());
                if (revenue > 0) {
                    revenueTableModel.addRow(new Object[]{
                        "Airline",
                        airline.getName(),
                        "$" + revenue
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void showRevenueByCustomer() {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            TicketDAO ticketDAO = new TicketDAO();
            List<Customer> customers = customerDAO.getAllCustomers();
            
            revenueTableModel.setRowCount(0);
            for (Customer customer : customers) {
                double revenue = ticketDAO.getTotalRevenueByCustomer(customer.getCustomerID());
                if (revenue > 0) {
                    revenueTableModel.addRow(new Object[]{
                        "Customer",
                        customer.getName(),
                        "$" + revenue
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void showTopCustomer() {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            TicketDAO ticketDAO = new TicketDAO();
            List<Customer> customers = customerDAO.getAllCustomers();
            
            double maxRevenue = 0;
            Customer topCustomer = null;
            
            for (Customer customer : customers) {
                double revenue = ticketDAO.getTotalRevenueByCustomer(customer.getCustomerID());
                if (revenue > maxRevenue) {
                    maxRevenue = revenue;
                    topCustomer = customer;
                }
            }
            
            if (topCustomer != null) {
                JOptionPane.showMessageDialog(this, 
                    "Top Customer:\n\n" +
                    "Name: " + topCustomer.getName() + "\n" +
                    "Email: " + topCustomer.getEmail() + "\n" +
                    "Total Revenue: $" + maxRevenue);
            } else {
                JOptionPane.showMessageDialog(this, "No customer data found");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setLayout(new GridLayout(6, 2, 8, 8));
        dialog.setSize(420, 260);
        dialog.setLocationRelativeTo(this);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Customer", "Representative"});
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        dialog.add(new JLabel("Type:"));
        dialog.add(typeCombo);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email (customer only):"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone (customer only):"));
        dialog.add(phoneField);

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        dialog.add(save);
        dialog.add(cancel);

        save.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name is required");
                    return;
                }

                if ("Customer".equals(type)) {
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    if (email.isEmpty() || phone.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Email and phone are required for customer");
                        return;
                    }
                    Customer customer = new Customer();
                    customer.setName(name);
                    customer.setEmail(email);
                    customer.setPhone(phone);
                    new CustomerDAO().addCustomer(customer);
                } else {
                    Employee employee = new Employee();
                    employee.setName(name);
                    new EmployeeDAO().addEmployee(employee, "CUSTOMER_REPRESENTATIVE");
                }

                loadAllUsers();
                JOptionPane.showMessageDialog(dialog, "User added successfully");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dialog.dispose());

        typeCombo.addActionListener(e -> {
            boolean isCustomer = "Customer".equals(typeCombo.getSelectedItem());
            emailField.setEnabled(isCustomer);
            phoneField.setEnabled(isCustomer);
        });
        typeCombo.setSelectedIndex(0);
        dialog.setVisible(true);
    }
    
    private void showEditUserDialog() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }

        int modelRow = usersTable.convertRowIndexToModel(selectedRow);
        int id = Integer.parseInt(String.valueOf(usersTableModel.getValueAt(modelRow, 0)));
        String name = String.valueOf(usersTableModel.getValueAt(modelRow, 1));
        String emailOrRole = String.valueOf(usersTableModel.getValueAt(modelRow, 2));
        String type = String.valueOf(usersTableModel.getValueAt(modelRow, 3));

        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setLayout(new GridLayout(6, 2, 8, 8));
        dialog.setSize(420, 260);
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField(name);
        JTextField emailField = new JTextField("Customer".equals(type) ? emailOrRole : "");
        JTextField phoneField = new JTextField();

        dialog.add(new JLabel("Type:"));
        dialog.add(new JLabel(type));
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email (customer only):"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone (customer only):"));
        dialog.add(phoneField);

        JButton loadCustomer = new JButton("Load Existing");
        JButton save = new JButton("Save");
        dialog.add(loadCustomer);
        dialog.add(save);

        JButton cancel = new JButton("Cancel");
        dialog.add(new JLabel(""));
        dialog.add(cancel);

        boolean isCustomer = "Customer".equals(type);
        emailField.setEnabled(isCustomer);
        phoneField.setEnabled(isCustomer);

        loadCustomer.addActionListener(e -> {
            if (!isCustomer) return;
            try {
                Customer c = new CustomerDAO().getCustomerByID(id);
                if (c != null) {
                    nameField.setText(c.getName());
                    emailField.setText(c.getEmail());
                    phoneField.setText(c.getPhone());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error loading customer: " + ex.getMessage());
            }
        });

        save.addActionListener(e -> {
            try {
                if (isCustomer) {
                    Customer c = new Customer();
                    c.setCustomerID(id);
                    c.setName(nameField.getText().trim());
                    c.setEmail(emailField.getText().trim());
                    c.setPhone(phoneField.getText().trim());
                    new CustomerDAO().updateCustomer(c);
                } else {
                    Employee emp = new Employee();
                    emp.setEmployeeID(id);
                    emp.setName(nameField.getText().trim());
                    new EmployeeDAO().updateEmployee(emp);
                }
                loadAllUsers();
                JOptionPane.showMessageDialog(dialog, "User updated successfully");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this user?", 
            "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = usersTable.convertRowIndexToModel(selectedRow);
                int id = Integer.parseInt(String.valueOf(usersTableModel.getValueAt(modelRow, 0)));
                String type = String.valueOf(usersTableModel.getValueAt(modelRow, 3));

                if ("Customer".equals(type)) {
                    new CustomerDAO().deleteCustomer(id);
                } else if ("Representative".equals(type)) {
                    new EmployeeDAO().deleteEmployee(id, "CUSTOMER_REPRESENTATIVE");
                } else {
                    JOptionPane.showMessageDialog(this, "Unsupported user type: " + type);
                    return;
                }

                loadAllUsers();
                JOptionPane.showMessageDialog(this, "User deleted successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
            }
        }
    }
}