package test;

import dao.MovieDAO;
import exception.InvalidInputException;
import model.Movie;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit tests for MovieDAO using JUnit 4.
 *
 * Dr. Barron's Unit Testing slides:
 *  - @BeforeClass runs once before all tests (like @BeforeAll in JUnit 5)
 *  - @Before runs before each test (like @BeforeEach in JUnit 5)
 *  - @After runs after each test
 *  - @AfterClass runs once after all tests
 *  - @Test marks each test method
 *  - assertEquals, assertNotNull, assertNull, assertFalse for assertions
 */
public class MovieDAOTest {

    private static MovieDAO movieDAO;

    @BeforeClass
    public static void beforeAll() {
        System.out.println("Setting up database connection...");
        movieDAO = new MovieDAO();
    }

    @Before
    public void beforeEach() {
        System.out.println("Starting next test...");
    }

    @After
    public void afterEach() {
        System.out.println("Test finished.");
    }

    @AfterClass
    public static void afterAll() {
        System.out.println("All tests complete.");
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    public void testGetAllMovies_ReturnsNonNullList() {
        List<Movie> movies = movieDAO.getAllMovies();
        assertNotNull("Movie list should not be null", movies);
    }

    @Test
    public void testGetAllMovies_NotEmpty() {
        List<Movie> movies = movieDAO.getAllMovies();
        assertFalse("Movie list should not be empty", movies.isEmpty());
    }

    @Test
    public void testGetMovieByID_InvalidID_ReturnsNull() {
        Movie movie = movieDAO.getMovieByID(-999);
        assertNull("Non-existent ID should return null", movie);
    }

    @Test
    public void testAddMovie_EmptyTitle_ThrowsException() {
        try {
            movieDAO.addMovie("", "Description", 120, "12A", "Action");
            fail("Should have thrown InvalidInputException for empty title");
        } catch (InvalidInputException e) {
            assertNotNull("Exception message should not be null", e.getMessage());
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
