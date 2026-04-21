package ui;

import dao.MovieDAO;
import dao.SeatDAO;
import dao.ShowtimeDAO;
import exception.InvalidInputException;
import model.Movie;
import model.Showtime;
import util.ModernUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Admin panel for managing showtimes — full CRUD.
 *
 * Swing components used:
 *  - JTable      : displays showtimes
 *  - JComboBox   : select movie and hall
 *  - JTextField  : date, time, price input
 *  - JButton     : Add, Update, Delete, Clear
 *  - JLabel      : field labels
 *  - JScrollPane : wraps table
 */
public class ShowtimePanel extends JPanel {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd-MM-uuuu");
    private static final DateTimeFormatter STORAGE_DATE_FORMAT =
        DateTimeFormatter.ISO_LOCAL_DATE;

    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private MovieDAO    movieDAO    = new MovieDAO();

    // Table
    private JTable            showtimeTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JComboBox<Movie>   movieCombo;
    private JComboBox<String>  hallCombo;
    private JTextField         dateField;
    private JTextField         timeField;
    private JTextField         priceField;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    // State
    private int selectedShowtimeID = -1;

    // Hall IDs matching the combo options
    private static final int[] HALL_IDS = {1, 2, 3};

    public ShowtimePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ModernUI.APP_BACKGROUND);

        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFormPanel(),  BorderLayout.EAST);

        loadShowtimes();
    }

    // ── TABLE PANEL ───────────────────────────────────────────────────────

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ModernUI.APP_BACKGROUND);

        JLabel title = new JLabel("Showtime Schedule");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ModernUI.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Movie", "Hall", "Date", "Time", "Price (EUR)", "Seats Left"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        showtimeTable = new JTable(tableModel);
        showtimeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUI.styleTable(showtimeTable);

        showtimeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        panel.add(ModernUI.wrapScroll(showtimeTable), BorderLayout.CENTER);
        return panel;
    }

    // ── FORM PANEL ────────────────────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel panel = ModernUI.createSurfacePanel(new GridLayout(13, 1, 8, 8), 24);
        panel.setPreferredSize(new Dimension(280, 0));

        // Movie dropdown
        panel.add(new JLabel("Movie:"));
        movieCombo = new JComboBox<>();
        loadMoviesIntoCombo();
        movieCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(movieCombo);

        // Hall dropdown
        panel.add(new JLabel("Hall:"));
        hallCombo = new JComboBox<>(new String[]{"Hall 1", "Hall 2", "Hall 3"});
        hallCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(hallCombo);

        // Date
        panel.add(new JLabel("Date (DD-MM-YYYY):"));
        dateField = new JTextField();
        dateField.setToolTipText("Example: 20-05-2026");
        ModernUI.styleTextField(dateField);
        panel.add(dateField);

        // Time
        panel.add(new JLabel("Time (HH:MM):"));
        timeField = new JTextField();
        timeField.setToolTipText("Example: 19:00");
        ModernUI.styleTextField(timeField);
        panel.add(timeField);

        // Price
        panel.add(new JLabel("Ticket Price (EUR):"));
        priceField = new JTextField();
        priceField.setToolTipText("Example: 12.50");
        ModernUI.styleTextField(priceField);
        panel.add(priceField);

        // Buttons
        addButton    = new JButton("Add Showtime");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton  = new JButton("Clear");

        ModernUI.styleButton(addButton,    ModernUI.SUCCESS, Color.WHITE);
        ModernUI.styleButton(updateButton, ModernUI.PRIMARY, Color.WHITE);
        ModernUI.styleButton(deleteButton, new Color(254, 226, 226), ModernUI.DANGER);
        ModernUI.styleButton(clearButton,  new Color(226, 232, 240), ModernUI.TEXT_PRIMARY);

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);

        addButton.addActionListener(e    -> handleAdd());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e  -> clearForm());

        return panel;
    }

    // ── CRUD HANDLERS ─────────────────────────────────────────────────────

    private void handleAdd() {
        try {
            Movie  movie  = (Movie) movieCombo.getSelectedItem();
            int    hallID = HALL_IDS[hallCombo.getSelectedIndex()];
            String date   = toStorageDate(dateField.getText().trim());
            String time   = timeField.getText().trim();

            // NumberFormatException caught if user types letters in price
            double price = Double.parseDouble(priceField.getText().trim());

            if (movie == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a movie.", "No Movie", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate time format
            if (!time.matches("\\d{2}:\\d{2}")) {
                throw new InvalidInputException(
                    "Time must be in format HH:MM (e.g. 19:00). You entered: " + time);
            }

            showtimeDAO.addShowtime(movie.getMovieID(), hallID, date, time, price);

            JOptionPane.showMessageDialog(this,
                "Showtime added! Seats generated automatically.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadShowtimes();
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Price must be a number (e.g. 12.50). You entered: '" + priceField.getText() + "'",
                "Invalid Price", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedShowtimeID == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a showtime to update.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Movie  movie  = (Movie) movieCombo.getSelectedItem();
            int    hallID = HALL_IDS[hallCombo.getSelectedIndex()];
            String date   = toStorageDate(dateField.getText().trim());
            String time   = timeField.getText().trim();
            double price  = Double.parseDouble(priceField.getText().trim());

            if (!time.matches("\\d{2}:\\d{2}")) {
                throw new InvalidInputException("Time must be in format HH:MM.");
            }

            showtimeDAO.updateShowtime(selectedShowtimeID,
                movie.getMovieID(), hallID, date, time, price);

            JOptionPane.showMessageDialog(this,
                "Showtime updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadShowtimes();
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Price must be a number (e.g. 12.50).",
                "Invalid Price", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedShowtimeID == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a showtime to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this showtime? All seats will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                showtimeDAO.deleteShowtime(selectedShowtimeID);
                JOptionPane.showMessageDialog(this,
                    "Showtime deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadShowtimes();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────

    private void loadShowtimes() {
        tableModel.setRowCount(0);
        List<Showtime> showtimes = showtimeDAO.getAllShowtimes();
        SeatDAO seatDAO = new SeatDAO();

        for (Showtime s : showtimes) {
            int seatsLeft = seatDAO.getAvailableSeatsCount(s.getShowtimeID());
            tableModel.addRow(new Object[]{
                s.getShowtimeID(),
                s.getMovieTitle(),
                s.getHallName(),
                toDisplayDate(s.getShowDate()),
                s.getShowTime(),
                String.format("%.2f", s.getTicketPrice()),
                seatsLeft
            });
        }
    }

    private void loadMoviesIntoCombo() {
        movieCombo.removeAllItems();
        List<Movie> movies = movieDAO.getAllMovies();
        for (Movie m : movies) {
            movieCombo.addItem(m);
        }
    }

    private void populateFormFromSelection() {
        int row = showtimeTable.getSelectedRow();
        if (row < 0) return;

        selectedShowtimeID = (int) tableModel.getValueAt(row, 0);
        String movieTitle  = (String) tableModel.getValueAt(row, 1);
        String hallName    = (String) tableModel.getValueAt(row, 2);

        dateField.setText((String) tableModel.getValueAt(row, 3));
        timeField.setText((String) tableModel.getValueAt(row, 4));
        priceField.setText(tableModel.getValueAt(row, 5).toString());

        // Select matching movie in combo
        for (int i = 0; i < movieCombo.getItemCount(); i++) {
            if (movieCombo.getItemAt(i).getTitle().equals(movieTitle)) {
                movieCombo.setSelectedIndex(i);
                break;
            }
        }

        // Select matching hall in combo
        for (int i = 0; i < hallCombo.getItemCount(); i++) {
            if (hallCombo.getItemAt(i).equals(hallName)) {
                hallCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        selectedShowtimeID = -1;
        dateField.setText("");
        timeField.setText("");
        priceField.setText("");
        movieCombo.setSelectedIndex(0);
        hallCombo.setSelectedIndex(0);
        showtimeTable.clearSelection();
    }

    private String toStorageDate(String displayDate) throws InvalidInputException {
        try {
            LocalDate parsed = LocalDate.parse(displayDate, DISPLAY_DATE_FORMAT);
            return parsed.format(STORAGE_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(
                "Date must be in format DD-MM-YYYY (e.g. 20-05-2026). You entered: " + displayDate);
        }
    }

    private String toDisplayDate(String storageDate) {
        try {
            return LocalDate.parse(storageDate, STORAGE_DATE_FORMAT).format(DISPLAY_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return storageDate;
        }
    }

}
