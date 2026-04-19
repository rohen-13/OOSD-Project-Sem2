package util;

import model.Booking;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Writes booking reports to a .txt file using Streams.
 *
 * Dr. Barron's Streams slides:
 *  - FileWriter = character-based output stream (slide 6)
 *  - BufferedWriter wraps it for efficiency
 *  - try-with-resources ensures the stream is closed (slide 32-34)
 *  - IOException must be caught (checked exception)
 */
public class BookingReportWriter {

    private static final String REPORT_FILE = "booking_report.txt";

    /**
     * Writes all bookings to a text file.
     * Uses FileWriter (character-based stream) as taught in Streams lectures.
     *
     * @param bookings list of bookings to write
     * @throws IOException if the file cannot be written
     */
    public static void writeReport(List<Booking> bookings) throws IOException {

        // try-with-resources automatically closes the stream (like finally block)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILE))) {

            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Write header
            writer.write("========================================");
            writer.newLine();
            writer.write("  CINEMA BOOKING SYSTEM - REPORT");
            writer.newLine();
            writer.write("  Generated: " + timestamp);
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            if (bookings.isEmpty()) {
                writer.write("No bookings found.");
                writer.newLine();
            } else {
                // Column headers
                writer.write(String.format("%-10s %-20s %-25s %-12s %-10s %-12s",
                    "BookingID", "Customer", "Movie", "Date", "Time", "Total (€)"));
                writer.newLine();
                writer.write("-".repeat(90));
                writer.newLine();

                // Write each booking as a formatted line
                for (Booking b : bookings) {
                    writer.write(String.format("%-10d %-20s %-25s %-12s %-10s %-12.2f",
                        b.getBookingID(),
                        b.getCustomerName(),
                        b.getMovieTitle(),
                        b.getShowDate(),
                        b.getShowTime(),
                        b.getTotalPrice()
                    ));
                    writer.newLine();
                }

                writer.newLine();
                writer.write("Total bookings: " + bookings.size());
                writer.newLine();

                // Calculate total revenue
                double totalRevenue = bookings.stream()
                    .mapToDouble(Booking::getTotalPrice)
                    .sum();
                writer.write(String.format("Total revenue:  €%.2f", totalRevenue));
                writer.newLine();
            }

            writer.write("========================================");
            writer.newLine();

        }
        // IOException is a checked exception — caller must handle it
        // This satisfies the "catch-or-declare" requirement from Dr. Barron's slides
    }

    public static String getReportFilePath() {
        return REPORT_FILE;
    }
}
