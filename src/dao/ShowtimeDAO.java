package dao;

import exception.InvalidInputException;
import model.Showtime;
import util.DBConnection;
import util.ErrorLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Showtime table.
 * Full CRUD with exception handling.
 */
public class ShowtimeDAO {

    private Connection conn;

    public ShowtimeDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────────────────

    /**
     * Adds a new showtime and automatically generates seats for it.
     * @throws InvalidInputException if date, time or price are invalid
     * @throws SQLException if a database error occurs
     */
    public boolean addShowtime(int movieID, int hallID,
                               String showDate, String showTime, double ticketPrice)
            throws InvalidInputException, SQLException {

        // Validation
        if (showDate == null || showDate.trim().isEmpty()) {
            throw new InvalidInputException("Show date cannot be empty.");
        }
        if (showTime == null || showTime.trim().isEmpty()) {
            throw new InvalidInputException("Show time cannot be empty.");
        }
        if (ticketPrice <= 0) {
            throw new InvalidInputException("Ticket price must be greater than zero.");
        }

        String sql = "INSERT INTO Showtime (MovieID, HallID, ShowDate, ShowTime, TicketPrice) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, movieID);
            stmt.setInt(2, hallID);
            stmt.setString(3, showDate.trim());
            stmt.setString(4, showTime.trim());
            stmt.setDouble(5, ticketPrice);
            stmt.executeUpdate();

            // Get the new ShowtimeID to generate seats
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int showtimeID = keys.getInt(1);
                generateSeatsForShowtime(showtimeID, hallID);
            }
            return true;
        }
    }

    // ── READ ──────────────────────────────────────────────────────────────

    /**
     * Returns all showtimes with movie and hall names (INNER JOIN).
     */
    public List<Showtime> getAllShowtimes() {
        List<Showtime> showtimes = new ArrayList<>();

        String sql =
            "SELECT s.ShowtimeID, s.MovieID, s.HallID, s.ShowDate, s.ShowTime, s.TicketPrice, " +
            "       m.Title AS MovieTitle, h.HallName " +
            "FROM Showtime s " +
            "INNER JOIN Movie m ON s.MovieID = m.MovieID " +
            "INNER JOIN Hall  h ON s.HallID  = h.HallID " +
            "ORDER BY s.ShowDate, s.ShowTime";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                showtimes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            ErrorLogger.log("Error fetching showtimes.", e);
        }

        return showtimes;
    }

    /**
     * Returns showtimes for a specific movie (INNER JOIN with Hall).
     */
    public List<Showtime> getShowtimesByMovie(int movieID) {
        List<Showtime> showtimes = new ArrayList<>();

        String sql =
            "SELECT s.ShowtimeID, s.MovieID, s.HallID, s.ShowDate, s.ShowTime, s.TicketPrice, " +
            "       m.Title AS MovieTitle, h.HallName " +
            "FROM Showtime s " +
            "INNER JOIN Movie m ON s.MovieID = m.MovieID " +
            "INNER JOIN Hall  h ON s.HallID  = h.HallID " +
            "WHERE s.MovieID = ? " +
            "ORDER BY s.ShowDate, s.ShowTime";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                showtimes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            ErrorLogger.log("Error fetching showtimes for movie ID " + movieID + ".", e);
        }

        return showtimes;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────

    /**
     * Updates an existing showtime.
     * @throws InvalidInputException if inputs are invalid
     * @throws SQLException if a database error occurs
     */
    public boolean updateShowtime(int showtimeID, int movieID, int hallID,
                                  String showDate, String showTime, double ticketPrice)
            throws InvalidInputException, SQLException {

        if (showDate == null || showDate.trim().isEmpty()) {
            throw new InvalidInputException("Show date cannot be empty.");
        }
        if (showTime == null || showTime.trim().isEmpty()) {
            throw new InvalidInputException("Show time cannot be empty.");
        }
        if (ticketPrice <= 0) {
            throw new InvalidInputException("Ticket price must be greater than zero.");
        }

        String sql = "UPDATE Showtime SET MovieID=?, HallID=?, ShowDate=?, ShowTime=?, TicketPrice=? " +
                     "WHERE ShowtimeID=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            stmt.setInt(2, hallID);
            stmt.setString(3, showDate.trim());
            stmt.setString(4, showTime.trim());
            stmt.setDouble(5, ticketPrice);
            stmt.setInt(6, showtimeID);
            return stmt.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────

    /**
     * Deletes a showtime (seats are deleted automatically via CASCADE).
     * @throws SQLException if a database error occurs
     */
    public boolean deleteShowtime(int showtimeID) throws SQLException {
        String sql = "DELETE FROM Showtime WHERE ShowtimeID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            return stmt.executeUpdate() > 0;
        }
    }

    // ── HELPER: generate seats ────────────────────────────────────────────

    /**
     * Automatically generates seat rows A-Z based on hall layout.
     * Called when a new showtime is created.
     */
    private void generateSeatsForShowtime(int showtimeID, int hallID) throws SQLException {
        // Get hall layout
        String hallSql = "SELECT `Rows`, SeatsPerRow FROM Hall WHERE HallID = ?";
        int rows = 0, seatsPerRow = 0;

        try (PreparedStatement stmt = conn.prepareStatement(hallSql)) {
            stmt.setInt(1, hallID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rows        = rs.getInt("Rows");
                seatsPerRow = rs.getInt("SeatsPerRow");
            }
        }

        // Generate seats: rows A, B, C... and seat numbers 1, 2, 3...
        String insertSql = "INSERT INTO Seat (ShowtimeID, RowLabel, SeatNumber, Status) " +
                           "VALUES (?, ?, ?, 'AVAILABLE')";

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (int r = 0; r < rows; r++) {
                String rowLabel = String.valueOf((char) ('A' + r)); // A, B, C...
                for (int s = 1; s <= seatsPerRow; s++) {
                    stmt.setInt(1, showtimeID);
                    stmt.setString(2, rowLabel);
                    stmt.setInt(3, s);
                    stmt.executeUpdate();
                }
            }
        }
    }

    // ── MAP ROW ───────────────────────────────────────────────────────────

    private Showtime mapRow(ResultSet rs) throws SQLException {
        Showtime s = new Showtime(
            rs.getInt("ShowtimeID"),
            rs.getInt("MovieID"),
            rs.getInt("HallID"),
            rs.getString("ShowDate"),
            rs.getString("ShowTime"),
            rs.getDouble("TicketPrice")
        );
        s.setMovieTitle(rs.getString("MovieTitle"));
        s.setHallName(rs.getString("HallName"));
        return s;
    }
}
