package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
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
    private TableRowSorter<DefaultTableModel> searchTableSorter;
    private JComboBox<String> sortCombo;
    private JTextField maxPriceField;
    private JComboBox<String> airlineFilterCombo;
    private JComboBox<String> stopsFilterCombo;
    private JTextField departureAfterField;
    private JTextField arrivalBeforeField;
    private Date currentSearchDate;
    
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
        
        add(tabbedPane);

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
        String[] columns = {"Flight", "Airline", "From", "To", "Departure", "Arrival", "Duration", "Stops", "Class", "Price", "Seats", "InstanceID"};
        searchTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchResultsTable = new JTable(searchTableModel);
        searchResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchTableSorter = new TableRowSorter<>(searchTableModel);
        searchResultsTable.setRowSorter(searchTableSorter);
        searchResultsTable.getColumnModel().getColumn(11).setMinWidth(0);
        searchResultsTable.getColumnModel().getColumn(11).setMaxWidth(0);
        searchResultsTable.getColumnModel().getColumn(11).setWidth(0);
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Sort and filter controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortCombo = new JComboBox<>(new String[]{
            "Default",
            "Price: Low to High",
            "Price: High to Low",
            "Take-off: Earliest",
            "Take-off: Latest",
            "Landing: Earliest",
            "Landing: Latest",
            "Duration: Shortest",
            "Duration: Longest"
        });
        sortCombo.addActionListener(e -> applySortOption());
        maxPriceField = new JTextField(6);
        airlineFilterCombo = new JComboBox<>();
        airlineFilterCombo.addItem("All");
        stopsFilterCombo = new JComboBox<>(new String[]{"Any", "Non-stop"});
        departureAfterField = new JTextField(5); // HH:MM
        arrivalBeforeField = new JTextField(5); // HH:MM
        JButton applyFiltersButton = new JButton("Apply Filters");
        applyFiltersButton.addActionListener(e -> applySearchFilters());
        JButton clearFiltersButton = new JButton("Clear Filters");
        clearFiltersButton.addActionListener(e -> clearSearchFilters());
        
        controlsPanel.add(new JLabel("Sort:"));
        controlsPanel.add(sortCombo);
        controlsPanel.add(new JLabel("Max $:"));
        controlsPanel.add(maxPriceField);
        controlsPanel.add(new JLabel("Airline:"));
        controlsPanel.add(airlineFilterCombo);
        controlsPanel.add(new JLabel("Stops:"));
        controlsPanel.add(stopsFilterCombo);
        controlsPanel.add(new JLabel("Take-off after (HH:MM):"));
        controlsPanel.add(departureAfterField);
        controlsPanel.add(new JLabel("Landing before (HH:MM):"));
        controlsPanel.add(arrivalBeforeField);
        controlsPanel.add(applyFiltersButton);
        controlsPanel.add(clearFiltersButton);

        // Book Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Selected Flight");
        bookButton.addActionListener(e -> bookFlight());
        JButton bookWaitlistButton = new JButton("Join Waitlist");
        bookWaitlistButton.addActionListener(e -> joinWaitlist());
        buttonPanel.add(bookButton);
        buttonPanel.add(bookWaitlistButton);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(controlsPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);
        
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
        JButton upcomingButton = new JButton("Upcoming Trips");
        upcomingButton.addActionListener(e -> showTripsByDate(true));
        JButton pastButton = new JButton("Past Trips");
        pastButton.addActionListener(e -> showTripsByDate(false));
        JButton cancelButton = new JButton("Cancel Reservation");
        cancelButton.addActionListener(e -> cancelReservation());
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(upcomingButton);
        buttonPanel.add(pastButton);
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
            currentSearchDate = departureDate;
            
            FlightDAO flightDAO = new FlightDAO();
            FlightInstanceDAO flightInstanceDAO = new FlightInstanceDAO();
            List<Flight> flights = flightDAO.searchFlights(fromAirport, toAirport, departureDate, flexible);
            
            searchTableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                String flightNum = flight.getAirlineID() + flight.getFlightNumber();
                String duration = calculateDuration(flight.getDepartureTime(), flight.getArrivalTime());
                String ticketClass = (String) classCombo.getSelectedItem();
                double price = calculatePrice(ticketClass);
                int instanceId = ensureFlightInstance(flight, departureDate, flightInstanceDAO);
                int availableSeats = getAvailableSeats(instanceId, flightInstanceDAO);
                String seatText = availableSeats > 0 ? String.valueOf(availableSeats) : "Full";
                
                searchTableModel.addRow(new Object[]{
                    flightNum,
                    flight.getAirlineID(),
                    flight.getDepartureAirportID(),
                    flight.getArrivalAirportID(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    duration,
                    0,
                    ticketClass,
                    "$" + price,
                    seatText,
                    instanceId
                });
            }
            reloadAirlineFilterFromResults();
            applySortOption();
            
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

    private void applySortOption() {
        if (searchTableSorter == null) return;
        String option = (String) sortCombo.getSelectedItem();
        if (option == null || option.equals("Default")) {
            searchTableSorter.setSortKeys(null);
            return;
        }

        searchTableSorter.setComparator(9, Comparator.comparingDouble(this::parsePrice));
        searchTableSorter.setComparator(4, Comparator.comparing(this::parseTime));
        searchTableSorter.setComparator(5, Comparator.comparing(this::parseTime));
        searchTableSorter.setComparator(6, Comparator.comparingInt(this::parseDurationMinutes));

        SortOrder order = SortOrder.ASCENDING;
        int column = 9;
        switch (option) {
            case "Price: High to Low":
                order = SortOrder.DESCENDING;
                column = 9;
                break;
            case "Price: Low to High":
                column = 9;
                break;
            case "Take-off: Earliest":
                column = 4;
                break;
            case "Take-off: Latest":
                column = 4;
                order = SortOrder.DESCENDING;
                break;
            case "Landing: Earliest":
                column = 5;
                break;
            case "Landing: Latest":
                column = 5;
                order = SortOrder.DESCENDING;
                break;
            case "Duration: Shortest":
                column = 6;
                break;
            case "Duration: Longest":
                column = 6;
                order = SortOrder.DESCENDING;
                break;
            default:
                break;
        }
        searchTableSorter.setSortKeys(List.of(new RowSorter.SortKey(column, order)));
    }

    private void applySearchFilters() {
        if (searchTableSorter == null) return;
        RowFilter<DefaultTableModel, Integer> filter = new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String priceText = String.valueOf(entry.getValue(9));
                String airline = String.valueOf(entry.getValue(1));
                String dep = String.valueOf(entry.getValue(4));
                String arr = String.valueOf(entry.getValue(5));
                String stops = String.valueOf(entry.getValue(7));

                String maxPriceText = maxPriceField.getText().trim();
                if (!maxPriceText.isEmpty()) {
                    try {
                        double max = Double.parseDouble(maxPriceText);
                        if (parsePrice(priceText) > max) return false;
                    } catch (NumberFormatException ignored) {
                        return false;
                    }
                }

                String airlineSel = (String) airlineFilterCombo.getSelectedItem();
                if (airlineSel != null && !airlineSel.equals("All") && !airline.equalsIgnoreCase(airlineSel)) {
                    return false;
                }

                String stopsSel = (String) stopsFilterCombo.getSelectedItem();
                if ("Non-stop".equals(stopsSel) && !"0".equals(stops)) {
                    return false;
                }

                String depAfter = departureAfterField.getText().trim();
                if (!depAfter.isEmpty()) {
                    if (!Pattern.matches("\\d{2}:\\d{2}", depAfter)) return false;
                    if (parseTime(dep).compareTo(depAfter + ":00") < 0) return false;
                }

                String arrBefore = arrivalBeforeField.getText().trim();
                if (!arrBefore.isEmpty()) {
                    if (!Pattern.matches("\\d{2}:\\d{2}", arrBefore)) return false;
                    if (parseTime(arr).compareTo(arrBefore + ":00") > 0) return false;
                }
                return true;
            }
        };
        searchTableSorter.setRowFilter(filter);
    }

    private void clearSearchFilters() {
        maxPriceField.setText("");
        departureAfterField.setText("");
        arrivalBeforeField.setText("");
        if (airlineFilterCombo.getItemCount() > 0) airlineFilterCombo.setSelectedIndex(0);
        stopsFilterCombo.setSelectedIndex(0);
        searchTableSorter.setRowFilter(null);
        applySortOption();
    }

    private void reloadAirlineFilterFromResults() {
        String prev = (String) airlineFilterCombo.getSelectedItem();
        airlineFilterCombo.removeAllItems();
        airlineFilterCombo.addItem("All");
        for (int i = 0; i < searchTableModel.getRowCount(); i++) {
            String airline = String.valueOf(searchTableModel.getValueAt(i, 1));
            boolean exists = false;
            for (int j = 0; j < airlineFilterCombo.getItemCount(); j++) {
                if (airline.equals(airlineFilterCombo.getItemAt(j))) {
                    exists = true;
                    break;
                }
            }
            if (!exists) airlineFilterCombo.addItem(airline);
        }
        if (prev != null) airlineFilterCombo.setSelectedItem(prev);
    }

    private double parsePrice(Object v) {
        String s = String.valueOf(v).replace("$", "").trim();
        try { return Double.parseDouble(s); } catch (Exception e) { return Double.MAX_VALUE; }
    }

    private String parseTime(Object v) {
        String s = String.valueOf(v).trim();
        if (s.length() == 5) return s + ":00";
        return s;
    }

    private int parseDurationMinutes(Object v) {
        String s = String.valueOf(v);
        // expected format: "Xh Ym"
        try {
            String[] parts = s.split("h");
            int h = Integer.parseInt(parts[0].trim());
            int m = Integer.parseInt(parts[1].replace("m", "").trim());
            return h * 60 + m;
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
    
    private void bookFlight() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to book");
            return;
        }
        
        try {
            int modelRow = searchResultsTable.convertRowIndexToModel(selectedRow);
            int flightInstanceID = (int) searchTableModel.getValueAt(modelRow, 11);
            String seatValue = String.valueOf(searchTableModel.getValueAt(modelRow, 10));
            if ("Full".equalsIgnoreCase(seatValue) || "0".equals(seatValue)) {
                JOptionPane.showMessageDialog(this, "This flight is full. Please join the waitlist.");
                return;
            }

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
                String ticketNumber = ticketDAO.createTicket(ticket, flightInstanceID);
                
                JOptionPane.showMessageDialog(this, 
                    "Booking successful!\nTicket Number: " + ticketNumber + 
                    "\nTotal: $" + (price + bookingFee));
                
                searchTableModel.setValueAt("Booked", modelRow, 10);
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
            int modelRow = searchResultsTable.convertRowIndexToModel(selectedRow);
            int flightInstanceID = (int) searchTableModel.getValueAt(modelRow, 11);
            WaitlistDAO waitlistDAO = new WaitlistDAO();
            if (waitlistDAO.isOnWaitlist(currentCustomer.getCustomerID(), flightInstanceID)) {
                JOptionPane.showMessageDialog(this, "You are already on the waitlist for this flight.");
                return;
            }
            boolean success = waitlistDAO.addToWaitlist(currentCustomer.getCustomerID(), flightInstanceID);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Added to waitlist successfully!");
                searchTableModel.setValueAt("Waitlisted", modelRow, 10);
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
                TicketDAO ticketDAO = new TicketDAO();
                List<Ticket> tickets = ticketDAO.getTicketsByReservation(reservationID);
                boolean hasEconomy = false;
                for (Ticket t : tickets) {
                    if ("ECONOMY".equalsIgnoreCase(t.getTicketClass())) {
                        hasEconomy = true;
                        break;
                    }
                }
                if (hasEconomy) {
                    JOptionPane.showMessageDialog(this, "Only BUSINESS or FIRST reservations can be cancelled.");
                    return;
                }

                ReservationDAO reservationDAO = new ReservationDAO();
                boolean success = reservationDAO.updateReservationStatus(reservationID, "CANCELLED");
                
                if (success) {
                    sendWaitlistAlertsForTickets(tickets);
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully");
                    loadReservations();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private int ensureFlightInstance(Flight flight, Date departureDate, FlightInstanceDAO flightInstanceDAO) throws Exception {
        FlightDAO flightDAO = new FlightDAO();
        List<FlightInstance> instances = flightDAO.getFlightInstances(flight.getAirlineID(), flight.getFlightNumber(), departureDate);
        if (!instances.isEmpty()) {
            return instances.get(0).getFlightInstanceID();
        }
        FlightInstance instance = new FlightInstance();
        instance.setAirlineID(flight.getAirlineID());
        instance.setFlightNumber(flight.getFlightNumber());
        instance.setDepartureDate(departureDate);
        instance.setActualDepartureTime(flight.getDepartureTime());
        instance.setActualArrivalTime(flight.getArrivalTime());
        return flightInstanceDAO.createFlightInstance(instance);
    }

    private int getAvailableSeats(int instanceId, FlightInstanceDAO flightInstanceDAO) {
        try {
            FlightInstance fi = flightInstanceDAO.getFlightInstanceByID(instanceId);
            return fi != null ? fi.getAvailableSeats() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void showTripsByDate(boolean upcoming) {
        try {
            TicketDAO ticketDAO = new TicketDAO();
            List<Ticket> tickets = ticketDAO.getTicketsByCustomer(currentCustomer.getCustomerID());
            LocalDate today = LocalDate.now();
            StringBuilder text = new StringBuilder(upcoming ? "Upcoming Trips:\n\n" : "Past Trips:\n\n");
            int count = 0;
            for (Ticket t : tickets) {
                LocalDate d = parseDateFromFlightInfo(t.getFlightInfo());
                if (d == null) {
                    continue;
                }
                if ((upcoming && (d.isEqual(today) || d.isAfter(today))) || (!upcoming && d.isBefore(today))) {
                    count++;
                    text.append(t.getFlightInfo())
                        .append(" | Class: ").append(t.getTicketClass())
                        .append(" | Ticket: ").append(t.getTicketNumber())
                        .append("\n");
                }
            }
            if (count == 0) {
                text.append("No trips found.");
            }
            JOptionPane.showMessageDialog(this, text.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + ex.getMessage());
        }
    }

    private LocalDate parseDateFromFlightInfo(String info) {
        if (info == null) return null;
        int onIdx = info.indexOf(" on ");
        int parenIdx = info.indexOf(" (");
        if (onIdx < 0 || parenIdx < 0 || parenIdx <= onIdx + 4) return null;
        String datePart = info.substring(onIdx + 4, parenIdx).trim();
        try {
            return LocalDate.parse(datePart);
        } catch (Exception e) {
            return null;
        }
    }

    private void sendWaitlistAlertsForTickets(List<Ticket> tickets) {
        try {
            WaitlistDAO waitlistDAO = new WaitlistDAO();
            Set<Integer> notifiedInstances = new HashSet<>();
            StringBuilder msg = new StringBuilder();
            for (Ticket t : tickets) {
                int id = t.getFlightInstanceID();
                if (id <= 0 || notifiedInstances.contains(id)) continue;
                notifiedInstances.add(id);
                List<WaitlistEntry> entries = waitlistDAO.getWaitlistByFlight(id);
                if (!entries.isEmpty()) {
                    msg.append("Alerted waitlist for ").append(entries.get(0).getFlightInfo())
                       .append(" (").append(entries.size()).append(" customer(s)).\n");
                }
            }
            if (msg.length() > 0) {
                JOptionPane.showMessageDialog(this, msg.toString());
            }
        } catch (Exception ignored) {
            // non-blocking best effort alert message
        }
    }
}