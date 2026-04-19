package test;

import dao.BookingDAO;
import exception.InvalidBookingException;
import model.Booking;
import org.junit.After;
import org.testng.annotations.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for BookingDAO using JUnit 4.
 */
public class BookingDAOTest {

    private static BookingDAO bookingDAO;

    @BeforeClass
    public static void beforeAll() {
        System.out.println("Setting up BookingDAO tests...");
        bookingDAO = new BookingDAO();
    }

    @Before
    public void beforeEach() {
        System.out.println("Starting BookingDAO test...");
    }

    @After
    public void afterEach() {
        System.out.println("BookingDAO test finished.");
    }

    @AfterClass
    public static void afterAll() {
        System.out.println("BookingDAO tests complete.");
    }

    @Test
    public void testGetBookingsByCustomer_InvalidID_ReturnsEmptyList() {
        List<Booking> bookings = bookingDAO.getBookingsByCustomer(-999);
        assertNotNull("Booking list should not be null", bookings);
        assertTrue("Invalid customer ID should return an empty list", bookings.isEmpty());
    }

    @Test
    public void testCancelBooking_InvalidID_ThrowsException() {
        try {
            bookingDAO.cancelBooking(-999);
            fail("Should have thrown InvalidBookingException for invalid booking ID");
        } catch (InvalidBookingException e) {
            assertTrue("Exception message should mention missing booking",
                e.getMessage().toLowerCase().contains("not found"));
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
            assertTrue("Exception message should mention seat selection",
                e.getMessage().toLowerCase().contains("seat"));
        } catch (SQLException e) {
            fail("Wrong exception type thrown: " + e.getClass().getName());
        }
    }
}
