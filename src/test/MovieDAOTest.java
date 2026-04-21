package test;

import dao.MovieDAO;
import exception.InvalidInputException;
import model.Movie;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for MovieDAO using JUnit 5.
 */
public class MovieDAOTest {

    private static MovieDAO movieDAO;

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Setting up database connection...");
        movieDAO = new MovieDAO();
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("Starting next test...");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("Test finished.");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("All tests complete.");
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    public void testGetAllMovies_ReturnsNonNullList() {
        List<Movie> movies = movieDAO.getAllMovies();
        assertNotNull(movies, "Movie list should not be null");
    }

    @Test
    public void testGetAllMovies_NotEmpty() {
        List<Movie> movies = movieDAO.getAllMovies();
        assertFalse(movies.isEmpty(), "Movie list should not be empty");
    }

    @Test
    public void testGetMovieByID_InvalidID_ReturnsNull() {
        Movie movie = movieDAO.getMovieByID(-999);
        assertNull(movie, "Non-existent ID should return null");
    }

    @Test
    public void testAddMovie_EmptyTitle_ThrowsException() {
        try {
            movieDAO.addMovie("", "Description", 120, "12A", "Action");
            fail("Should have thrown InvalidInputException for empty title");
        } catch (InvalidInputException e) {
            assertNotNull(e.getMessage(), "Exception message should not be null");
        } catch (Exception e) {
            fail("Wrong exception type thrown: " + e.getClass().getName());
        }
    }

    @Test
    public void testAddMovie_ZeroDuration_ThrowsException() {
        try {
            movieDAO.addMovie("Test Movie", "Description", 0, "PG", "Drama");
            fail("Should have thrown InvalidInputException for zero duration");
        } catch (InvalidInputException e) {
            assertNotNull(e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void testAddMovie_NegativeDuration_ThrowsException() {
        try {
            movieDAO.addMovie("Test Movie", "Description", -10, "PG", "Drama");
            fail("Should have thrown InvalidInputException for negative duration");
        } catch (InvalidInputException e) {
            assertNotNull(e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void testAddMovie_EmptyRating_ThrowsException() {
        try {
            movieDAO.addMovie("Test Movie", "Description", 120, "", "Sci-Fi");
            fail("Should have thrown InvalidInputException for empty rating");
        } catch (InvalidInputException e) {
            assertNotNull(e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void testUpdateMovie_EmptyTitle_ThrowsException() {
        try {
            movieDAO.updateMovie(1, "", "Desc", 120, "PG", "Drama");
            fail("Should have thrown InvalidInputException for empty title");
        } catch (InvalidInputException e) {
            assertNotNull(e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getName());
        }
    }

    @Test
    public void testUpdateMovie_NegativeDuration_ThrowsException() {
        try {
            movieDAO.updateMovie(1, "Valid Title", "Desc", -5, "PG", "Drama");
            fail("Should have thrown InvalidInputException for negative duration");
        } catch (InvalidInputException e) {
            assertNotNull(e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getName());
        }
    }
}
