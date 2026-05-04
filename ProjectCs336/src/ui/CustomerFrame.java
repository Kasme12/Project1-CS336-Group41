package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Date;
import java.sql.Time;

import java.util.List;
import java.util.Random;
import dao.*;
import model.*;

public class CustomerFrame extends JFrame {
    private Customer currentCustomer;
    private JTabbedPane tabbedPane;
    
    // Search components
    private JComboBox<String> fromAirportCombo;
    private JComboBox<String> toAirportCombo;
    private JComboBox<String> tripTypeCombo;
    private JComboBox<String> classCombo;
    private JTextField departureDateField;
    private JTextField returnDateField;
    private JCheckBox flexibleCheckBox;
    private JTable searchResultsTable;
    private DefaultTableModel searchTableModel;
    
    // My Reservations components
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    
    public CustomerFrame(Customer customer) {
        this.currentCustomer = customer;
        setTitle("Travel Reservation - Customer: " + customer.getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        loadAirports();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Search Flights Tab
        tabbedPane.addTab("Search Flights", createSearchPanel());
        
        // My Reservations Tab
        tabbedPane.addTab("My Reservations", createReservationsPanel());
        
        // Waitlist Tab
        tabbedPane.addTab("My Waitlist", createWaitlistPanel());

        QAPanel qaPanel = new QAPanel(currentCustomer);
        tabbedPane.addTab("Q&A", qaPanel.createQAPanel());
        
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
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search Form Panel
        JPanel searchFormPanel = new JPanel(new GridBagLayout());
        searchFormPanel.setBorder(BorderFactory.createTitledBorder("Search Flights"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Trip Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchFormPanel.add(new JLabel("Trip Type:"), gbc);
        gbc.gridx = 1;
        tripTypeCombo = new JComboBox<>(new String[]{"One-Way", "Round-Trip"});
        tripTypeCombo.addActionListener(e -> {
            returnDateField.setEnabled(tripTypeCombo.getSelectedItem().equals("Round-Trip"));
        });
        searchFormPanel.add(tripTypeCombo, gbc);
        
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
        
        // Return Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        searchFormPanel.add(new JLabel("Return Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        returnDateField = new JTextField(15);
        returnDateField.setEnabled(false);
        searchFormPanel.add(returnDateField, gbc);
        
        // Class
        gbc.gridx = 0;
        gbc.gridy = 5;
        searchFormPanel.add(new JLabel("Class:"), gbc);
        gbc.gridx = 1;
        classCombo = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS", "FIRST"});
        searchFormPanel.add(classCombo, gbc);
        
        // Flexible Dates
        gbc.gridx = 0;
        gbc.gridy = 6;
        searchFormPanel.add(new JLabel(""), gbc);
        gbc.gridx = 1;
        flexibleCheckBox = new JCheckBox("Flexible Dates (+/- 3 days)");
        searchFormPanel.add(flexibleCheckBox, gbc);
        
        // Search Button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JButton searchButton = new JButton("Search Flights");
        searchButton.addActionListener(e -> searchFlights());
        searchFormPanel.add(searchButton, gbc);
        
        panel.add(searchFormPanel, BorderLayout.NORTH);
        
        // Results Table
        String[] columns = {"Flight", "From", "To", "Departure", "Arrival", "Duration", "Class", "Price", "Seats"};
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
        bookButton.addActionListener(e -> bookFlight());
        JButton bookWaitlistButton = new JButton("Join Waitlist");
        bookWaitlistButton.addActionListener(e -> joinWaitlist());
        buttonPanel.add(bookButton);
        buttonPanel.add(bookWaitlistButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("My Reservations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Reservation ID", "Date", "Status", "Tickets", "Total Fare"};
        reservationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadReservations());
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewReservationDetails());
        JButton cancelButton = new JButton("Cancel Reservation");
        cancelButton.addActionListener(e -> cancelReservation());
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load reservations on tab selection
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                loadReservations();
            }
        });
        
