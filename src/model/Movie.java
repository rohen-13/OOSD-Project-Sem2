package model;

import java.io.Serializable;

/**
 * Represents a Movie in the cinema system.
 * Implements Serializable so it can be written to a file using Streams.
 * (Dr. Barron's Streams slides: ObjectOutputStream / Serializable)
 */
public class Movie implements Serializable {

    private int    movieID;
    private String title;
    private String description;
    private int    duration;   // in minutes
    private String rating;     // e.g. PG, 12A, 18
    private String genre;
    private boolean isActive;

    // ── Constructors ─────────────────────────────────────────────────────

    public Movie() {}

    public Movie(int movieID, String title, String description,
                 int duration, String rating, String genre, boolean isActive) {
        this.movieID     = movieID;
        this.title       = title;
        this.description = description;
        this.duration    = duration;
        this.rating      = rating;
        this.genre       = genre;
        this.isActive    = isActive;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int     getMovieID()     { return movieID; }
    public String  getTitle()       { return title; }
    public String  getDescription() { return description; }
    public int     getDuration()    { return duration; }
    public String  getRating()      { return rating; }
    public String  getGenre()       { return genre; }
    public boolean isActive()       { return isActive; }

    public void setMovieID(int movieID)         { this.movieID = movieID; }
    public void setTitle(String title)          { this.title = title; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setDuration(int duration)       { this.duration = duration; }
    public void setRating(String rating)        { this.rating = rating; }
    public void setGenre(String genre)          { this.genre = genre; }
    public void setActive(boolean active)       { this.isActive = active; }

    // Used by JTable / JComboBox to display movie title automatically
    @Override
    public String toString() {
        return title + " (" + rating + ", " + duration + " mins)";
    }
}
