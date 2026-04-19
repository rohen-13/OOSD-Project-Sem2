package ui;

import dao.MovieDAO;
import exception.InvalidInputException;
import model.Movie;
import util.ModernUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MoviePanel extends JPanel {

    private MovieDAO movieDAO = new MovieDAO();

    private JTable            movieTable;
    private DefaultTableModel tableModel;

    private JTextField titleField;
    private JTextField durationField;
    private JTextField genreField;
    private JTextArea  descriptionArea;
    private JComboBox<String> ratingCombo;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private int selectedMovieID = -1;

    public MoviePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ModernUI.APP_BACKGROUND);

        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFormPanel(),  BorderLayout.EAST);

        loadMovies();
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ModernUI.APP_BACKGROUND);

        JLabel title = new JLabel("Movie List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ModernUI.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Duration (mins)", "Rating", "Genre"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        movieTable = new JTable(tableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUI.styleTable(movieTable);

        movieTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        panel.add(ModernUI.wrapScroll(movieTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = ModernUI.createSurfacePanel(new GridLayout(12, 1, 8, 8), 24);
        panel.setPreferredSize(new Dimension(280, 0));

        panel.add(new JLabel("Title:"));
        titleField = new JTextField();
        ModernUI.styleTextField(titleField);
        panel.add(titleField);

        panel.add(new JLabel("Duration (minutes):"));
        durationField = new JTextField();
        ModernUI.styleTextField(durationField);
        panel.add(durationField);

        panel.add(new JLabel("Rating:"));
        ratingCombo = new JComboBox<>(new String[]{"G", "PG", "12A", "15A", "16", "18"});
        ratingCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(ratingCombo);

        panel.add(new JLabel("Genre:"));
        genreField = new JTextField();
        ModernUI.styleTextField(genreField);
        panel.add(genreField);

        panel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBorder(ModernUI.createInputBorder());
        panel.add(ModernUI.wrapScroll(descriptionArea));

        addButton    = new JButton("Add Movie");
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

    private void handleAdd() {
        try {
            String title       = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String genre       = genreField.getText().trim();
            String rating      = (String) ratingCombo.getSelectedItem();
            int    duration    = Integer.parseInt(durationField.getText().trim());

            boolean success = movieDAO.addMovie(title, description, duration, rating, genre);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Movie '" + title + "' added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMovies();
                clearForm();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Duration must be a whole number (e.g. 120).\nYou entered: '" + durationField.getText() + "'",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedMovieID == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a movie from the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String title       = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String genre       = genreField.getText().trim();
            String rating      = (String) ratingCombo.getSelectedItem();
            int    duration    = Integer.parseInt(durationField.getText().trim());

            boolean success = movieDAO.updateMovie(selectedMovieID, title, description, duration, rating, genre);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Movie updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMovies();
                clearForm();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Duration must be a whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedMovieID == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a movie to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this movie?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                movieDAO.deleteMovie(selectedMovieID);
                JOptionPane.showMessageDialog(this,
                    "Movie deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadMovies();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadMovies() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMovies();
        for (Movie m : movies) {
            tableModel.addRow(new Object[]{
                m.getMovieID(), m.getTitle(), m.getDuration(), m.getRating(), m.getGenre()
            });
        }
    }

    private void populateFormFromSelection() {
        int row = movieTable.getSelectedRow();
        if (row < 0) return;
        selectedMovieID = (int) tableModel.getValueAt(row, 0);
        Movie movie = movieDAO.getMovieByID(selectedMovieID);
        if (movie != null) {
            titleField.setText(movie.getTitle());
            durationField.setText(String.valueOf(movie.getDuration()));
            genreField.setText(movie.getGenre());
            descriptionArea.setText(movie.getDescription());
            ratingCombo.setSelectedItem(movie.getRating());
        }
    }

    private void clearForm() {
        selectedMovieID = -1;
        titleField.setText("");
        durationField.setText("");
        genreField.setText("");
        descriptionArea.setText("");
        ratingCombo.setSelectedIndex(0);
        movieTable.clearSelection();
    }

}
