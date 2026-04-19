package ui;

import dao.BookingDAO;
import dao.SeatDAO;
import dao.ShowtimeDAO;
import exception.InvalidBookingException;
import model.Customer;
import model.Seat;
import model.Showtime;
import util.ModernUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer booking flow panel.
 * Step 1: Select a showtime from the list
 * Step 2: Interactive seat map — click seats to select them
 * Step 3: Confirm booking
 *
 * Swing components used:
 *  - JList       : list of available showtimes
 *  - JPanel grid : interactive seat map (each seat is a JButton)
 *  - JLabel      : showtime info, legend
 *  - JButton     : seat buttons + confirm button
 *  - JScrollPane : wraps showtime list and seat map
 *  - JTextArea   : booking summary
 */
public class BookingPanel extends JPanel {

    // Callback interface for booking completion
    public interface BookingListener {
        void onBookingComplete();
        void onBookingCancelled();
    }

    private Customer    loggedInCustomer;
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private SeatDAO     seatDAO     = new SeatDAO();
    private BookingDAO  bookingDAO  = new BookingDAO();

    private BookingListener bookingListener;

    // Step 1 — Showtime selection
    private JList<Showtime>        showtimeList;
    private DefaultListModel<Showtime> showtimeListModel;

    // Step 2 — Seat map
    private JPanel    seatMapPanel;
    private JLabel    showtimeInfoLabel;
    private JLabel    selectedSeatsLabel;

    // Step 3 — Confirm
    private JButton   confirmButton;
    private JLabel    totalLabel;

    // State
    private Showtime        selectedShowtime = null;
    private List<Integer>   selectedSeatIDs  = new ArrayList<>();

    // Colours for seat states
    private static final Color COLOR_AVAILABLE = new Color(100, 180, 100);  // green
    private static final Color COLOR_BOOKED    = new Color(180, 80,  80);   // red
    private static final Color COLOR_SELECTED  = new Color(70,  130, 200);  // blue

