package ui;

import java.util.List;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import dao.*;
import model.*;

public class RepresentativeFrame extends JFrame {
    private Employee currentEmployee;
    private JTabbedPane tabbedPane;
    
    // Make Reservation components
    private JComboBox<String> customerCombo;
    private JComboBox<String> fromAirportCombo;
    private JComboBox<String> toAirportCombo;
    private JTextField departureDateField;
    private JComboBox<String> classCombo;
    private JTable searchResultsTable;
    private DefaultTableModel searchTableModel;
    
    // Manage Flights components
    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;
    
    // View Waitlist components
    private JTable waitlistTable;
    private DefaultTableModel waitlistTableModel;
    
    public RepresentativeFrame(Employee employee) {
        this.currentEmployee = employee;
        setTitle("Travel Reservation - Customer Representative: " + employee.getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Make Reservation Tab
        tabbedPane.addTab("Make Reservation", createReservationPanel());
        
        // Manage Flights Tab
        tabbedPane.addTab("Manage Flights", createManageFlightsPanel());
        
        // View Waitlist Tab
        tabbedPane.addTab("View Waitlist", createWaitlistPanel());
        
        // View All Reservations Tab
        tabbedPane.addTab("All Reservations", createAllReservationsPanel());

        tabbedPane.addTab("Q&A", new RepQAPanel().createPanel());
        
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
    
    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search Form Panel
        JPanel searchFormPanel = new JPanel(new GridBagLayout());
        searchFormPanel.setBorder(BorderFactory.createTitledBorder("Make Reservation for Customer"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Customer
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchFormPanel.add(new JLabel("Select Customer:"), gbc);
        gbc.gridx = 1;
        customerCombo = new JComboBox<>();
        searchFormPanel.add(customerCombo, gbc);
        
        // From Airport
        gbc.gridx = 0;
        gbc.gridy = 1;
        searchFormPanel.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        fromAirportCombo = new JComboBox<>();
        searchFormPanel.add(fromAirportCombo, gbc);
        
        // To Airport
        gbc.gridx = 0;
        gbc.gridy = 2;
        searchFormPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        toAirportCombo = new JComboBox<>();
        searchFormPanel.add(toAirportCombo, gbc);
        
        // Departure Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        searchFormPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        departureDateField = new JTextField(15);
        searchFormPanel.add(departureDateField, gbc);
        
        // Class
        gbc.gridx = 0;
        gbc.gridy = 4;
        searchFormPanel.add(new JLabel("Class:"), gbc);
        gbc.gridx = 1;
        classCombo = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS", "FIRST"});
        searchFormPanel.add(classCombo, gbc);
        
        // Search Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton searchButton = new JButton("Search Flights");
        searchButton.addActionListener(e -> searchFlightsForReservation());
        searchFormPanel.add(searchButton, gbc);
        
        panel.add(searchFormPanel, BorderLayout.NORTH);
        
        // Results Table
        String[] columns = {"Flight", "From", "To", "Departure", "Arrival", "Duration", "Class", "Price"};
        searchTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchResultsTable = new JTable(searchTableModel);
        searchResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Book Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Selected Flight");
        bookButton.addActionListener(e -> bookFlightForCustomer());
        buttonPanel.add(bookButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load customers and airports
        loadCustomers();
        loadAirports();
        
        return panel;
    }
    
    private JPanel createManageFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Manage Flights");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Airline", "Flight #", "From", "To", "Departure", "Arrival", "Type", "Days"};
        flightsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsTableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadFlights());
        JButton addButton = new JButton("Add Flight");
        addButton.addActionListener(e -> showAddFlightDialog());
        JButton editButton = new JButton("Edit Flight");
        editButton.addActionListener(e -> showEditFlightDialog());
        JButton deleteButton = new JButton("Delete Flight");
        deleteButton.addActionListener(e -> deleteFlight());
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load flights
        loadFlights();
        
        return panel;
    }
    
    private JPanel createWaitlistPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("View Waitlist by Flight");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Flight selection
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel flightLabel = new JLabel("Select Flight Instance:");
        JComboBox<String> flightInstanceCombo = new JComboBox<>();
        filterPanel.add(flightLabel);
        filterPanel.add(flightInstanceCombo);
        panel.add(filterPanel, BorderLayout.CENTER);
        
        // Table
        String[] columns = {"Customer Name", "Flight", "Request Time"};
        waitlistTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        waitlistTable = new JTable(waitlistTableModel);
        JScrollPane scrollPane = new JScrollPane(waitlistTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton viewButton = new JButton("View Waitlist");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAllReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("All Reservations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
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
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                ReservationDAO reservationDAO = new ReservationDAO();
                List<Reservation> reservations = reservationDAO.getAllReservations();
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load on tab selection
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) {
                refreshButton.doClick();
            }
        });
        
