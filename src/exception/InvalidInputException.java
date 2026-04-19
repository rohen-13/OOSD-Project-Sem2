package exception;

/**
 * Custom exception thrown when user input fails validation.
 * e.g. letters in a number field, invalid email format, empty required fields.
 *
 * Dr. Barron's slides: "Declaring New Exception Types" (slide 48-49)
 */
public class InvalidInputException extends Exception {

    public InvalidInputException() {
        super("Invalid input provided.");
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