    public BookingPanel(Customer customer) {
        this.loggedInCustomer = customer;
        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(ModernUI.APP_BACKGROUND);

        add(buildLeftPanel(),   BorderLayout.WEST);
        add(buildCentrePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadShowtimes();
    }

    public void setBookingListener(BookingListener listener) {
        this.bookingListener = listener;
    }

    // ── LEFT PANEL: Showtime list ─────────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel panel = ModernUI.createSurfacePanel(new BorderLayout(10, 10), 24);
        panel.setPreferredSize(new Dimension(300, 0));

        JLabel title = ModernUI.createTitleLabel("Available Showtimes", 18);
        panel.add(title, BorderLayout.NORTH);

        showtimeListModel = new DefaultListModel<>();
        showtimeList = new JList<>(showtimeListModel);
        showtimeList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showtimeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showtimeList.setFixedCellHeight(48);
        showtimeList.setBackground(ModernUI.CARD_ALT_BACKGROUND);
        showtimeList.setForeground(ModernUI.TEXT_PRIMARY);
        showtimeList.setSelectionBackground(new Color(219, 234, 254));
        showtimeList.setSelectionForeground(ModernUI.TEXT_PRIMARY);
        showtimeList.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // When showtime is selected, load the seat map
        showtimeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedShowtime = showtimeList.getSelectedValue();
                if (selectedShowtime != null) {
                    loadSeatMap(selectedShowtime.getShowtimeID());
                    showtimeInfoLabel.setText(
                        selectedShowtime.getMovieTitle() +
                        " | " + selectedShowtime.getShowDate() +
                        " " + selectedShowtime.getShowTime() +
                        " | EUR " + String.format("%.2f", selectedShowtime.getTicketPrice()) + " per seat"
                    );
                }
            }
        });

        panel.add(ModernUI.wrapScroll(showtimeList), BorderLayout.CENTER);
        return panel;
    }

    // ── CENTRE PANEL: Seat map ────────────────────────────────────────────

    private JPanel buildCentrePanel() {
        JPanel panel = ModernUI.createSurfacePanel(new BorderLayout(10, 12), 24);

        showtimeInfoLabel = new JLabel("Select a showtime on the left to see seats.");
        showtimeInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        showtimeInfoLabel.setForeground(ModernUI.TEXT_PRIMARY);
        showtimeInfoLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        panel.add(showtimeInfoLabel, BorderLayout.NORTH);

        seatMapPanel = new JPanel();
        seatMapPanel.setBackground(ModernUI.CARD_ALT_BACKGROUND);
        seatMapPanel.setLayout(new BorderLayout());
        seatMapPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel placeholder = new JLabel("Seat map will appear here.", SwingConstants.CENTER);
        placeholder.setForeground(ModernUI.TEXT_MUTED);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        seatMapPanel.add(placeholder, BorderLayout.CENTER);

        JScrollPane seatScroll = ModernUI.wrapScroll(seatMapPanel);
        seatScroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(seatScroll, BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        legend.setOpaque(false);
        legend.add(legendItem("Available", COLOR_AVAILABLE));
        legend.add(legendItem("Selected",  COLOR_SELECTED));
        legend.add(legendItem("Booked",    COLOR_BOOKED));
        panel.add(legend, BorderLayout.SOUTH);

        return panel;
    }

    // ── BOTTOM PANEL: Summary + Confirm ───────────────────────────────────

    private JPanel buildBottomPanel() {
        JPanel panel = ModernUI.createSurfacePanel(new FlowLayout(FlowLayout.RIGHT, 15, 10), 24);

        selectedSeatsLabel = new JLabel("No seats selected.");
        selectedSeatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        selectedSeatsLabel.setForeground(ModernUI.TEXT_PRIMARY);

        totalLabel = new JLabel("Total: EUR 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLabel.setForeground(ModernUI.TEXT_PRIMARY);

        confirmButton = new JButton("Confirm Booking");
        ModernUI.styleButton(confirmButton, ModernUI.SUCCESS, Color.WHITE);
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> handleConfirmBooking());

        JButton cancelButton = new JButton("Cancel Booking");
        ModernUI.styleButton(cancelButton, new Color(254, 226, 226), ModernUI.DANGER);
        cancelButton.addActionListener(e -> handleCancelBooking());

        panel.add(selectedSeatsLabel);
        panel.add(totalLabel);
        panel.add(cancelButton);
        panel.add(confirmButton);

        return panel;
    }

    // ── LOAD DATA ─────────────────────────────────────────────────────────

    private void loadShowtimes() {
        showtimeListModel.clear();
        List<Showtime> showtimes = showtimeDAO.getAllShowtimes();
        for (Showtime s : showtimes) {
            // Only show showtimes that have available seats
            if (seatDAO.getAvailableSeatsCount(s.getShowtimeID()) > 0) {
                showtimeListModel.addElement(s);
            }
        }
        if (showtimeListModel.isEmpty()) {
            showtimeInfoLabel.setText("No showtimes available at the moment.");
        }
    }

    /**
     * Programmatically set a specific showtime as selected.
     * Useful when user selects a movie and then clicks "View Showtimes" to book.
     */
    public void setSelectedShowtime(Showtime showtime) {
        if (showtime != null) {
            for (int i = 0; i < showtimeListModel.getSize(); i++) {
                if (showtimeListModel.getElementAt(i).getShowtimeID() == showtime.getShowtimeID()) {
                    showtimeList.setSelectedIndex(i);
                    showtimeList.ensureIndexIsVisible(i);
                    break;
                }
            }
        }
    }

    /**
     * Builds the interactive seat map grid.
     * Each seat is a JButton — green = available, red = booked, blue = selected.
     */
    private void loadSeatMap(int showtimeID) {
        seatMapPanel.removeAll();
        selectedSeatIDs.clear();
        updateSummary();

        List<Seat> seats = seatDAO.getSeatsByShowtime(showtimeID);

        if (seats.isEmpty()) {
            JLabel noSeats = new JLabel("No seats found for this showtime.", SwingConstants.CENTER);
            noSeats.setForeground(ModernUI.TEXT_MUTED);
            seatMapPanel.add(noSeats, BorderLayout.CENTER);
            seatMapPanel.revalidate();
            seatMapPanel.repaint();
            return;
        }

        // Group seats by row
        String currentRow = "";
        JPanel rowPanel   = null;
        JPanel allRows    = new JPanel();
        allRows.setLayout(new BoxLayout(allRows, BoxLayout.Y_AXIS));
        allRows.setBackground(ModernUI.CARD_ALT_BACKGROUND);

        JPanel screenWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        screenWrap.setOpaque(false);

        JLabel screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        screenLabel.setForeground(ModernUI.TEXT_PRIMARY);
        screenLabel.setOpaque(true);
        screenLabel.setBackground(new Color(235, 241, 255));
        screenLabel.setPreferredSize(new Dimension(260, 42));
        screenLabel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(191, 219, 254), 1, true),
            new EmptyBorder(10, 16, 10, 16)
        ));
        screenWrap.add(screenLabel);
        allRows.add(screenWrap);
        allRows.add(Box.createVerticalStrut(15));

        for (Seat seat : seats) {
            // New row — create a new row panel
            if (!seat.getRowLabel().equals(currentRow)) {
                currentRow = seat.getRowLabel();
                rowPanel   = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
                rowPanel.setBackground(ModernUI.CARD_ALT_BACKGROUND);

                // Row label on left
                JLabel rowLabel = new JLabel(currentRow + "  ");
                rowLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                rowLabel.setForeground(ModernUI.TEXT_PRIMARY);
                rowLabel.setPreferredSize(new Dimension(26, 24));
                rowPanel.add(rowLabel);

                allRows.add(rowPanel);
            }

            // Create seat button
            JButton seatBtn = new JButton(seat.getRowLabel() + seat.getSeatNumber());
            seatBtn.setPreferredSize(new Dimension(60, 38));
            seatBtn.setMinimumSize(new Dimension(60, 38));
            seatBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            seatBtn.setFocusPainted(false);
            seatBtn.setBorderPainted(false);
            seatBtn.setOpaque(true);
            seatBtn.setContentAreaFilled(true);
            seatBtn.setMargin(new Insets(0, 0, 0, 0));
            seatBtn.setBorder(new EmptyBorder(0, 0, 0, 0));

            // Treat reserved seats like booked seats so customers only see what is actually selectable.
            if ("AVAILABLE".equals(seat.getStatus())) {
                seatBtn.setBackground(COLOR_AVAILABLE);
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setEnabled(true);
                final int seatID = seat.getSeatID();
                seatBtn.addActionListener(e -> toggleSeatSelection(seatBtn, seatID));
            } else {
                seatBtn.setBackground(COLOR_BOOKED);
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setEnabled(false);
            }

            if (rowPanel != null) rowPanel.add(seatBtn);
        }

        seatMapPanel.add(allRows, BorderLayout.NORTH);
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    // ── SEAT SELECTION ────────────────────────────────────────────────────

    private void toggleSeatSelection(JButton btn, int seatID) {
        if (selectedSeatIDs.contains(seatID)) {
            // Deselect
            selectedSeatIDs.remove(Integer.valueOf(seatID));
            btn.setBackground(COLOR_AVAILABLE);
        } else {
            // Select
            selectedSeatIDs.add(seatID);
            btn.setBackground(COLOR_SELECTED);
        }
        updateSummary();
    }

    private void updateSummary() {
        int count = selectedSeatIDs.size();
        if (count == 0) {
            selectedSeatsLabel.setText("No seats selected.");
            totalLabel.setText("Total: EUR 0.00");
            confirmButton.setEnabled(false);
        } else {
            double total = selectedShowtime != null
                ? count * selectedShowtime.getTicketPrice() : 0;
            selectedSeatsLabel.setText(count + " seat(s) selected.");
            totalLabel.setText("Total: EUR " + String.format("%.2f", total));
            confirmButton.setEnabled(true);
        }
    }

    // ── CONFIRM BOOKING ───────────────────────────────────────────────────

    private void handleCancelBooking() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?",
            "Cancel Booking", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Reset selections
            selectedSeatIDs.clear();
            updateSummary();

            // Notify listener
            if (bookingListener != null) {
                bookingListener.onBookingCancelled();
            }
        }
    }

    private void handleConfirmBooking() {
        if (selectedShowtime == null || selectedSeatIDs.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a showtime and at least one seat.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = selectedSeatIDs.size() * selectedShowtime.getTicketPrice();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm booking?\n\n" +
            "Movie:  " + selectedShowtime.getMovieTitle() + "\n" +
            "Date:   " + selectedShowtime.getShowDate() + "\n" +
            "Time:   " + selectedShowtime.getShowTime() + "\n" +
            "Seats:  " + selectedSeatIDs.size() + "\n" +
            "Total:  EUR " + String.format("%.2f", total),
            "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int bookingID = bookingDAO.createBooking(
                    loggedInCustomer.getCustomerID(),
                    selectedShowtime.getShowtimeID(),
                    selectedSeatIDs,
                    total
                );

                JOptionPane.showMessageDialog(this,
                    "Booking confirmed!\nBooking ID: #" + bookingID +
                    "\nPlease show this ID at the cinema entrance.",
                    "Booking Successful", JOptionPane.INFORMATION_MESSAGE);

                // Refresh the seat map and showtime list
                loadSeatMap(selectedShowtime.getShowtimeID());
                loadShowtimes();

                // Notify listener that booking is complete
                if (bookingListener != null) {
                    bookingListener.onBookingComplete();
                }

            } catch (InvalidBookingException e) {
                JOptionPane.showMessageDialog(this,
                    "Booking failed: " + e.getMessage(),
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── LEGEND HELPER ─────────────────────────────────────────────────────

    private JPanel legendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(16, 16));
        item.add(colorBox);
        JLabel textLabel = new JLabel(label);
        textLabel.setForeground(ModernUI.TEXT_PRIMARY);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.add(textLabel);
        return item;
    }
}
