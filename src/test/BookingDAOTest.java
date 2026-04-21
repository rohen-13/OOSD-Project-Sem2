package test;

import dao.BookingDAO;
import exception.InvalidBookingException;
import model.Booking;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookingDAO using JUnit 5.
 */
public class BookingDAOTest {

    private static BookingDAO bookingDAO;

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Setting up BookingDAO tests...");
        bookingDAO = new BookingDAO();
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("Starting BookingDAO test...");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("BookingDAO test finished.");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("BookingDAO tests complete.");
    }

    @Test
    public void testGetBookingsByCustomer_InvalidID_ReturnsEmptyList() {
        List<Booking> bookings = bookingDAO.getBookingsByCustomer(-999);
        assertNotNull(bookings, "Booking list should not be null");
        assertTrue(bookings.isEmpty(), "Invalid customer ID should return an empty list");
    }

    @Test
    public void testCancelBooking_InvalidID_ThrowsException() {
        try {
            bookingDAO.cancelBooking(-999);
            fail("Should have thrown InvalidBookingException for invalid booking ID");
        } catch (InvalidBookingException e) {
            assertTrue(e.getMessage().toLowerCase().contains("not found"),
                "Exception message should mention missing booking");
        } catch (SQLException e) {
            fail("Wrong exception type thrown: " + e.getClass().getName());
        }
    }

    @Test
    public void testCreateBooking_NoSeatsSelected_ThrowsException() {
        try {
            bookingDAO.createBooking(1, 1, Collections.emptyList(), 0.0);
            fail("Should have thrown InvalidBookingException for empty seat selection");
        } catch (InvalidBookingException e) {
            assertTrue(e.getMessage().toLowerCase().contains("seat"),
                "Exception message should mention seat selection");
        } catch (SQLException e) {
            fail("Wrong exception type thrown: " + e.getClass().getName());
        }
    }
}
