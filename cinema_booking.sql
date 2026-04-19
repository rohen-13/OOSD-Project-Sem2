-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 19, 2026 at 09:23 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cinema_booking`
--

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

CREATE TABLE `booking` (
  `BookingID` int(11) NOT NULL,
  `CustomerID` int(11) NOT NULL,
  `ShowtimeID` int(11) NOT NULL,
  `TotalPrice` decimal(8,2) NOT NULL,
  `Status` enum('CONFIRMED','CANCELLED') DEFAULT 'CONFIRMED',
  `BookedAt` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `booking`
--

INSERT INTO `booking` (`BookingID`, `CustomerID`, `ShowtimeID`, `TotalPrice`, `Status`, `BookedAt`) VALUES
(1, 4, 6, 25.00, 'CANCELLED', '2026-04-11 19:02:11'),
(2, 4, 6, 25.00, 'CANCELLED', '2026-04-11 19:49:37'),
(3, 4, 6, 50.00, 'CANCELLED', '2026-04-11 21:21:33'),
(4, 5, 6, 25.00, 'CANCELLED', '2026-04-11 21:22:13'),
(5, 4, 6, 37.50, 'CANCELLED', '2026-04-11 23:22:25'),
(6, 4, 6, 25.00, 'CONFIRMED', '2026-04-13 17:26:03'),
(7, 4, 8, 14.00, 'CONFIRMED', '2026-04-15 04:05:46'),
(8, 4, 6, 25.00, 'CONFIRMED', '2026-04-16 15:42:06'),
(9, 4, 8, 28.00, 'CONFIRMED', '2026-04-16 15:44:34'),
(10, 4, 7, 58.00, 'CONFIRMED', '2026-04-16 15:49:33'),
(11, 4, 8, 14.00, 'CONFIRMED', '2026-04-19 18:33:13'),
(12, 4, 7, 14.50, 'CONFIRMED', '2026-04-19 19:06:23');

-- --------------------------------------------------------

--
-- Table structure for table `bookingseat`
--

CREATE TABLE `bookingseat` (
  `BookingSeatID` int(11) NOT NULL,
  `BookingID` int(11) NOT NULL,
  `SeatID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bookingseat`
--

INSERT INTO `bookingseat` (`BookingSeatID`, `BookingID`, `SeatID`) VALUES
(1, 1, 35),
(2, 1, 36),
(3, 2, 35),
(4, 2, 36),
(5, 3, 23),
(6, 3, 24),
(7, 3, 25),
(8, 3, 26),
(9, 4, 34),
(10, 4, 35),
(11, 5, 34),
(12, 5, 35),
(13, 5, 45),
(14, 6, 44),
(15, 6, 54),
(16, 7, 145),
(18, 8, 36),
(17, 8, 46),
(20, 9, 135),
(19, 9, 144),
(21, 10, 85),
(22, 10, 95),
(23, 10, 105),
(24, 10, 115),
(25, 11, 146),
(26, 12, 106);

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `CustomerID` int(11) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Email` varchar(150) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT current_timestamp(),
  `IsAdmin` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`CustomerID`, `Name`, `Email`, `Password`, `Phone`, `CreatedAt`, `IsAdmin`) VALUES
(1, 'Admin User', 'admin@cinema.com', 'admin123', '0851234567', '2026-04-11 13:31:21', 1),
(2, 'John Doe', 'john@example.com', 'password123', '0879876543', '2026-04-11 13:31:21', 0),
(3, 'Jane Smith', 'jane@example.com', 'password456', '0861112233', '2026-04-11 13:31:21', 0),
(4, 'Olekasndr Storozhuk', 'storozhuk@gmail.com', '123456', '', '2026-04-11 14:26:34', 0),
(5, 'Oleksandr Hrynkin', 'sasagrinkin@gmail.com', '123456', '', '2026-04-11 19:56:55', 0);

-- --------------------------------------------------------

--
-- Table structure for table `hall`
--

