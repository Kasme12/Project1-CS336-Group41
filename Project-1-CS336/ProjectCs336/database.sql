CREATE DATABASE  IF NOT EXISTS `travel_reservation` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `travel_reservation`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: travel_reservation
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `employeeID` int NOT NULL,
  PRIMARY KEY (`employeeID`),
  CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`employeeID`) REFERENCES `employee` (`employeeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aircraft`
--

DROP TABLE IF EXISTS `aircraft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aircraft` (
  `aircraftID` int NOT NULL AUTO_INCREMENT,
  `model` varchar(100) DEFAULT NULL,
  `capacity` int NOT NULL,
  `AirlineID` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`aircraftID`),
  KEY `AirlineID` (`AirlineID`),
  CONSTRAINT `aircraft_ibfk_1` FOREIGN KEY (`AirlineID`) REFERENCES `airline` (`AirlineID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aircraft`
--

LOCK TABLES `aircraft` WRITE;
/*!40000 ALTER TABLE `aircraft` DISABLE KEYS */;
INSERT INTO `aircraft` VALUES (1,'Boeing 737',150,'AA'),(2,'Boeing 747',400,'AA'),(3,'Airbus A320',160,'UA'),(4,'Boeing 777',350,'UA'),(5,'Airbus A321',180,'DL'),(6,'Boeing 737',140,'SW'),(7,'Airbus A320',150,'JB');
/*!40000 ALTER TABLE `aircraft` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `airline`
--

DROP TABLE IF EXISTS `airline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airline` (
  `AirlineID` varchar(2) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`AirlineID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airline`
--

LOCK TABLES `airline` WRITE;
/*!40000 ALTER TABLE `airline` DISABLE KEYS */;
INSERT INTO `airline` VALUES ('AA','American Airlines'),('DL','Delta Airlines'),('JB','JetBlue Airways'),('SW','Southwest Airlines'),('UA','United Airlines');
/*!40000 ALTER TABLE `airline` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `airport`
--

DROP TABLE IF EXISTS `airport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airport` (
  `AirportID` varchar(3) NOT NULL,
  `name` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`AirportID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airport`
--

LOCK TABLES `airport` WRITE;
/*!40000 ALTER TABLE `airport` DISABLE KEYS */;
INSERT INTO `airport` VALUES ('ATL','Hartsfield-Jackson Atlanta','Atlanta','USA'),('BOS','Logan International','Boston','USA'),('DFW','Dallas/Fort Worth International','Dallas','USA'),('EWR','Newark Liberty International','Newark','USA'),('JFK','John F. Kennedy International','New York','USA'),('LAX','Los Angeles International','Los Angeles','USA'),('LGA','LaGuardia Airport','New York','USA'),('MIA','Miami International','Miami','USA'),('ORD','O\'Hare International','Chicago','USA'),('SFO','San Francisco International','San Francisco','USA');
/*!40000 ALTER TABLE `airport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customerID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`customerID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'a','a','a');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_representative`
--

DROP TABLE IF EXISTS `customer_representative`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_representative` (
  `employeeID` int NOT NULL,
  PRIMARY KEY (`employeeID`),
  CONSTRAINT `customer_representative_ibfk_1` FOREIGN KEY (`employeeID`) REFERENCES `employee` (`employeeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_representative`
--

LOCK TABLES `customer_representative` WRITE;
/*!40000 ALTER TABLE `customer_representative` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_representative` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `employeeID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`employeeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight`
--

DROP TABLE IF EXISTS `flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight` (
  `AirlineID` varchar(2) NOT NULL,
  `flight_number` varchar(10) NOT NULL,
  `departure_AirportID` varchar(3) DEFAULT NULL,
  `arrival_AirportID` varchar(3) DEFAULT NULL,
  `aircraftID` int DEFAULT NULL,
  `departure_time` time DEFAULT NULL,
  `arrival_time` time DEFAULT NULL,
  `days_of_week` varchar(20) DEFAULT NULL,
  `type` enum('domestic','international') DEFAULT NULL,
  PRIMARY KEY (`AirlineID`,`flight_number`),
  KEY `departure_AirportID` (`departure_AirportID`),
  KEY `arrival_AirportID` (`arrival_AirportID`),
  KEY `aircraftID` (`aircraftID`),
  CONSTRAINT `flight_ibfk_1` FOREIGN KEY (`AirlineID`) REFERENCES `airline` (`AirlineID`),
  CONSTRAINT `flight_ibfk_2` FOREIGN KEY (`departure_AirportID`) REFERENCES `airport` (`AirportID`),
  CONSTRAINT `flight_ibfk_3` FOREIGN KEY (`arrival_AirportID`) REFERENCES `airport` (`AirportID`),
  CONSTRAINT `flight_ibfk_4` FOREIGN KEY (`aircraftID`) REFERENCES `aircraft` (`aircraftID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight`
--

LOCK TABLES `flight` WRITE;
/*!40000 ALTER TABLE `flight` DISABLE KEYS */;
INSERT INTO `flight` VALUES ('AA','100','JFK','LAX',1,'08:00:00','11:30:00','Mon,Wed,Fri','domestic'),('AA','101','LAX','JFK',1,'14:00:00','22:30:00','Tue,Thu,Sat','domestic'),('DL','300','ATL','MIA',5,'07:00:00','09:30:00','Daily','domestic'),('DL','301','MIA','ATL',5,'10:30:00','13:00:00','Daily','domestic'),('UA','200','JFK','SFO',3,'09:00:00','12:30:00','Daily','domestic'),('UA','201','SFO','JFK',3,'15:00:00','23:30:00','Daily','domestic'),('AA','102','JFK','MIA',2,'06:30:00','09:20:00','Daily','domestic'),('AA','103','MIA','JFK',2,'18:15:00','21:10:00','Daily','domestic'),('DL','302','ATL','JFK',5,'08:15:00','10:35:00','Daily','domestic'),('DL','303','JFK','ATL',5,'12:10:00','14:40:00','Daily','domestic'),('UA','202','EWR','ORD',4,'07:20:00','09:00:00','Daily','domestic'),('UA','203','ORD','EWR',4,'17:40:00','20:55:00','Daily','domestic'),('SW','400','LGA','ATL',6,'09:45:00','12:15:00','Daily','domestic'),('SW','401','ATL','LGA',6,'13:20:00','15:50:00','Daily','domestic'),('JB','500','JFK','BOS',7,'07:05:00','08:20:00','Daily','domestic'),('JB','501','BOS','JFK',7,'19:10:00','20:25:00','Daily','domestic'),('AA','104','DFW','LAX',1,'11:10:00','12:50:00','Mon,Tue,Wed,Thu,Fri','domestic'),('AA','105','LAX','DFW',1,'16:15:00','21:00:00','Mon,Tue,Wed,Thu,Fri','domestic'),('DL','304','MIA','BOS',5,'06:50:00','09:55:00','Daily','domestic'),('DL','305','BOS','MIA',5,'15:05:00','18:25:00','Daily','domestic'),('UA','204','SFO','LAX',3,'08:40:00','10:05:00','Daily','domestic'),('UA','205','LAX','SFO',3,'20:05:00','21:25:00','Daily','domestic'),('SW','402','ORD','DFW',6,'10:30:00','12:50:00','Daily','domestic'),('SW','403','DFW','ORD',6,'18:20:00','20:35:00','Daily','domestic'),('JB','502','LAX','JFK',7,'08:30:00','16:55:00','Daily','domestic'),('JB','503','JFK','LAX',7,'17:45:00','21:20:00','Daily','domestic'),('AA','106','JFK','ORD',2,'07:15:00','09:05:00','Daily','domestic'),('AA','107','ORD','JFK',2,'19:45:00','22:40:00','Daily','domestic'),('DL','306','ATL','SFO',5,'09:30:00','12:40:00','Daily','domestic'),('DL','307','SFO','ATL',5,'13:50:00','21:30:00','Daily','domestic');
/*!40000 ALTER TABLE `flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight_instance`
--

DROP TABLE IF EXISTS `flight_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_instance` (
  `flight_instance_id` int NOT NULL AUTO_INCREMENT,
  `AirlineID` varchar(2) DEFAULT NULL,
  `flight_number` varchar(10) DEFAULT NULL,
  `departure_date` date NOT NULL,
  `actual_departure_time` datetime DEFAULT NULL,
  `actual_arrival_time` datetime DEFAULT NULL,
  PRIMARY KEY (`flight_instance_id`),
  KEY `AirlineID` (`AirlineID`,`flight_number`),
  CONSTRAINT `flight_instance_ibfk_1` FOREIGN KEY (`AirlineID`, `flight_number`) REFERENCES `flight` (`AirlineID`, `flight_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_instance`
--

LOCK TABLES `flight_instance` WRITE;
/*!40000 ALTER TABLE `flight_instance` DISABLE KEYS */;
INSERT INTO `flight_instance` VALUES (1,'AA','100','2026-05-05','2026-05-05 08:00:00','2026-05-05 11:30:00'),(2,'AA','101','2026-05-06','2026-05-06 14:00:00','2026-05-06 22:30:00'),(3,'UA','200','2026-05-05','2026-05-05 09:00:00','2026-05-05 12:30:00'),(4,'UA','201','2026-05-06','2026-05-06 15:00:00','2026-05-06 23:30:00'),(5,'DL','300','2026-05-05','2026-05-05 07:00:00','2026-05-05 09:30:00'),(6,'DL','301','2026-05-05','2026-05-05 10:30:00','2026-05-05 13:00:00'),(7,'JB','500','2026-05-05','2026-05-05 07:05:00','2026-05-05 08:20:00'),(8,'SW','400','2026-05-05','2026-05-05 09:45:00','2026-05-05 12:15:00');
/*!40000 ALTER TABLE `flight_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `includes`
--

DROP TABLE IF EXISTS `includes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `includes` (
  `ticket_number` varchar(15) NOT NULL,
  `flight_instance_id` int NOT NULL,
  `sequence_number` int NOT NULL,
  PRIMARY KEY (`ticket_number`,`flight_instance_id`,`sequence_number`),
  KEY `flight_instance_id` (`flight_instance_id`),
  CONSTRAINT `includes_ibfk_1` FOREIGN KEY (`ticket_number`) REFERENCES `ticket` (`ticket_number`),
  CONSTRAINT `includes_ibfk_2` FOREIGN KEY (`flight_instance_id`) REFERENCES `flight_instance` (`flight_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `includes`
--

LOCK TABLES `includes` WRITE;
/*!40000 ALTER TABLE `includes` DISABLE KEYS */;
/*!40000 ALTER TABLE `includes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manages`
--

DROP TABLE IF EXISTS `manages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manages` (
  `employeeID` int NOT NULL,
  `reservationID` int NOT NULL,
  PRIMARY KEY (`employeeID`,`reservationID`),
  KEY `reservationID` (`reservationID`),
  CONSTRAINT `manages_ibfk_1` FOREIGN KEY (`employeeID`) REFERENCES `employee` (`employeeID`),
  CONSTRAINT `manages_ibfk_2` FOREIGN KEY (`reservationID`) REFERENCES `reservation` (`reservationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manages`
--

LOCK TABLES `manages` WRITE;
/*!40000 ALTER TABLE `manages` DISABLE KEYS */;
/*!40000 ALTER TABLE `manages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operates_at`
--

DROP TABLE IF EXISTS `operates_at`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operates_at` (
  `AirlineID` varchar(2) NOT NULL,
  `AirportID` varchar(3) NOT NULL,
  PRIMARY KEY (`AirlineID`,`AirportID`),
  KEY `AirportID` (`AirportID`),
  CONSTRAINT `operates_at_ibfk_1` FOREIGN KEY (`AirlineID`) REFERENCES `airline` (`AirlineID`),
  CONSTRAINT `operates_at_ibfk_2` FOREIGN KEY (`AirportID`) REFERENCES `airport` (`AirportID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operates_at`
--

LOCK TABLES `operates_at` WRITE;
/*!40000 ALTER TABLE `operates_at` DISABLE KEYS */;
/*!40000 ALTER TABLE `operates_at` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qa`
--

DROP TABLE IF EXISTS `qa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qa` (
  `qa_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `question` text NOT NULL,
  `answer` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`qa_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `qa_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customerID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qa`
--

LOCK TABLES `qa` WRITE;
/*!40000 ALTER TABLE `qa` DISABLE KEYS */;
INSERT INTO `qa` VALUES (1,1,'hello?',NULL,'2026-05-04 14:57:35');
/*!40000 ALTER TABLE `qa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `reservationID` int NOT NULL AUTO_INCREMENT,
  `customerID` int DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `status` enum('CONFIRMED','CANCELLED','WAITLISTED') DEFAULT NULL,
  PRIMARY KEY (`reservationID`),
  KEY `customerID` (`customerID`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `customer` (`customerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
  `ticket_number` varchar(15) NOT NULL,
  `reservationID` int DEFAULT NULL,
  `customerID` int DEFAULT NULL,
  `total_fare` decimal(10,2) DEFAULT NULL,
  `booking_fee` decimal(10,2) DEFAULT NULL,
  `purchase_datetime` datetime DEFAULT NULL,
  `class` enum('ECONOMY','BUSINESS','FIRST') DEFAULT NULL,
  `seat_number` varchar(10) DEFAULT NULL,
  `special_meal` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ticket_number`),
  KEY `reservationID` (`reservationID`),
  KEY `customerID` (`customerID`),
  CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`reservationID`) REFERENCES `reservation` (`reservationID`),
  CONSTRAINT `ticket_ibfk_2` FOREIGN KEY (`customerID`) REFERENCES `customer` (`customerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `waits_for`
--

DROP TABLE IF EXISTS `waits_for`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `waits_for` (
  `customerID` int NOT NULL,
  `flight_instance_id` int NOT NULL,
  `request_time` datetime DEFAULT NULL,
  PRIMARY KEY (`customerID`,`flight_instance_id`),
  KEY `flight_instance_id` (`flight_instance_id`),
  CONSTRAINT `waits_for_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `customer` (`customerID`),
  CONSTRAINT `waits_for_ibfk_2` FOREIGN KEY (`flight_instance_id`) REFERENCES `flight_instance` (`flight_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `waits_for`
--

LOCK TABLES `waits_for` WRITE;
/*!40000 ALTER TABLE `waits_for` DISABLE KEYS */;
/*!40000 ALTER TABLE `waits_for` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-04 11:00:49
