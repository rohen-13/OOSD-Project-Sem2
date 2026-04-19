package exception;

/**
 * Custom exception thrown when a booking operation fails.
 * e.g. seat already taken, invalid showtime, payment error.
 *
 * Dr. Barron's slides: "Declaring New Exception Types" (slide 48-49)
 * A new exception class must extend an existing exception class.
 */
public class InvalidBookingException extends Exception {

    // Constructor 1: no message
    public InvalidBookingException() {
        super("Invalid booking operation.");
    }

    // Constructor 2: custom message
    public InvalidBookingException(String message) {
        super(message);
    }

    // Constructor 3: custom message + cause (for chained exceptions)
    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor 4: cause only (for chained exceptions)
    public InvalidBookingException(Throwable cause) {
        super(cause);
    }
}
