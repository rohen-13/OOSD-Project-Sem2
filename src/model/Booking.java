package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a Booking made by a Customer.
 * Implements Serializable for file export via Streams.
 */
public class Booking implements Serializable {

    private int           bookingID;
    private int           customerID;
    private int           showtimeID;
    private double        totalPrice;
    private String        status;       // "CONFIRMED" or "CANCELLED"
    private LocalDateTime bookedAt;

    // Extra fields for display (from JOIN queries)
    private String customerName;
    private String movieTitle;
    private String showDate;
    private String showTime;

    // ── Constructors ──────────────────────────────────────────────────────

    public Booking() {}

    public Booking(int bookingID, int customerID, int showtimeID,
                   double totalPrice, String status, LocalDateTime bookedAt) {
        this.bookingID  = bookingID;
        this.customerID = customerID;
        this.showtimeID = showtimeID;
        this.totalPrice = totalPrice;
        this.status     = status;
        this.bookedAt   = bookedAt;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int           getBookingID()    { return bookingID; }
    public int           getCustomerID()   { return customerID; }
    public int           getShowtimeID()   { return showtimeID; }
    public double        getTotalPrice()   { return totalPrice; }
    public String        getStatus()       { return status; }
    public LocalDateTime getBookedAt()     { return bookedAt; }
    public String        getCustomerName() { return customerName; }
    public String        getMovieTitle()   { return movieTitle; }
    public String        getShowDate()     { return showDate; }
    public String        getShowTime()     { return showTime; }

    public void setBookingID(int id)             { this.bookingID = id; }
    public void setCustomerID(int id)            { this.customerID = id; }
    public void setShowtimeID(int id)            { this.showtimeID = id; }
    public void setTotalPrice(double price)      { this.totalPrice = price; }
    public void setStatus(String status)         { this.status = status; }
    public void setBookedAt(LocalDateTime dt)    { this.bookedAt = dt; }
    public void setCustomerName(String name)     { this.customerName = name; }
    public void setMovieTitle(String title)      { this.movieTitle = title; }
    public void setShowDate(String date)         { this.showDate = date; }
    public void setShowTime(String time)         { this.showTime = time; }

    @Override
    public String toString() {
        return "Booking #" + bookingID + " | " + movieTitle + " | €" + totalPrice + " | " + status;
    }
}
