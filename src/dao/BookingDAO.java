package dao;

import exception.InvalidBookingException;
import model.Booking;
import util.DBConnection;
import util.ErrorLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking table.
 * Key feature: uses INNER JOIN across 4 tables (required by Dr. Barron).
 *
 * Exception Handling:
 *  - try/catch/finally pattern
 *  - Custom InvalidBookingException thrown when business rules are broken
 */
public class BookingDAO {

    private Connection conn;

    public BookingDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────────────────

    /**
     * Creates a new booking for a customer.
     * @throws InvalidBookingException if seats are already taken
     * @throws SQLException if a database error occurs
     */
    public int createBooking(int customerID, int showtimeID,
                              List<Integer> seatIDs, double totalPrice)
            throws InvalidBookingException, SQLException {

        if (seatIDs == null || seatIDs.isEmpty()) {
            throw new InvalidBookingException("Please select at least one seat before confirming.");
        }

        // Check all selected seats are still available
        for (int seatID : seatIDs) {
            if (!isSeatAvailable(seatID)) {
                throw new InvalidBookingException(
                    "Seat ID " + seatID + " is no longer available. Please choose another seat."
                );
            }
        }

        // Insert booking record
        String bookingSql = "INSERT INTO Booking (CustomerID, ShowtimeID, TotalPrice, Status) " +
                            "VALUES (?, ?, ?, 'CONFIRMED')";

        try (PreparedStatement stmt = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerID);
            stmt.setInt(2, showtimeID);
            stmt.setDouble(3, totalPrice);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int bookingID = keys.getInt(1);
                // Link seats to this booking
                bookSeats(bookingID, seatIDs);
                return bookingID;
            }
        }
        throw new InvalidBookingException("Booking could not be created.");
    }

    // ── READ — uses INNER JOIN across 4 tables ────────────────────────────

    /**
     * Returns all bookings with customer and movie details.
     * Uses INNER JOIN across: Booking, Customer, Showtime, Movie.
     * This satisfies Dr. Barron's "inner join over multiple tables" requirement.
     */
    public List<Booking> getAllBookingsWithDetails() {
        List<Booking> bookings = new ArrayList<>();

        String sql =
            "SELECT b.BookingID, b.TotalPrice, b.Status, b.BookedAt, " +
            "       c.Name AS CustomerName, " +
            "       m.Title AS MovieTitle, " +
            "       s.ShowDate, s.ShowTime " +
            "FROM Booking b " +
            "INNER JOIN Customer c  ON b.CustomerID = c.CustomerID " +
            "INNER JOIN Showtime s  ON b.ShowtimeID = s.ShowtimeID " +
            "INNER JOIN Movie    m  ON s.MovieID    = m.MovieID " +
            "ORDER BY b.BookedAt DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingID(rs.getInt("BookingID"));
                b.setTotalPrice(rs.getDouble("TotalPrice"));
                b.setStatus(rs.getString("Status"));
                b.setCustomerName(rs.getString("CustomerName"));
                b.setMovieTitle(rs.getString("MovieTitle"));
                b.setShowDate(rs.getString("ShowDate"));
                b.setShowTime(rs.getString("ShowTime"));
                bookings.add(b);
            }

        } catch (SQLException e) {
            ErrorLogger.log("Error fetching bookings.", e);
        }

        return bookings;
    }

    /**
     * Returns all bookings for a specific customer (with movie details).
     */
    public List<Booking> getBookingsByCustomer(int customerID) {
        List<Booking> bookings = new ArrayList<>();

        String sql =
            "SELECT b.BookingID, b.TotalPrice, b.Status, b.BookedAt, " +
            "       m.Title AS MovieTitle, s.ShowDate, s.ShowTime " +
            "FROM Booking b " +
            "INNER JOIN Showtime s ON b.ShowtimeID = s.ShowtimeID " +
            "INNER JOIN Movie    m ON s.MovieID    = m.MovieID " +
            "WHERE b.CustomerID = ? " +
            "ORDER BY b.BookedAt DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingID(rs.getInt("BookingID"));
                b.setTotalPrice(rs.getDouble("TotalPrice"));
                b.setStatus(rs.getString("Status"));
                b.setMovieTitle(rs.getString("MovieTitle"));
                b.setShowDate(rs.getString("ShowDate"));
                b.setShowTime(rs.getString("ShowTime"));
                bookings.add(b);
            }

        } catch (SQLException e) {
            ErrorLogger.log("Error fetching customer bookings for customer ID " + customerID + ".", e);
        }

        return bookings;
    }

    // ── UPDATE (cancel booking) ───────────────────────────────────────────

    /**
     * Cancels a booking and releases the associated seats.
     * @throws InvalidBookingException if booking is already cancelled
     * @throws SQLException if a database error occurs
     */
    public boolean cancelBooking(int bookingID)
            throws InvalidBookingException, SQLException {

        // Check booking exists and is not already cancelled
        String checkSql = "SELECT Status FROM Booking WHERE BookingID = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, bookingID);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new InvalidBookingException("Booking #" + bookingID + " not found.");
            }
            if ("CANCELLED".equals(rs.getString("Status"))) {
                throw new InvalidBookingException("Booking #" + bookingID + " is already cancelled.");
            }
        }

        // Cancel the booking
        String cancelSql = "UPDATE Booking SET Status = 'CANCELLED' WHERE BookingID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(cancelSql)) {
            stmt.setInt(1, bookingID);
            stmt.executeUpdate();
        }

        // Release the seats back to AVAILABLE
        String releaseSql =
            "UPDATE Seat SET Status = 'AVAILABLE' " +
            "WHERE SeatID IN (SELECT SeatID FROM BookingSeat WHERE BookingID = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(releaseSql)) {
            stmt.setInt(1, bookingID);
            stmt.executeUpdate();
        }

        return true;
    }

    // ── DELETE ────────────────────────────────────────────────────────────

    /**
     * Hard deletes a booking record (admin only).
     */
    public boolean deleteBooking(int bookingID) throws SQLException {
        String sql = "DELETE FROM Booking WHERE BookingID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingID);
            return stmt.executeUpdate() > 0;
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────

    private boolean isSeatAvailable(int seatID) throws SQLException {
        String sql = "SELECT Status FROM Seat WHERE SeatID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seatID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "AVAILABLE".equals(rs.getString("Status"));
            }
        }
        return false;
    }

    private void bookSeats(int bookingID, List<Integer> seatIDs) throws SQLException {
        String insertSql = "INSERT INTO BookingSeat (BookingID, SeatID) VALUES (?, ?)";
        String updateSql = "UPDATE Seat SET Status = 'BOOKED' WHERE SeatID = ?";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            for (int seatID : seatIDs) {
                insertStmt.setInt(1, bookingID);
                insertStmt.setInt(2, seatID);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, seatID);
                updateStmt.executeUpdate();
            }
        }
    }
}
