package dao;

import model.Seat;
import util.DBConnection;
import util.ErrorLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Seat table.
 */
public class SeatDAO {

    private Connection conn;

    public SeatDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Returns all seats for a specific showtime.
     */
    public List<Seat> getSeatsByShowtime(int showtimeID) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seat WHERE ShowtimeID = ? ORDER BY RowLabel, SeatNumber";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seats.add(new Seat(
                    rs.getInt("SeatID"),
                    rs.getInt("ShowtimeID"),
                    rs.getString("RowLabel"),
                    rs.getInt("SeatNumber"),
                    rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            ErrorLogger.log("Error fetching seats for showtime ID " + showtimeID + ".", e);
        }

        return seats;
    }

    /**
     * Returns available seat count for a showtime.
     */
    public int getAvailableSeatsCount(int showtimeID) {
        String sql = "SELECT COUNT(*) FROM Seat WHERE ShowtimeID = ? AND Status = 'AVAILABLE'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showtimeID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            ErrorLogger.log("Error counting available seats for showtime ID " + showtimeID + ".", e);
        }
        return 0;
    }
}
