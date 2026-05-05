package ui;

import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JComboBox<String> flightInstanceCombo;

    // Q&A
    private JTable qaTable;
    private DefaultTableModel qaTableModel;
    
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

        // Flights by Airport tab
        tabbedPane.addTab("Flights by Airport", createFlightsByAirportPanel());

        // Q&A Replies tab
        tabbedPane.addTab("Q&A Replies", createQAPanel());
        
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
        JButton manageAircraftButton = new JButton("Manage Aircraft");
        manageAircraftButton.addActionListener(e -> showManageAircraftDialog());
        JButton manageAirportButton = new JButton("Manage Airports");
        manageAirportButton.addActionListener(e -> showManageAirportsDialog());
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(manageAircraftButton);
        buttonPanel.add(manageAirportButton);
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
        flightInstanceCombo = new JComboBox<>();
        filterPanel.add(flightLabel);
        filterPanel.add(flightInstanceCombo);
        panel.add(filterPanel, BorderLayout.NORTH);
        
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
        viewButton.addActionListener(e -> loadWaitlistBySelectedInstance());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        loadFlightInstances();
        
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
        JButton editReservationButton = new JButton("Edit Selected Reservation");
        editReservationButton.addActionListener(e -> editSelectedReservation(table, model));
        buttonPanel.add(refreshButton);
        buttonPanel.add(editReservationButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load on tab selection
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) {
                refreshButton.doClick();
            }
        });
        
        return panel;
    }

    private JPanel createFlightsByAirportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> airportCombo = new JComboBox<>();
        JButton loadButton = new JButton("Load Flights");
        top.add(new JLabel("Airport:"));
        top.add(airportCombo);
        top.add(loadButton);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Flight", "From", "To", "Departure", "Arrival", "Days"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            AirportDAO airportDAO = new AirportDAO();
            for (Airport a : airportDAO.getAllAirports()) {
                airportCombo.addItem(a.getAirportID());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading airports: " + ex.getMessage());
        }

        loadButton.addActionListener(e -> {
            try {
                String airport = (String) airportCombo.getSelectedItem();
                if (airport == null) return;
                FlightDAO dao = new FlightDAO();
                List<Flight> flights = dao.getFlightsByAirport(airport);
                model.setRowCount(0);
                for (Flight f : flights) {
                    model.addRow(new Object[]{
                        f.getAirlineID() + f.getFlightNumber(),
                        f.getDepartureAirportID(),
                        f.getArrivalAirportID(),
                        f.getDepartureTime(),
                        f.getArrivalTime(),
                        f.getDaysOfWeek()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createQAPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"QA ID", "Customer", "Question", "Answer"};
        qaTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        qaTable = new JTable(qaTableModel);
        panel.add(new JScrollPane(qaTable), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadQAs());
        JButton reply = new JButton("Reply Selected");
        reply.addActionListener(e -> replyToSelectedQA());
        btns.add(refresh);
        btns.add(reply);
        panel.add(btns, BorderLayout.SOUTH);
        loadQAs();

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
            FlightInstanceDAO flightInstanceDAO = new FlightInstanceDAO();
            List<Flight> flights = flightDAO.searchFlights(fromAirport, toAirport, departureDate, false);
            
            searchTableModel.setRowCount(0);
            
            for (Flight flight : flights) {
                String ticketClass = (String) classCombo.getSelectedItem();
                double price = calculatePrice(ticketClass);
                int instanceId = ensureFlightInstance(flight, departureDate, flightInstanceDAO);
                
                searchTableModel.addRow(new Object[]{
                    flight.getAirlineID() + flight.getFlightNumber(),
                    flight.getDepartureAirportID(),
                    flight.getArrivalAirportID(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    calculateDuration(flight.getDepartureTime(), flight.getArrivalTime()),
                    ticketClass,
                    "$" + price + " [FI:" + instanceId + "]"
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
            String priceCell = String.valueOf(searchTableModel.getValueAt(selectedRow, 7));
            int fiStart = priceCell.indexOf("[FI:");
            int fiEnd = priceCell.indexOf("]", fiStart);
            int flightInstanceId = Integer.parseInt(priceCell.substring(fiStart + 4, fiEnd));
            
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
                String ticketNumber = ticketDAO.createTicket(ticket, flightInstanceId);
                
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
        try {
            JTextField airlineField = new JTextField();
            JTextField flightNumberField = new JTextField();
            JTextField fromField = new JTextField();
            JTextField toField = new JTextField();
            JTextField aircraftField = new JTextField();
            JTextField depField = new JTextField("08:00:00");
            JTextField arrField = new JTextField("10:00:00");
            JTextField daysField = new JTextField("Daily");
            JTextField typeField = new JTextField("domestic");

            JPanel p = new JPanel(new GridLayout(9, 2, 6, 6));
            p.add(new JLabel("AirlineID (e.g. AA):")); p.add(airlineField);
            p.add(new JLabel("Flight #:")); p.add(flightNumberField);
            p.add(new JLabel("From AirportID:")); p.add(fromField);
            p.add(new JLabel("To AirportID:")); p.add(toField);
            p.add(new JLabel("Aircraft ID:")); p.add(aircraftField);
            p.add(new JLabel("Departure (HH:MM:SS):")); p.add(depField);
            p.add(new JLabel("Arrival (HH:MM:SS):")); p.add(arrField);
            p.add(new JLabel("Days:")); p.add(daysField);
            p.add(new JLabel("Type:")); p.add(typeField);

            if (JOptionPane.showConfirmDialog(this, p, "Add Flight", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                Flight f = new Flight();
                f.setAirlineID(airlineField.getText().trim());
                f.setFlightNumber(flightNumberField.getText().trim());
                f.setDepartureAirportID(fromField.getText().trim());
                f.setArrivalAirportID(toField.getText().trim());
                f.setAircraftID(Integer.parseInt(aircraftField.getText().trim()));
                f.setDepartureTime(Time.valueOf(depField.getText().trim()));
                f.setArrivalTime(Time.valueOf(arrField.getText().trim()));
                f.setDaysOfWeek(daysField.getText().trim());
                f.setType(typeField.getText().trim());
                new FlightDAO().addFlight(f);
                loadFlights();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding flight: " + ex.getMessage());
        }
    }
    
    private void showEditFlightDialog() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to edit");
            return;
        }
        try {
            String airlineId = String.valueOf(flightsTableModel.getValueAt(selectedRow, 0));
            String flightNo = String.valueOf(flightsTableModel.getValueAt(selectedRow, 1));
            JTextField fromField = new JTextField(String.valueOf(flightsTableModel.getValueAt(selectedRow, 2)));
            JTextField toField = new JTextField(String.valueOf(flightsTableModel.getValueAt(selectedRow, 3)));
            JTextField depField = new JTextField(String.valueOf(flightsTableModel.getValueAt(selectedRow, 4)));
            JTextField arrField = new JTextField(String.valueOf(flightsTableModel.getValueAt(selectedRow, 5)));
            JTextField typeField = new JTextField(String.valueOf(flightsTableModel.getValueAt(selectedRow, 6)));
            JTextField daysField = new JTextField(String.valueOf(flightsTableModel.getValueAt(selectedRow, 7)));
            JTextField aircraftField = new JTextField("1");
            JPanel p = new JPanel(new GridLayout(7, 2, 6, 6));
            p.add(new JLabel("From:")); p.add(fromField);
            p.add(new JLabel("To:")); p.add(toField);
            p.add(new JLabel("Departure (HH:MM:SS):")); p.add(depField);
            p.add(new JLabel("Arrival (HH:MM:SS):")); p.add(arrField);
            p.add(new JLabel("Type:")); p.add(typeField);
            p.add(new JLabel("Days:")); p.add(daysField);
            p.add(new JLabel("Aircraft ID:")); p.add(aircraftField);
            if (JOptionPane.showConfirmDialog(this, p, "Edit Flight " + airlineId + flightNo, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                Flight f = new Flight();
                f.setAirlineID(airlineId);
                f.setFlightNumber(flightNo);
                f.setDepartureAirportID(fromField.getText().trim());
                f.setArrivalAirportID(toField.getText().trim());
                f.setDepartureTime(Time.valueOf(depField.getText().trim().substring(0, 8)));
                f.setArrivalTime(Time.valueOf(arrField.getText().trim().substring(0, 8)));
                f.setType(typeField.getText().trim());
                f.setDaysOfWeek(daysField.getText().trim());
                f.setAircraftID(Integer.parseInt(aircraftField.getText().trim()));
                new FlightDAO().updateFlight(f);
                loadFlights();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error editing flight: " + ex.getMessage());
        }
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
            try {
                String airlineId = String.valueOf(flightsTableModel.getValueAt(selectedRow, 0));
                String flightNo = String.valueOf(flightsTableModel.getValueAt(selectedRow, 1));
                new FlightDAO().deleteFlight(airlineId, flightNo);
                loadFlights();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting flight: " + ex.getMessage());
            }
        }
    }

    private void loadFlightInstances() {
        try {
            flightInstanceCombo.removeAllItems();
            FlightInstanceDAO dao = new FlightInstanceDAO();
            for (FlightInstance fi : dao.getAllFlightInstances()) {
                flightInstanceCombo.addItem(fi.getFlightInstanceID() + " - " + fi.getFullFlightNumber() + " on " + fi.getDepartureDate());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading flight instances: " + ex.getMessage());
        }
    }

    private void loadWaitlistBySelectedInstance() {
        try {
            String selected = (String) flightInstanceCombo.getSelectedItem();
            if (selected == null) return;
            int id = Integer.parseInt(selected.split(" - ")[0]);
            WaitlistDAO dao = new WaitlistDAO();
            List<WaitlistEntry> entries = dao.getWaitlistByFlight(id);
            waitlistTableModel.setRowCount(0);
            for (WaitlistEntry e : entries) {
                waitlistTableModel.addRow(new Object[]{e.getCustomerName(), e.getFlightInfo(), e.getRequestTime()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading waitlist: " + ex.getMessage());
        }
    }

    private void editSelectedReservation(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select reservation first");
            return;
        }
        try {
            int reservationId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
            TicketDAO ticketDAO = new TicketDAO();
            List<Ticket> tickets = ticketDAO.getTicketsByReservation(reservationId);
            if (tickets.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No tickets in reservation");
                return;
            }
            Ticket t = tickets.get(0);
            JComboBox<String> classBox = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS", "FIRST"});
            classBox.setSelectedItem(t.getTicketClass());
            JTextField seatField = new JTextField(t.getSeatNumber());
            JPanel p = new JPanel(new GridLayout(2, 2, 6, 6));
            p.add(new JLabel("Ticket class:")); p.add(classBox);
            p.add(new JLabel("Seat number:")); p.add(seatField);
            if (JOptionPane.showConfirmDialog(this, p, "Edit Reservation #" + reservationId, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                for (Ticket tk : tickets) {
                    tk.setTicketClass((String) classBox.getSelectedItem());
                    tk.setSeatNumber(seatField.getText().trim());
                    ticketDAO.updateTicket(tk);
                }
                JOptionPane.showMessageDialog(this, "Reservation updated");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error editing reservation: " + ex.getMessage());
        }
    }

    private void loadQAs() {
        try {
            qaTableModel.setRowCount(0);
            QADAO dao = new QADAO();
            for (QA qa : dao.getAllQA()) {
                qaTableModel.addRow(new Object[]{
                    qa.getId(),
                    qa.getCustomerName(),
                    qa.getQuestion(),
                    qa.getAnswer() == null ? "Pending..." : qa.getAnswer()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading Q&A: " + ex.getMessage());
        }
    }

    private void replyToSelectedQA() {
        int row = qaTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a question first");
            return;
        }
        int qaId = Integer.parseInt(String.valueOf(qaTableModel.getValueAt(row, 0)));
        String answer = JOptionPane.showInputDialog(this, "Enter reply:");
        if (answer == null || answer.trim().isEmpty()) return;
        try {
            new QADAO().answerQuestion(qaId, answer.trim());
            loadQAs();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error replying to question: " + ex.getMessage());
        }
    }

    private int ensureFlightInstance(Flight flight, java.sql.Date departureDate, FlightInstanceDAO flightInstanceDAO) throws Exception {
        FlightDAO dao = new FlightDAO();
        List<FlightInstance> instances = dao.getFlightInstances(flight.getAirlineID(), flight.getFlightNumber(), departureDate);
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

    private void showManageAircraftDialog() {
        try {
            AircraftDAO dao = new AircraftDAO();
            List<Aircraft> all = dao.getAllAircraft();
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Model", "Capacity", "Airline"}, 0);
            for (Aircraft a : all) model.addRow(new Object[]{a.getAircraftID(), a.getModel(), a.getCapacity(), a.getAirlineID()});
            JTable t = new JTable(model);
            JScrollPane sp = new JScrollPane(t);
            Object[] options = {"Add", "Edit", "Delete", "Close"};
            while (true) {
                int choice = JOptionPane.showOptionDialog(this, sp, "Manage Aircraft", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[3]);
                if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) break;
                if (choice == 0) {
                    JTextField modelField = new JTextField();
                    JTextField capField = new JTextField();
                    JTextField airlineField = new JTextField();
                    JPanel p = new JPanel(new GridLayout(3, 2));
                    p.add(new JLabel("Model:")); p.add(modelField);
                    p.add(new JLabel("Capacity:")); p.add(capField);
                    p.add(new JLabel("AirlineID:")); p.add(airlineField);
                    if (JOptionPane.showConfirmDialog(this, p, "Add Aircraft", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        Aircraft a = new Aircraft();
                        a.setModel(modelField.getText().trim());
                        a.setCapacity(Integer.parseInt(capField.getText().trim()));
                        a.setAirlineID(airlineField.getText().trim());
                        dao.addAircraft(a);
                    }
                } else {
                    int r = t.getSelectedRow();
                    if (r < 0) continue;
                    int id = Integer.parseInt(String.valueOf(model.getValueAt(r, 0)));
                    if (choice == 2) dao.deleteAircraft(id);
                    if (choice == 1) {
                        Aircraft a = dao.getAircraftByID(id);
                        JTextField modelField = new JTextField(a.getModel());
                        JTextField capField = new JTextField(String.valueOf(a.getCapacity()));
                        JTextField airlineField = new JTextField(a.getAirlineID());
                        JPanel p = new JPanel(new GridLayout(3, 2));
                        p.add(new JLabel("Model:")); p.add(modelField);
                        p.add(new JLabel("Capacity:")); p.add(capField);
                        p.add(new JLabel("AirlineID:")); p.add(airlineField);
                        if (JOptionPane.showConfirmDialog(this, p, "Edit Aircraft", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                            a.setModel(modelField.getText().trim());
                            a.setCapacity(Integer.parseInt(capField.getText().trim()));
                            a.setAirlineID(airlineField.getText().trim());
                            dao.updateAircraft(a);
                        }
                    }
                }
                showManageAircraftDialog();
                break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error managing aircraft: " + ex.getMessage());
        }
    }

    private void showManageAirportsDialog() {
        try {
            AirportDAO dao = new AirportDAO();
            List<Airport> all = dao.getAllAirports();
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "City", "Country"}, 0);
            for (Airport a : all) model.addRow(new Object[]{a.getAirportID(), a.getName(), a.getCity(), a.getCountry()});
            JTable t = new JTable(model);
            JScrollPane sp = new JScrollPane(t);
            Object[] options = {"Add", "Edit", "Delete", "Close"};
            while (true) {
                int choice = JOptionPane.showOptionDialog(this, sp, "Manage Airports", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[3]);
                if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) break;
                if (choice == 0) {
                    JTextField idField = new JTextField();
                    JTextField nameField = new JTextField();
                    JTextField cityField = new JTextField();
                    JTextField countryField = new JTextField();
                    JPanel p = new JPanel(new GridLayout(4, 2));
                    p.add(new JLabel("Airport ID:")); p.add(idField);
                    p.add(new JLabel("Name:")); p.add(nameField);
                    p.add(new JLabel("City:")); p.add(cityField);
                    p.add(new JLabel("Country:")); p.add(countryField);
                    if (JOptionPane.showConfirmDialog(this, p, "Add Airport", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        Airport a = new Airport();
                        a.setAirportID(idField.getText().trim());
                        a.setName(nameField.getText().trim());
                        a.setCity(cityField.getText().trim());
                        a.setCountry(countryField.getText().trim());
                        dao.addAirport(a);
                    }
                } else {
                    int r = t.getSelectedRow();
                    if (r < 0) continue;
                    String id = String.valueOf(model.getValueAt(r, 0));
                    if (choice == 2) dao.deleteAirport(id);
                    if (choice == 1) {
                        Airport a = dao.getAirportByID(id);
                        JTextField nameField = new JTextField(a.getName());
                        JTextField cityField = new JTextField(a.getCity());
                        JTextField countryField = new JTextField(a.getCountry());
                        JPanel p = new JPanel(new GridLayout(3, 2));
                        p.add(new JLabel("Name:")); p.add(nameField);
                        p.add(new JLabel("City:")); p.add(cityField);
                        p.add(new JLabel("Country:")); p.add(countryField);
                        if (JOptionPane.showConfirmDialog(this, p, "Edit Airport", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                            a.setName(nameField.getText().trim());
                            a.setCity(cityField.getText().trim());
                            a.setCountry(countryField.getText().trim());
                            dao.updateAirport(a);
                        }
                    }
                }
                showManageAirportsDialog();
                break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error managing airports: " + ex.getMessage());
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