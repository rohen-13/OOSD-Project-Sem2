package dao;

import exception.InvalidInputException;
import model.Movie;
import util.DBConnection;
import util.ErrorLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Movie table.
 * Provides full CRUD operations with proper Exception Handling.
 *
 * Dr. Barron's Exception Handling slides:
 *  - try/catch blocks around all DB operations
 *  - throws clause on methods that can fail
 *  - Custom exceptions (InvalidInputException)
 *  - finally block to close resources
 */
public class MovieDAO {

    private Connection conn;

    public MovieDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────────────────

    /**
     * Adds a new movie to the database.
     * @throws InvalidInputException if any required field is empty or duration is less than or equal to 0
     * @throws SQLException if a database error occurs
     */
    public boolean addMovie(String title, String description,
                            int duration, String rating, String genre)
            throws InvalidInputException, SQLException {

        // Input validation — throws custom exception (Dr. Barron slide 48)
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Movie title cannot be empty.");
        }
        if (duration <= 0) {
            throw new InvalidInputException("Duration must be a positive number. Got: " + duration);
        }
        if (rating == null || rating.trim().isEmpty()) {
            throw new InvalidInputException("Rating cannot be empty.");
        }

        String sql = "INSERT INTO Movie (Title, Description, Duration, Rating, Genre, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, TRUE)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title.trim());
            stmt.setString(2, description);
            stmt.setInt(3, duration);
            stmt.setString(4, rating.trim());
            stmt.setString(5, genre);
            return stmt.executeUpdate() > 0;
        }
        // SQLException propagates — caller (UI) will catch and show error dialog
    }

    // ── READ (all active movies) ──────────────────────────────────────────

    /**
     * Returns all active movies from the database.
     */
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movie WHERE IsActive = TRUE ORDER BY Title";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                movies.add(mapRow(rs));
            }

        } catch (SQLException e) {
            // Log the error but return empty list — UI shows "no movies found"
            ErrorLogger.log("Error fetching movies.", e);
        }

        return movies;
    }

    /**
     * Returns a single movie by its ID.
     */
    public Movie getMovieByID(int movieID) {
        String sql = "SELECT * FROM Movie WHERE MovieID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            ErrorLogger.log("Error fetching movie ID " + movieID + ".", e);
        }

        return null;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────

    /**
     * Updates an existing movie's details.
     * @throws InvalidInputException if title is empty or duration is invalid
     * @throws SQLException if a database error occurs
     */
    public boolean updateMovie(int movieID, String title, String description,
                               int duration, String rating, String genre)
            throws InvalidInputException, SQLException {

        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Movie title cannot be empty.");
        }
        if (duration <= 0) {
            throw new InvalidInputException("Duration must be greater than zero. Got: " + duration);
        }

        String sql = "UPDATE Movie SET Title=?, Description=?, Duration=?, Rating=?, Genre=? " +
                     "WHERE MovieID=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title.trim());
            stmt.setString(2, description);
            stmt.setInt(3, duration);
            stmt.setString(4, rating.trim());
            stmt.setString(5, genre);
            stmt.setInt(6, movieID);
            return stmt.executeUpdate() > 0;
        }
    }

    // ── DELETE (soft delete — sets IsActive = FALSE) ──────────────────────

    /**
     * Soft-deletes a movie by setting IsActive = FALSE.
     * This preserves existing bookings that reference this movie.
     * @throws SQLException if a database error occurs
     */
    public boolean deleteMovie(int movieID) throws SQLException {
        String sql = "UPDATE Movie SET IsActive = FALSE WHERE MovieID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieID);
            return stmt.executeUpdate() > 0;
        }
    }

    // ── HELPER ────────────────────────────────────────────────────────────

    /**
     * Maps a ResultSet row to a Movie object.
     * Centralised mapping avoids code duplication.
     */
    private Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
            rs.getInt("MovieID"),
            rs.getString("Title"),
            rs.getString("Description"),
            rs.getInt("Duration"),
            rs.getString("Rating"),
            rs.getString("Genre"),
            rs.getBoolean("IsActive")
        );
    }
}