        return panel;
    }
    
    private JPanel createWaitlistPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("My Waitlist Entries");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Flight", "Request Time", "Status"};
        DefaultTableModel waitlistModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable waitlistTable = new JTable(waitlistModel);
        JScrollPane scrollPane = new JScrollPane(waitlistTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                WaitlistDAO waitlistDAO = new WaitlistDAO();
                List<WaitlistEntry> entries = waitlistDAO.getWaitlistByCustomer(currentCustomer.getCustomerID());
                waitlistModel.setRowCount(0);
                for (WaitlistEntry entry : entries) {
                    waitlistModel.addRow(new Object[]{
                        entry.getFlightInfo(),
                        entry.getRequestTime(),
                        "Waiting"
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
    
    private void searchFlights() {
        String fromAirport = (String) fromAirportCombo.getSelectedItem();
        String toAirport = (String) toAirportCombo.getSelectedItem();
        String departureDateStr = departureDateField.getText().trim();
        boolean flexible = flexibleCheckBox.isSelected();
        
        if (fromAirport == null || toAirport == null || departureDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields");
            return;
        }
        
        if (fromAirport.equals(toAirport)) {
            JOptionPane.showMessageDialog(this, "From and To airports cannot be the same");
            return;
        }
        
        try {
            Date departureDate = Date.valueOf(departureDateStr);
            
            FlightDAO flightDAO = new FlightDAO();
            List<Flight> flights = flightDAO.searchFlights(fromAirport, toAirport, departureDate, flexible);
            
            searchTableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                String flightNum = flight.getAirlineID() + flight.getFlightNumber();
                String duration = calculateDuration(flight.getDepartureTime(), flight.getArrivalTime());
                String ticketClass = (String) classCombo.getSelectedItem();
                double price = calculatePrice(ticketClass);
                
                searchTableModel.addRow(new Object[]{
                    flightNum,
                    flight.getDepartureAirportID(),
                    flight.getArrivalAirportID(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    duration,
                    ticketClass,
                    "$" + price,
                    "Available"
                });
            }
            
            if (searchTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No flights found for the selected criteria");
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching flights: " + ex.getMessage());
            ex.printStackTrace();
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
            case "BUSINESS":
                return basePrice * 3;
            case "FIRST":
                return basePrice * 5;
            default:
                return basePrice;
        }
    }
    
    private void bookFlight() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to book");
            return;
        }
        
        try {
            String flightInfo = (String) searchTableModel.getValueAt(selectedRow, 0);
            String ticketClass = (String) classCombo.getSelectedItem();
            double price = calculatePrice(ticketClass);
            double bookingFee = price * 0.1;
            
            // Create reservation
            ReservationDAO reservationDAO = new ReservationDAO();
            int reservationID = reservationDAO.createReservation(currentCustomer.getCustomerID());
            
            if (reservationID > 0) {
                // Create ticket
                Ticket ticket = new Ticket();
                ticket.setReservationID(reservationID);
                ticket.setCustomerID(currentCustomer.getCustomerID());
                ticket.setTotalFare(price);
                ticket.setBookingFee(bookingFee);
                ticket.setTicketClass(ticketClass);
                ticket.setSeatNumber(generateSeatNumber());
                ticket.setSpecialMeal("None");
                
                TicketDAO ticketDAO = new TicketDAO();
                String ticketNumber = ticketDAO.createTicket(ticket, 1); // flightInstanceID = 1 for demo
                
                JOptionPane.showMessageDialog(this, 
                    "Booking successful!\nTicket Number: " + ticketNumber + 
                    "\nTotal: $" + (price + bookingFee));
                
                searchTableModel.removeRow(selectedRow);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error booking flight: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void joinWaitlist() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to join waitlist");
            return;
        }
        
        try {
            WaitlistDAO waitlistDAO = new WaitlistDAO();
            boolean success = waitlistDAO.addToWaitlist(currentCustomer.getCustomerID(), 1);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Added to waitlist successfully!");
                searchTableModel.removeRow(selectedRow);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error joining waitlist: " + ex.getMessage());
        }
    }
    
    private String generateSeatNumber() {
        Random random = new Random();
        char row = (char) ('A' + random.nextInt(6));
        int seat = 1 + random.nextInt(30);
        return row + String.valueOf(seat);
    }
    
    private void loadReservations() {
        try {
            ReservationDAO reservationDAO = new ReservationDAO();
            List<Reservation> reservations = reservationDAO.getReservationsByCustomer(currentCustomer.getCustomerID());
            
            reservationsTableModel.setRowCount(0);
            
            for (Reservation reservation : reservations) {
                reservationsTableModel.addRow(new Object[]{
                    reservation.getReservationID(),
                    reservation.getCreatedAt(),
                    reservation.getStatus(),
                    reservation.getTicketCount(),
                    "$" + reservation.getTotalFare()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + ex.getMessage());
        }
    }
    
    private void viewReservationDetails() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation");
            return;
        }
        
        int reservationID = (int) reservationsTableModel.getValueAt(selectedRow, 0);
        
        try {
            TicketDAO ticketDAO = new TicketDAO();
            List<Ticket> tickets = ticketDAO.getTicketsByReservation(reservationID);
            
            StringBuilder details = new StringBuilder("Reservation #" + reservationID + " Details:\n\n");
            for (Ticket ticket : tickets) {
                details.append("Ticket: ").append(ticket.getTicketNumber()).append("\n");
                details.append("Class: ").append(ticket.getTicketClass()).append("\n");
                details.append("Seat: ").append(ticket.getSeatNumber()).append("\n");
                details.append("Fare: $").append(ticket.getTotalFare()).append("\n");
                details.append("Booking Fee: $").append(ticket.getBookingFee()).append("\n");
                details.append("---\n");
            }
            
            JOptionPane.showMessageDialog(this, details.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void cancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel");
            return;
        }
        
        int reservationID = (int) reservationsTableModel.getValueAt(selectedRow, 0);
        String status = (String) reservationsTableModel.getValueAt(selectedRow, 2);
        
        if (!status.equals("CONFIRMED")) {
            JOptionPane.showMessageDialog(this, "Can only cancel confirmed reservations");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this reservation?", 
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ReservationDAO reservationDAO = new ReservationDAO();
                boolean success = reservationDAO.updateReservationStatus(reservationID, "CANCELLED");
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully");
                    loadReservations();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}