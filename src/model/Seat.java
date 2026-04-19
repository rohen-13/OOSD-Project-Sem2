package model;

import java.io.Serializable;

/**
 * Represents an individual seat for a specific showtime.
 */
public class Seat implements Serializable {

    private int    seatID;
    private int    showtimeID;
    private String rowLabel;    // e.g. A, B, C
    private int    seatNumber;
    private String status;      // AVAILABLE, RESERVED, BOOKED

    // ── Constructors ──────────────────────────────────────────────────────

    public Seat() {}

    public Seat(int seatID, int showtimeID, String rowLabel,
                int seatNumber, String status) {
        this.seatID     = seatID;
        this.showtimeID = showtimeID;
        this.rowLabel   = rowLabel;
        this.seatNumber = seatNumber;
        this.status     = status;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int    getSeatID()     { return seatID; }
    public int    getShowtimeID() { return showtimeID; }
    public String getRowLabel()   { return rowLabel; }
    public int    getSeatNumber() { return seatNumber; }
    public String getStatus()     { return status; }

    public void setSeatID(int id)         { this.seatID = id; }
    public void setShowtimeID(int id)     { this.showtimeID = id; }
    public void setRowLabel(String row)   { this.rowLabel = row; }
    public void setSeatNumber(int num)    { this.seatNumber = num; }
    public void setStatus(String status)  { this.status = status; }

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }

    @Override
    public String toString() {
        return rowLabel + seatNumber + " (" + status + ")";
    }
}
