package model;

import java.io.Serializable;

/**
 * Represents a Showtime — a specific screening of a Movie in a Hall.
 * Implements Serializable for file export via Streams.
 */
public class Showtime implements Serializable {

    private int    showtimeID;
    private int    movieID;
    private int    hallID;
    private String showDate;      // stored as String for easy display e.g. "2026-04-15"
    private String showTime;      // stored as String e.g. "19:00"
    private double ticketPrice;

    // Extra fields populated from JOIN queries
    private String movieTitle;
    private String hallName;

    // ── Constructors ──────────────────────────────────────────────────────

    public Showtime() {}

    public Showtime(int showtimeID, int movieID, int hallID,
                    String showDate, String showTime, double ticketPrice) {
        this.showtimeID  = showtimeID;
        this.movieID     = movieID;
        this.hallID      = hallID;
        this.showDate    = showDate;
        this.showTime    = showTime;
        this.ticketPrice = ticketPrice;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int    getShowtimeID()  { return showtimeID; }
    public int    getMovieID()     { return movieID; }
    public int    getHallID()      { return hallID; }
    public String getShowDate()    { return showDate; }
    public String getShowTime()    { return showTime; }
    public double getTicketPrice() { return ticketPrice; }
    public String getMovieTitle()  { return movieTitle; }
    public String getHallName()    { return hallName; }

    public void setShowtimeID(int id)          { this.showtimeID = id; }
    public void setMovieID(int id)             { this.movieID = id; }
    public void setHallID(int id)              { this.hallID = id; }
    public void setShowDate(String date)       { this.showDate = date; }
    public void setShowTime(String time)       { this.showTime = time; }
    public void setTicketPrice(double price)   { this.ticketPrice = price; }
    public void setMovieTitle(String title)    { this.movieTitle = title; }
    public void setHallName(String name)       { this.hallName = name; }

    // Used in JComboBox / JList to display showtime nicely
    @Override
    public String toString() {
        return showDate + " at " + showTime +
               " | " + hallName +
               " | EUR " + String.format("%.2f", ticketPrice);
    }
}
