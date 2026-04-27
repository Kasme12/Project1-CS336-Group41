-- Travel Reservation System Database Initialization Script
-- Run this script in MySQL Workbench to create and populate the database

CREATE DATABASE IF NOT EXISTS travel_reservation;
USE travel_reservation;

-- Drop tables in reverse order due to foreign keys
DROP TABLE IF EXISTS waits_for;
DROP TABLE IF EXISTS includes;
DROP TABLE IF EXISTS manages;
DROP TABLE IF EXISTS Ticket;
DROP TABLE IF EXISTS Reservation;
DROP TABLE IF EXISTS Customer_Representative;
DROP TABLE IF EXISTS Admin;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Flight_Instance;
DROP TABLE IF EXISTS operates_at;
DROP TABLE IF EXISTS Flight;
DROP TABLE IF EXISTS Aircraft;
DROP TABLE IF EXISTS Airport;
DROP TABLE IF EXISTS Airline;

-- Create tables
CREATE TABLE Airline(
    AirlineID VARCHAR(2) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE Airport(
    AirportID VARCHAR(3) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100),
    country VARCHAR(100)
);

CREATE TABLE Aircraft(
    aircraftID INT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100),
    capacity INT NOT NULL,
    AirlineID VARCHAR(2),
    FOREIGN KEY (AirlineID) REFERENCES Airline(AirlineID)
);

CREATE TABLE Flight(
    AirlineID VARCHAR(2),
    flight_number VARCHAR(10),
    departure_AirportID VARCHAR(3),
    arrival_AirportID VARCHAR(3),
    aircraftID INT,
    departure_time TIME,
    arrival_time TIME,
    days_of_week VARCHAR(20),
    type ENUM('domestic','international'),

    PRIMARY KEY (AirlineID, flight_number),

    FOREIGN KEY (AirlineID) REFERENCES Airline(AirlineID),
    FOREIGN KEY (departure_AirportID) REFERENCES Airport(AirportID),
    FOREIGN KEY (arrival_AirportID) REFERENCES Airport(AirportID),
    FOREIGN KEY (aircraftID) REFERENCES Aircraft(aircraftID)
);

CREATE TABLE Flight_Instance(
    flight_instance_id INT AUTO_INCREMENT PRIMARY KEY,
    AirlineID VARCHAR(2),
    flight_number VARCHAR(10),
    departure_date DATE NOT NULL,
    actual_departure_time DATETIME,
    actual_arrival_time DATETIME,

    FOREIGN KEY (AirlineID, flight_number)
        REFERENCES Flight(AirlineID, flight_number)
);