CREATE TABLE `hall` (
  `HallID` int(11) NOT NULL,
  `HallName` varchar(50) NOT NULL,
  `TotalSeats` int(11) NOT NULL,
  `Rows` int(11) NOT NULL,
  `SeatsPerRow` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hall`
--

INSERT INTO `hall` (`HallID`, `HallName`, `TotalSeats`, `Rows`, `SeatsPerRow`) VALUES
(1, 'Hall 1', 60, 6, 10),
(2, 'Hall 2', 40, 4, 10),
(3, 'Hall 3', 80, 8, 10);

-- --------------------------------------------------------

--
-- Table structure for table `movie`
--

CREATE TABLE `movie` (
  `MovieID` int(11) NOT NULL,
  `Title` varchar(150) NOT NULL,
  `Description` text DEFAULT NULL,
  `Duration` int(11) NOT NULL,
  `Rating` varchar(10) DEFAULT NULL,
  `Genre` varchar(50) DEFAULT NULL,
  `PosterURL` varchar(255) DEFAULT NULL,
  `IsActive` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `movie`
--

INSERT INTO `movie` (`MovieID`, `Title`, `Description`, `Duration`, `Rating`, `Genre`, `PosterURL`, `IsActive`) VALUES
(1, 'Inception', 'A thief who steals corporate secrets through dream-sharing technology.', 148, '12A', 'Sci-Fi', NULL, 1),
(2, 'The Dark Knight', 'Batman faces the Joker in a battle for Gotham City.', 152, '12A', 'Action', NULL, 1),
(3, 'Interstellar', 'A team of explorers travel through a wormhole in space.', 169, '12A', 'Sci-Fi', NULL, 1),
(4, 'Oppenheimer', 'The story of the development of the atomic bomb.', 180, 'G', 'Drama', NULL, 1),
(6, 'Project Hail Mary', 'Project Hail Mary is a science fiction novel (and 2026 film) by Andy Weir about Ryland Grace, a science teacher turned astronaut who wakes up alone on a spaceship with amnesia. He discovers he is on a desperate, last-chance mission to save Earth from an alien microorganism, \"Astrophage,\" that is consuming the sun\'s energy.', 156, 'PG', 'Sci-Fi, Action', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `seat`
--

CREATE TABLE `seat` (
  `SeatID` int(11) NOT NULL,
  `ShowtimeID` int(11) NOT NULL,
  `RowLabel` char(1) NOT NULL,
  `SeatNumber` int(11) NOT NULL,
  `Status` enum('AVAILABLE','RESERVED','BOOKED') DEFAULT 'AVAILABLE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `seat`
--

INSERT INTO `seat` (`SeatID`, `ShowtimeID`, `RowLabel`, `SeatNumber`, `Status`) VALUES
(1, 6, 'A', 1, 'AVAILABLE'),
(2, 6, 'A', 2, 'AVAILABLE'),
(3, 6, 'A', 3, 'AVAILABLE'),
(4, 6, 'A', 4, 'AVAILABLE'),
(5, 6, 'A', 5, 'AVAILABLE'),
(6, 6, 'A', 6, 'AVAILABLE'),
(7, 6, 'A', 7, 'AVAILABLE'),
(8, 6, 'A', 8, 'AVAILABLE'),
(9, 6, 'A', 9, 'AVAILABLE'),
(10, 6, 'A', 10, 'AVAILABLE'),
(11, 6, 'B', 1, 'AVAILABLE'),
(12, 6, 'B', 2, 'AVAILABLE'),
(13, 6, 'B', 3, 'AVAILABLE'),
(14, 6, 'B', 4, 'AVAILABLE'),
(15, 6, 'B', 5, 'AVAILABLE'),
(16, 6, 'B', 6, 'AVAILABLE'),
(17, 6, 'B', 7, 'AVAILABLE'),
(18, 6, 'B', 8, 'AVAILABLE'),
(19, 6, 'B', 9, 'AVAILABLE'),
(20, 6, 'B', 10, 'AVAILABLE'),
(21, 6, 'C', 1, 'AVAILABLE'),
(22, 6, 'C', 2, 'AVAILABLE'),
(23, 6, 'C', 3, 'AVAILABLE'),
(24, 6, 'C', 4, 'AVAILABLE'),
(25, 6, 'C', 5, 'AVAILABLE'),
(26, 6, 'C', 6, 'AVAILABLE'),
(27, 6, 'C', 7, 'AVAILABLE'),
(28, 6, 'C', 8, 'AVAILABLE'),
(29, 6, 'C', 9, 'AVAILABLE'),
(30, 6, 'C', 10, 'AVAILABLE'),
(31, 6, 'D', 1, 'AVAILABLE'),
(32, 6, 'D', 2, 'AVAILABLE'),
(33, 6, 'D', 3, 'AVAILABLE'),
(34, 6, 'D', 4, 'AVAILABLE'),
(35, 6, 'D', 5, 'AVAILABLE'),
(36, 6, 'D', 6, 'BOOKED'),
(37, 6, 'D', 7, 'AVAILABLE'),
(38, 6, 'D', 8, 'AVAILABLE'),
(39, 6, 'D', 9, 'AVAILABLE'),
(40, 6, 'D', 10, 'AVAILABLE'),
(41, 6, 'E', 1, 'AVAILABLE'),
(42, 6, 'E', 2, 'AVAILABLE'),
(43, 6, 'E', 3, 'AVAILABLE'),
(44, 6, 'E', 4, 'BOOKED'),
(45, 6, 'E', 5, 'AVAILABLE'),
(46, 6, 'E', 6, 'BOOKED'),
(47, 6, 'E', 7, 'AVAILABLE'),
(48, 6, 'E', 8, 'AVAILABLE'),
(49, 6, 'E', 9, 'AVAILABLE'),
(50, 6, 'E', 10, 'AVAILABLE'),
(51, 6, 'F', 1, 'AVAILABLE'),
(52, 6, 'F', 2, 'AVAILABLE'),
(53, 6, 'F', 3, 'AVAILABLE'),
(54, 6, 'F', 4, 'BOOKED'),
(55, 6, 'F', 5, 'AVAILABLE'),
(56, 6, 'F', 6, 'AVAILABLE'),
(57, 6, 'F', 7, 'AVAILABLE'),
(58, 6, 'F', 8, 'AVAILABLE'),
(59, 6, 'F', 9, 'AVAILABLE'),
(60, 6, 'F', 10, 'AVAILABLE'),
(61, 6, 'G', 1, 'AVAILABLE'),
(62, 6, 'G', 2, 'AVAILABLE'),
(63, 6, 'G', 3, 'AVAILABLE'),
(64, 6, 'G', 4, 'AVAILABLE'),
(65, 6, 'G', 5, 'AVAILABLE'),
(66, 6, 'G', 6, 'AVAILABLE'),
(67, 6, 'G', 7, 'AVAILABLE'),
(68, 6, 'G', 8, 'AVAILABLE'),
(69, 6, 'G', 9, 'AVAILABLE'),
(70, 6, 'G', 10, 'AVAILABLE'),
(71, 6, 'H', 1, 'AVAILABLE'),
(72, 6, 'H', 2, 'AVAILABLE'),
(73, 6, 'H', 3, 'AVAILABLE'),
(74, 6, 'H', 4, 'AVAILABLE'),
(75, 6, 'H', 5, 'AVAILABLE'),
(76, 6, 'H', 6, 'AVAILABLE'),
(77, 6, 'H', 7, 'AVAILABLE'),
(78, 6, 'H', 8, 'AVAILABLE'),
(79, 6, 'H', 9, 'AVAILABLE'),
(80, 6, 'H', 10, 'AVAILABLE'),
(81, 7, 'A', 1, 'AVAILABLE'),
(82, 7, 'A', 2, 'AVAILABLE'),
(83, 7, 'A', 3, 'AVAILABLE'),
(84, 7, 'A', 4, 'AVAILABLE'),
(85, 7, 'A', 5, 'BOOKED'),
(86, 7, 'A', 6, 'AVAILABLE'),
(87, 7, 'A', 7, 'AVAILABLE'),
(88, 7, 'A', 8, 'AVAILABLE'),
(89, 7, 'A', 9, 'AVAILABLE'),
(90, 7, 'A', 10, 'AVAILABLE'),
(91, 7, 'B', 1, 'AVAILABLE'),
(92, 7, 'B', 2, 'AVAILABLE'),
(93, 7, 'B', 3, 'AVAILABLE'),
(94, 7, 'B', 4, 'AVAILABLE'),
(95, 7, 'B', 5, 'BOOKED'),
(96, 7, 'B', 6, 'AVAILABLE'),
(97, 7, 'B', 7, 'AVAILABLE'),
(98, 7, 'B', 8, 'AVAILABLE'),
(99, 7, 'B', 9, 'AVAILABLE'),
(100, 7, 'B', 10, 'AVAILABLE'),
(101, 7, 'C', 1, 'AVAILABLE'),
(102, 7, 'C', 2, 'AVAILABLE'),
(103, 7, 'C', 3, 'AVAILABLE'),
(104, 7, 'C', 4, 'AVAILABLE'),
(105, 7, 'C', 5, 'BOOKED'),
(106, 7, 'C', 6, 'BOOKED'),
(107, 7, 'C', 7, 'AVAILABLE'),
(108, 7, 'C', 8, 'AVAILABLE'),
(109, 7, 'C', 9, 'AVAILABLE'),
(110, 7, 'C', 10, 'AVAILABLE'),
(111, 7, 'D', 1, 'AVAILABLE'),
(112, 7, 'D', 2, 'AVAILABLE'),
(113, 7, 'D', 3, 'AVAILABLE'),
(114, 7, 'D', 4, 'AVAILABLE'),
(115, 7, 'D', 5, 'BOOKED'),
(116, 7, 'D', 6, 'AVAILABLE'),
(117, 7, 'D', 7, 'AVAILABLE'),
(118, 7, 'D', 8, 'AVAILABLE'),
(119, 7, 'D', 9, 'AVAILABLE'),
(120, 7, 'D', 10, 'AVAILABLE'),
(121, 8, 'A', 1, 'AVAILABLE'),
(122, 8, 'A', 2, 'AVAILABLE'),
(123, 8, 'A', 3, 'AVAILABLE'),
(124, 8, 'A', 4, 'AVAILABLE'),
(125, 8, 'A', 5, 'AVAILABLE'),
(126, 8, 'A', 6, 'AVAILABLE'),
(127, 8, 'A', 7, 'AVAILABLE'),
(128, 8, 'A', 8, 'AVAILABLE'),
(129, 8, 'A', 9, 'AVAILABLE'),
(130, 8, 'A', 10, 'AVAILABLE'),
(131, 8, 'B', 1, 'AVAILABLE'),
(132, 8, 'B', 2, 'AVAILABLE'),
(133, 8, 'B', 3, 'AVAILABLE'),
(134, 8, 'B', 4, 'AVAILABLE'),
(135, 8, 'B', 5, 'BOOKED'),
(136, 8, 'B', 6, 'AVAILABLE'),
(137, 8, 'B', 7, 'AVAILABLE'),
(138, 8, 'B', 8, 'AVAILABLE'),
(139, 8, 'B', 9, 'AVAILABLE'),
(140, 8, 'B', 10, 'AVAILABLE'),
(141, 8, 'C', 1, 'AVAILABLE'),
(142, 8, 'C', 2, 'AVAILABLE'),
(143, 8, 'C', 3, 'AVAILABLE'),
(144, 8, 'C', 4, 'BOOKED'),
(145, 8, 'C', 5, 'BOOKED'),
(146, 8, 'C', 6, 'BOOKED'),
(147, 8, 'C', 7, 'AVAILABLE'),
(148, 8, 'C', 8, 'AVAILABLE'),
(149, 8, 'C', 9, 'AVAILABLE'),
(150, 8, 'C', 10, 'AVAILABLE'),
(151, 8, 'D', 1, 'AVAILABLE'),
(152, 8, 'D', 2, 'AVAILABLE'),
(153, 8, 'D', 3, 'AVAILABLE'),
(154, 8, 'D', 4, 'AVAILABLE'),
(155, 8, 'D', 5, 'AVAILABLE'),
(156, 8, 'D', 6, 'AVAILABLE'),
(157, 8, 'D', 7, 'AVAILABLE'),
(158, 8, 'D', 8, 'AVAILABLE'),
(159, 8, 'D', 9, 'AVAILABLE'),
(160, 8, 'D', 10, 'AVAILABLE'),
(161, 9, 'A', 1, 'AVAILABLE'),
(162, 9, 'A', 2, 'AVAILABLE'),
(163, 9, 'A', 3, 'AVAILABLE'),
(164, 9, 'A', 4, 'AVAILABLE'),
(165, 9, 'A', 5, 'AVAILABLE'),
(166, 9, 'A', 6, 'AVAILABLE'),
(167, 9, 'A', 7, 'AVAILABLE'),
(168, 9, 'A', 8, 'AVAILABLE'),
(169, 9, 'A', 9, 'AVAILABLE'),
(170, 9, 'A', 10, 'AVAILABLE'),
(171, 9, 'B', 1, 'AVAILABLE'),
(172, 9, 'B', 2, 'AVAILABLE'),
(173, 9, 'B', 3, 'AVAILABLE'),
(174, 9, 'B', 4, 'AVAILABLE'),
(175, 9, 'B', 5, 'AVAILABLE'),
(176, 9, 'B', 6, 'AVAILABLE'),
(177, 9, 'B', 7, 'AVAILABLE'),
(178, 9, 'B', 8, 'AVAILABLE'),
(179, 9, 'B', 9, 'AVAILABLE'),
(180, 9, 'B', 10, 'AVAILABLE'),
(181, 9, 'C', 1, 'AVAILABLE'),
(182, 9, 'C', 2, 'AVAILABLE'),
(183, 9, 'C', 3, 'AVAILABLE'),
(184, 9, 'C', 4, 'AVAILABLE'),
(185, 9, 'C', 5, 'AVAILABLE'),
(186, 9, 'C', 6, 'AVAILABLE'),
(187, 9, 'C', 7, 'AVAILABLE'),
(188, 9, 'C', 8, 'AVAILABLE'),
(189, 9, 'C', 9, 'AVAILABLE'),
(190, 9, 'C', 10, 'AVAILABLE'),
(191, 9, 'D', 1, 'AVAILABLE'),
(192, 9, 'D', 2, 'AVAILABLE'),
(193, 9, 'D', 3, 'AVAILABLE'),
(194, 9, 'D', 4, 'AVAILABLE'),
(195, 9, 'D', 5, 'AVAILABLE'),
(196, 9, 'D', 6, 'AVAILABLE'),
(197, 9, 'D', 7, 'AVAILABLE'),
(198, 9, 'D', 8, 'AVAILABLE'),
(199, 9, 'D', 9, 'AVAILABLE'),
(200, 9, 'D', 10, 'AVAILABLE');

-- --------------------------------------------------------

--
-- Table structure for table `showtime`
--

CREATE TABLE `showtime` (
  `ShowtimeID` int(11) NOT NULL,
  `MovieID` int(11) NOT NULL,
  `HallID` int(11) NOT NULL,
  `ShowDate` date NOT NULL,
  `ShowTime` time NOT NULL,
  `TicketPrice` decimal(6,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `showtime`
--

INSERT INTO `showtime` (`ShowtimeID`, `MovieID`, `HallID`, `ShowDate`, `ShowTime`, `TicketPrice`) VALUES
(1, 1, 1, '2026-03-05', '14:00:00', 12.50),
(2, 1, 2, '2026-03-05', '19:00:00', 14.00),
(3, 2, 1, '2026-03-06', '16:00:00', 12.50),
(4, 3, 3, '2026-03-07', '18:30:00', 13.00),
(5, 4, 2, '2026-03-08', '20:00:00', 14.50),
(6, 2, 3, '2026-03-07', '17:00:00', 12.50),
(7, 4, 2, '2026-03-08', '22:00:00', 14.50),
(8, 1, 2, '2026-03-05', '11:00:00', 14.00),
(9, 2, 2, '2026-10-06', '16:00:00', 12.50);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `booking`
--
ALTER TABLE `booking`
  ADD PRIMARY KEY (`BookingID`),
  ADD KEY `CustomerID` (`CustomerID`),
  ADD KEY `ShowtimeID` (`ShowtimeID`);

--
-- Indexes for table `bookingseat`
--
ALTER TABLE `bookingseat`
  ADD PRIMARY KEY (`BookingSeatID`),
  ADD UNIQUE KEY `BookingID` (`BookingID`,`SeatID`),
  ADD KEY `SeatID` (`SeatID`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`CustomerID`),
  ADD UNIQUE KEY `Email` (`Email`);

--
-- Indexes for table `hall`
--
ALTER TABLE `hall`
  ADD PRIMARY KEY (`HallID`);

--
-- Indexes for table `movie`
--
ALTER TABLE `movie`
  ADD PRIMARY KEY (`MovieID`);

--
-- Indexes for table `seat`
--
ALTER TABLE `seat`
  ADD PRIMARY KEY (`SeatID`),
  ADD UNIQUE KEY `ShowtimeID` (`ShowtimeID`,`RowLabel`,`SeatNumber`);

--
-- Indexes for table `showtime`
--
ALTER TABLE `showtime`
  ADD PRIMARY KEY (`ShowtimeID`),
  ADD KEY `MovieID` (`MovieID`),
  ADD KEY `HallID` (`HallID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `booking`
--
ALTER TABLE `booking`
  MODIFY `BookingID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `bookingseat`
--
ALTER TABLE `bookingseat`
  MODIFY `BookingSeatID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `CustomerID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `hall`
--
ALTER TABLE `hall`
  MODIFY `HallID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `movie`
--
ALTER TABLE `movie`
  MODIFY `MovieID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `seat`
--
ALTER TABLE `seat`
  MODIFY `SeatID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=201;

--
-- AUTO_INCREMENT for table `showtime`
--
ALTER TABLE `showtime`
  MODIFY `ShowtimeID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `booking`
--
ALTER TABLE `booking`
  ADD CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`CustomerID`) ON DELETE CASCADE,
  ADD CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`ShowtimeID`) REFERENCES `showtime` (`ShowtimeID`) ON DELETE CASCADE;

--
-- Constraints for table `bookingseat`
--
ALTER TABLE `bookingseat`
  ADD CONSTRAINT `bookingseat_ibfk_1` FOREIGN KEY (`BookingID`) REFERENCES `booking` (`BookingID`) ON DELETE CASCADE,
  ADD CONSTRAINT `bookingseat_ibfk_2` FOREIGN KEY (`SeatID`) REFERENCES `seat` (`SeatID`) ON DELETE CASCADE;

--
-- Constraints for table `seat`
--
ALTER TABLE `seat`
  ADD CONSTRAINT `seat_ibfk_1` FOREIGN KEY (`ShowtimeID`) REFERENCES `showtime` (`ShowtimeID`) ON DELETE CASCADE;

--
-- Constraints for table `showtime`
--
ALTER TABLE `showtime`
  ADD CONSTRAINT `showtime_ibfk_1` FOREIGN KEY (`MovieID`) REFERENCES `movie` (`MovieID`) ON DELETE CASCADE,
  ADD CONSTRAINT `showtime_ibfk_2` FOREIGN KEY (`HallID`) REFERENCES `hall` (`HallID`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
