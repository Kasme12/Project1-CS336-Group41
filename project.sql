CREATE DATABASE IF NOT EXISTS travel_reservation;
USE travel_reservation;

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