CREATE TABLE Customer(
    customerID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE Employee(
    employeeID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE Admin(
    employeeID INT PRIMARY KEY,
    FOREIGN KEY (employeeID) REFERENCES Employee(employeeID)
);

CREATE TABLE Customer_Representative(
    employeeID INT PRIMARY KEY,
    FOREIGN KEY (employeeID) REFERENCES Employee(employeeID)
);


CREATE TABLE Reservation(
    reservationID INT AUTO_INCREMENT PRIMARY KEY,
    customerID INT,
    created_at DATETIME NOT NULL,
    status ENUM('CONFIRMED','CANCELLED','WAITLISTED'),

    FOREIGN KEY (customerID) REFERENCES Customer(customerID)
);

CREATE TABLE Ticket(
    ticket_number VARCHAR(15) PRIMARY KEY,
    reservationID INT,
    customerID INT,

    total_fare DECIMAL(10,2),
    booking_fee DECIMAL(10,2),
    purchase_datetime DATETIME,
    class ENUM('ECONOMY','BUSINESS','FIRST'),
    seat_number VARCHAR(10),
    special_meal VARCHAR(50),

    FOREIGN KEY (reservationID) REFERENCES Reservation(reservationID),
    FOREIGN KEY (customerID) REFERENCES Customer(customerID)
);

CREATE TABLE manages(
    employeeID INT,
    reservationID INT,
    PRIMARY KEY (employeeID, reservationID),
    FOREIGN KEY (employeeID) REFERENCES Employee(employeeID),
    FOREIGN KEY (reservationID) REFERENCES Reservation(reservationID)
);

CREATE TABLE includes(
    ticket_number VARCHAR(15),
    flight_instance_id INT,
    sequence_number INT,

    PRIMARY KEY (ticket_number, flight_instance_id, sequence_number),

    FOREIGN KEY (ticket_number) REFERENCES Ticket(ticket_number),
    FOREIGN KEY (flight_instance_id) REFERENCES Flight_Instance(flight_instance_id)
);

CREATE TABLE waits_for(
    customerID INT,
    flight_instance_id INT,
    request_time DATETIME,

    PRIMARY KEY (customerID, flight_instance_id),

    FOREIGN KEY (customerID) REFERENCES Customer(customerID),
    FOREIGN KEY (flight_instance_id) REFERENCES Flight_Instance(flight_instance_id)
);

CREATE TABLE operates_at(
    AirlineID VARCHAR(2),
    AirportID VARCHAR(3),

    PRIMARY KEY (AirlineID, AirportID),

    FOREIGN KEY (AirlineID) REFERENCES Airline(AirlineID),
    FOREIGN KEY (AirportID) REFERENCES Airport(AirportID)
);

-- Insert sample data
-- Airlines
INSERT INTO Airline (AirlineID, name) VALUES ('AA', 'American Airlines');
INSERT INTO Airline (AirlineID, name) VALUES ('UA', 'United Airlines');
INSERT INTO Airline (AirlineID, name) VALUES ('DL', 'Delta Airlines');
INSERT INTO Airline (AirlineID, name) VALUES ('SW', 'Southwest Airlines');
INSERT INTO Airline (AirlineID, name) VALUES ('JB', 'JetBlue Airways');

-- Airports
INSERT INTO Airport (AirportID, name, city, country) VALUES ('JFK', 'John F. Kennedy International', 'New York', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('LGA', 'LaGuardia Airport', 'New York', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('EWR', 'Newark Liberty International', 'Newark', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('LAX', 'Los Angeles International', 'Los Angeles', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('ORD', 'O''Hare International', 'Chicago', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('MIA', 'Miami International', 'Miami', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('SFO', 'San Francisco International', 'San Francisco', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('BOS', 'Logan International', 'Boston', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('DFW', 'Dallas/Fort Worth International', 'Dallas', 'USA');
INSERT INTO Airport (AirportID, name, city, country) VALUES ('ATL', 'Hartsfield-Jackson Atlanta', 'Atlanta', 'USA');

-- Aircraft
INSERT INTO Aircraft (model, capacity, AirlineID) VALUES ('Boeing 737', 150, 'AA');
INSERT INTO Aircraft (model, capacity, AirlineID) VALUES ('Boeing 747', 400, 'AA');
INSERT INTO Aircraft (model, capacity, AirlineID) VALUES ('Airbus A320', 160, 'UA');
INSERT INTO Aircraft (model, capacity, AirlineID) VALUES ('Boeing 777', 350, 'UA');
INSERT INTO Aircraft (model, capacity, AirlineID) VALUES ('Airbus A321', 180, 'DL');
INSERT INTO Aircraft (model, capacity, AirlineID) VALUES ('Boeing 737', 140, 'SW');
INSERT INTO Aircraft (model, capacity, AirlineID) VALUES ('Airbus A320', 150, 'JB');

-- Flights
INSERT INTO Flight VALUES ('AA', '100', 'JFK', 'LAX', 1, '08:00:00', '11:30:00', 'Mon,Wed,Fri', 'domestic');
INSERT INTO Flight VALUES ('AA', '101', 'LAX', 'JFK', 1, '14:00:00', '22:30:00', 'Tue,Thu,Sat', 'domestic');
INSERT INTO Flight VALUES ('UA', '200', 'JFK', 'SFO', 3, '09:00:00', '12:30:00', 'Daily', 'domestic');
INSERT INTO Flight VALUES ('UA', '201', 'SFO', 'JFK', 3, '15:00:00', '23:30:00', 'Daily', 'domestic');
INSERT INTO Flight VALUES ('DL', '300', 'ATL', 'MIA', 5, '07:00:00', '09:30:00', 'Daily', 'domestic');
INSERT INTO Flight VALUES ('DL', '301', 'MIA', 'ATL', 5, '10:30:00', '13:00:00', 'Daily', 'domestic');
INSERT INTO Flight VALUES ('SW', '400', 'ORD', 'DEN', 6, '06:30:00', '08:00:00', 'Daily', 'domestic');
INSERT INTO Flight VALUES ('SW', '401', 'DEN', 'ORD', 6, '09:00:00', '10:30:00', 'Daily', 'domestic');
INSERT INTO Flight VALUES ('JB', '500', 'BOS', 'LAX', 7, '10:00:00', '13:30:00', 'Mon,Wed,Fri,Sat', 'domestic');
INSERT INTO Flight VALUES ('JB', '501', 'LAX', 'BOS', 7, '14:30:00', '22:00:00', 'Mon,Wed,Fri,Sat', 'domestic');

-- Flight Instances (sample dates)
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('AA', '100', '2026-05-01', '2026-05-01 08:00:00', '2026-05-01 11:30:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('AA', '100', '2026-05-03', '2026-05-03 08:00:00', '2026-05-03 11:30:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('AA', '100', '2026-05-05', '2026-05-05 08:00:00', '2026-05-05 11:30:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('UA', '200', '2026-05-01', '2026-05-01 09:00:00', '2026-05-01 12:30:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('UA', '200', '2026-05-02', '2026-05-02 09:00:00', '2026-05-02 12:30:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('DL', '300', '2026-05-01', '2026-05-01 07:00:00', '2026-05-01 09:30:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('DL', '300', '2026-05-02', '2026-05-02 07:00:00', '2026-05-02 09:30:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('SW', '400', '2026-05-01', '2026-05-01 06:30:00', '2026-05-01 08:00:00');
INSERT INTO Flight_Instance (AirlineID, flight_number, departure_date, actual_departure_time, actual_arrival_time) 
VALUES ('JB', '500', '2026-05-01', '2026-05-01 10:00:00', '2026-05-01 13:30:00');

-- Customers
INSERT INTO Customer (name, email, phone) VALUES ('John Smith', 'john.smith@email.com', '555-0001');
INSERT INTO Customer (name, email, phone) VALUES ('Jane Doe', 'jane.doe@email.com', '555-0002');
INSERT INTO Customer (name, email, phone) VALUES ('Bob Johnson', 'bob.johnson@email.com', '555-0003');
INSERT INTO Customer (name, email, phone) VALUES ('Alice Williams', 'alice.williams@email.com', '555-0004');
INSERT INTO Customer (name, email, phone) VALUES ('Charlie Brown', 'charlie.brown@email.com', '555-0005');
INSERT INTO Customer (name, email, phone) VALUES ('Diana Prince', 'diana.prince@email.com', '555-0006');
INSERT INTO Customer (name, email, phone) VALUES ('Edward Norton', 'edward.norton@email.com', '555-0007');
INSERT INTO Customer (name, email, phone) VALUES ('Fiona Apple', 'fiona.apple@email.com', '555-0008');

-- Employees
INSERT INTO Employee (name) VALUES ('Admin User');
INSERT INTO Employee (name) VALUES ('Agent Smith');
INSERT INTO Employee (name) VALUES ('Agent Jones');
INSERT INTO Employee (name) VALUES ('Agent Davis');

-- Admin
INSERT INTO Admin (employeeID) VALUES (1);

-- Customer Representatives
INSERT INTO Customer_Representative (employeeID) VALUES (2);
INSERT INTO Customer_Representative (employeeID) VALUES (3);
INSERT INTO Customer_Representative (employeeID) VALUES (4);

-- Sample Reservations
INSERT INTO Reservation (customerID, created_at, status) VALUES (1, '2026-04-15 10:00:00', 'CONFIRMED');
INSERT INTO Reservation (customerID, created_at, status) VALUES (2, '2026-04-16 11:00:00', 'CONFIRMED');
INSERT INTO Reservation (customerID, created_at, status) VALUES (3, '2026-04-17 12:00:00', 'CONFIRMED');
INSERT INTO Reservation (customerID, created_at, status) VALUES (4, '2026-04-18 13:00:00', 'CANCELLED');

-- Sample Tickets
INSERT INTO Ticket (ticket_number, reservationID, customerID, total_fare, booking_fee, purchase_datetime, class, seat_number, special_meal) 
VALUES ('TK00001', 1, 1, 350.00, 35.00, '2026-04-15 10:05:00', 'ECONOMY', '12A', 'None');
INSERT INTO Ticket (ticket_number, reservationID, customerID, total_fare, booking_fee, purchase_datetime, class, seat_number, special_meal) 
VALUES ('TK00002', 2, 2, 800.00, 80.00, '2026-04-16 11:10:00', 'BUSINESS', '5C', 'Vegetarian');
INSERT INTO Ticket (ticket_number, reservationID, customerID, total_fare, booking_fee, purchase_datetime, class, seat_number, special_meal) 
VALUES ('TK00003', 3, 3, 1200.00, 120.00, '2026-04-17 12:15:00', 'FIRST', '1A', 'Kosher');

-- Insert into includes
INSERT INTO includes (ticket_number, flight_instance_id, sequence_number) VALUES ('TK00001', 1, 1);
INSERT INTO includes (ticket_number, flight_instance_id, sequence_number) VALUES ('TK00002', 4, 1);
INSERT INTO includes (ticket_number, flight_instance_id, sequence_number) VALUES ('TK00003', 6, 1);

-- Waitlist sample
INSERT INTO waits_for (customerID, flight_instance_id, request_time) VALUES (5, 2, '2026-04-20 14:00:00');