        return panel;
    }
    
    private void loadCustomers() {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customers = customerDAO.getAllCustomers();
            
            customerCombo.removeAllItems();
            for (Customer customer : customers) {
                customerCombo.addItem(customer.getCustomerID() + " - " + customer.getName());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
        }
    }
    
    private void loadAirports() {
        try {
            AirportDAO airportDAO = new AirportDAO();
            List<Airport> airports = airportDAO.getAllAirports();
            
            fromAirportCombo.removeAllItems();
            toAirportCombo.removeAllItems();
            
            for (Airport airport : airports) {
                fromAirportCombo.addItem(airport.getAirportID());
                toAirportCombo.addItem(airport.getAirportID());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading airports: " + ex.getMessage());
        }
    }
    
    private void loadFlights() {
        try {
            FlightDAO flightDAO = new FlightDAO();
            List<Flight> flights = flightDAO.getAllFlights();
            
            flightsTableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                flightsTableModel.addRow(new Object[]{
                    flight.getAirlineID(),
                    flight.getFlightNumber(),
                    flight.getDepartureAirportID(),
                    flight.getArrivalAirportID(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    flight.getType(),
                    flight.getDaysOfWeek()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage());
        }
    }
    
    private void searchFlightsForReservation() {
        String fromAirport = (String) fromAirportCombo.getSelectedItem();
        String toAirport = (String) toAirportCombo.getSelectedItem();
        String departureDateStr = departureDateField.getText().trim();
        
        if (fromAirport == null || toAirport == null || departureDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields");
            return;
        }
        
        try {
            java.sql.Date departureDate = java.sql.Date.valueOf(departureDateStr);
            
            FlightDAO flightDAO = new FlightDAO();
            List<Flight> flights = flightDAO.searchFlights(fromAirport, toAirport, departureDate, false);
            
            searchTableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                String ticketClass = (String) classCombo.getSelectedItem();
                double price = calculatePrice(ticketClass);
                
                searchTableModel.addRow(new Object[]{
                    flight.getAirlineID() + flight.getFlightNumber(),
                    flight.getDepartureAirportID(),
                    flight.getArrivalAirportID(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    calculateDuration(flight.getDepartureTime(), flight.getArrivalTime()),
                    ticketClass,
                    "$" + price
                });
            }
            
            if (searchTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No flights found");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void bookFlightForCustomer() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight");
            return;
        }
        
        String customerSelection = (String) customerCombo.getSelectedItem();
        if (customerSelection == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer");
            return;
        }
        
        try {
            int customerID = Integer.parseInt(customerSelection.split(" - ")[0]);
            String ticketClass = (String) classCombo.getSelectedItem();
            double price = calculatePrice(ticketClass);
            double bookingFee = price * 0.1;
            
            // Create reservation
            ReservationDAO reservationDAO = new ReservationDAO();
            int reservationID = reservationDAO.createReservation(customerID);
            
            if (reservationID > 0) {
                Ticket ticket = new Ticket();
                ticket.setReservationID(reservationID);
                ticket.setCustomerID(customerID);
                ticket.setTotalFare(price);
                ticket.setBookingFee(bookingFee);
                ticket.setTicketClass(ticketClass);
                ticket.setSeatNumber(generateSeatNumber());
                ticket.setSpecialMeal("None");
                
                TicketDAO ticketDAO = new TicketDAO();
                String ticketNumber = ticketDAO.createTicket(ticket, 1);
                
                JOptionPane.showMessageDialog(this, 
                    "Reservation created!\nTicket: " + ticketNumber + "\nTotal: $" + (price + bookingFee));
                
                searchTableModel.removeRow(selectedRow);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void showAddFlightDialog() {
        JOptionPane.showMessageDialog(this, "Add Flight dialog - Use MySQL Workbench to add flights");
    }
    
    private void showEditFlightDialog() {
        JOptionPane.showMessageDialog(this, "Edit Flight dialog - Use MySQL Workbench to edit flights");
    }
    
    private void deleteFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this flight?", 
            "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Please use MySQL Workbench to delete flights");
        }
    }
    
    private String calculateDuration(Time departure, Time arrival) {
        long diff = arrival.getTime() - departure.getTime();
        long hours = diff / (60 * 60 * 1000);
        long minutes = (diff % (60 * 60 * 1000)) / (60 * 1000);
        return hours + "h " + minutes + "m";
    }
    
    private double calculatePrice(String ticketClass) {
        double basePrice = 200.0;
        switch (ticketClass) {
            case "BUSINESS": return basePrice * 3;
            case "FIRST": return basePrice * 5;
            default: return basePrice;
        }
    }
    
    private String generateSeatNumber() {
        Random random = new Random();
        char row = (char) ('A' + random.nextInt(6));
        int seat = 1 + random.nextInt(30);
        return row + String.valueOf(seat);
    }
}