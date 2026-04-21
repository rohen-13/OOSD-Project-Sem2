package ui;

import dao.BookingDAO;
import dao.MovieDAO;
import dao.ShowtimeDAO;
import dao.SeatDAO;
import model.Booking;
import model.Customer;
import model.Movie;
import model.Showtime;
import util.ModernUI;
import util.MoviePosterLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CustomerMainFrame extends JFrame {

    private Customer   loggedInCustomer;
    private MovieDAO   movieDAO   = new MovieDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private SeatDAO    seatDAO    = new SeatDAO();

    // Browse Movies tab
    private JList<Movie>              movieList;
    private DefaultListModel<Movie>   movieListModel;
    private JTextField                searchField;
    private PosterPanel               moviePosterPanel;
    private JTextArea                 movieDescriptionArea;
    private JLabel                    movieTitleLabel;
    private JLabel                    movieMetaLabel;
    private JLabel                    browseHintLabel;
    private JButton                   viewShowtimesButton;
    private Movie                     selectedMovie;

    // My Bookings tab
    private JTable            bookingsTable;
    private DefaultTableModel bookingsTableModel;

    // Booking Panel reference
    private BookingPanel bookingPanel;
    private JTabbedPane tabbedPane;

    public CustomerMainFrame(Customer customer) {
        this.loggedInCustomer = customer;
        initUI();
    }

    private void initUI() {
        setTitle("Cinema Booking - Welcome, " + loggedInCustomer.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(ModernUI.APP_BACKGROUND);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ModernUI.CARD_BACKGROUND);
        topBar.setBorder(ModernUI.createCardBorder(24, 14));

        JLabel welcomeLabel = new JLabel("Welcome back, " + loggedInCustomer.getName());
        welcomeLabel.setForeground(ModernUI.TEXT_PRIMARY);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton logoutBtn = new JButton("Logout");
        ModernUI.styleButton(logoutBtn, new Color(254, 226, 226), ModernUI.DANGER);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginPanel().setVisible(true);
        });

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(logoutBtn);

        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(actionPanel, BorderLayout.EAST);

        tabbedPane = new JTabbedPane();
        ModernUI.styleTabs(tabbedPane);

        tabbedPane.addTab("Browse Movies", buildBrowseMoviesPanel());
        bookingPanel = new BookingPanel(loggedInCustomer);

        // Set up listener for booking completion
        bookingPanel.setBookingListener(new BookingPanel.BookingListener() {
            @Override
            public void onBookingComplete() {
                // Remove the booking tab after successful booking
                if (tabbedPane.getTabCount() == 3) {
                    tabbedPane.removeTabAt(1);
                }
                // Switch back to browse movies
                tabbedPane.setSelectedIndex(0);
            }

            @Override
            public void onBookingCancelled() {
                // Remove the booking tab on cancel
                if (tabbedPane.getTabCount() == 3) {
                    tabbedPane.removeTabAt(1);
                }
                // Switch back to browse movies
                tabbedPane.setSelectedIndex(0);
            }
        });

        // Don't add "Book a Ticket" tab initially - it will be added dynamically when needed
        tabbedPane.addTab("My Bookings",   buildMyBookingsPanel());

        tabbedPane.addChangeListener(e -> {
            int newIndex = tabbedPane.getSelectedIndex();

            // If switching away from booking tab (index 1 when it exists), prevent it
            if (tabbedPane.getTabCount() == 3 && newIndex != 1) {
                // Check if previous tab was booking tab
                try {
                    if (tabbedPane.getComponentAt(1) == bookingPanel) {
                        // User tried to switch away from active booking - prevent it
                        tabbedPane.setSelectedIndex(1);
                        JOptionPane.showMessageDialog(this,
                            "Please complete or cancel your booking first.",
                            "Active Booking", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    // Component doesn't exist, continue
                }
            }

            // Load bookings if switching to My Bookings tab
            if (newIndex == (tabbedPane.getTabCount() == 3 ? 2 : 1)) {
                loadMyBookings();
            }
        });

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(ModernUI.APP_BACKGROUND);
        root.add(topBar, BorderLayout.NORTH);
        root.add(tabbedPane, BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);

        loadMovies();
    }

    // ── BROWSE MOVIES PANEL ───────────────────────────────────────────────

    private JPanel buildBrowseMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(ModernUI.APP_BACKGROUND);

        JPanel topPanel = ModernUI.createSurfacePanel(new BorderLayout(10, 10), 24);

        JLabel title = new JLabel("Now Showing");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ModernUI.TEXT_PRIMARY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn  = new JButton("Clear");
        ModernUI.styleTextField(searchField);
        ModernUI.styleButton(searchBtn, ModernUI.PRIMARY, Color.WHITE);
        ModernUI.styleButton(clearBtn,  new Color(226, 232, 240), ModernUI.TEXT_PRIMARY);

        searchBtn.addActionListener(e -> searchMovies());
        clearBtn.addActionListener(e  -> { searchField.setText(""); loadMovies(); });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        topPanel.add(title,       BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildMovieListPanel(),
            buildMovieDetailsPanel()
        );
        splitPane.setResizeWeight(0.34);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setDividerSize(10);
        splitPane.setContinuousLayout(true);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMovieListPanel() {
        JPanel panel = ModernUI.createSurfacePanel(new BorderLayout(10, 10), 24);
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel listTitle = ModernUI.createTitleLabel("Movie List", 17);
        panel.add(listTitle, BorderLayout.NORTH);

        movieListModel = new DefaultListModel<>();
        movieList = new JList<>(movieListModel);
        movieList.setCellRenderer(new MovieListRenderer());
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieList.setBackground(ModernUI.CARD_ALT_BACKGROUND);
        movieList.setBorder(new EmptyBorder(6, 6, 6, 6));

        movieList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateMovieDetails(movieList.getSelectedValue());
            }
        });

        panel.add(ModernUI.wrapScroll(movieList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMovieDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JPanel heroPanel = ModernUI.createSurfacePanel(new BorderLayout(18, 0), 28);
        moviePosterPanel = new PosterPanel();
        moviePosterPanel.setPreferredSize(new Dimension(240, 320));
        heroPanel.add(moviePosterPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        browseHintLabel = ModernUI.createMutedLabel("Pick a movie from the left to see more.");
        movieTitleLabel = ModernUI.createTitleLabel("Select a movie", 24);
        movieMetaLabel = ModernUI.createMutedLabel("Genre, rating and duration will appear here.");

        infoPanel.add(browseHintLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(movieTitleLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(movieMetaLabel);
        infoPanel.add(Box.createVerticalGlue());

        // Add "View Showtimes" button
        JButton viewShowtimesBtn = new JButton("View Showtimes");
        ModernUI.styleButton(viewShowtimesBtn, ModernUI.PRIMARY, Color.WHITE);
        viewShowtimesBtn.setEnabled(false);
        viewShowtimesBtn.setMaximumSize(new Dimension(160, 40));
        viewShowtimesBtn.addActionListener(e -> showShowtimesDialog());
        infoPanel.add(viewShowtimesBtn);

        // Store button reference for enabling/disabling
        this.viewShowtimesButton = viewShowtimesBtn;

        heroPanel.add(infoPanel, BorderLayout.CENTER);
        panel.add(heroPanel, BorderLayout.NORTH);

        JPanel descriptionPanel = ModernUI.createSurfacePanel(new BorderLayout(0, 10), 28);
        JLabel descriptionTitle = ModernUI.createTitleLabel("About This Movie", 17);
        descriptionPanel.add(descriptionTitle, BorderLayout.NORTH);

        movieDescriptionArea = new JTextArea();
        movieDescriptionArea.setEditable(false);
        movieDescriptionArea.setLineWrap(true);
        movieDescriptionArea.setWrapStyleWord(true);
        movieDescriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        movieDescriptionArea.setForeground(ModernUI.TEXT_PRIMARY);
        movieDescriptionArea.setBackground(ModernUI.CARD_BACKGROUND);
        movieDescriptionArea.setBorder(new EmptyBorder(4, 2, 4, 2));
        movieDescriptionArea.setText("Select a movie to preview its story, duration and rating.");

        descriptionPanel.add(ModernUI.wrapScroll(movieDescriptionArea), BorderLayout.CENTER);
        panel.add(descriptionPanel, BorderLayout.CENTER);

        return panel;
    }

    // ── MY BOOKINGS PANEL ─────────────────────────────────────────────────

    private JPanel buildMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(ModernUI.APP_BACKGROUND);

        JLabel title = new JLabel("My Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ModernUI.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Movie", "Date", "Time", "Total (EUR)", "Status"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUI.styleTable(bookingsTable);
        panel.add(ModernUI.wrapScroll(bookingsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);

        JButton cancelBtn  = new JButton("Cancel Booking");
        JButton refreshBtn = new JButton("Refresh");
        ModernUI.styleButton(cancelBtn,  new Color(254, 226, 226), ModernUI.DANGER);
        ModernUI.styleButton(refreshBtn, new Color(219, 234, 254), ModernUI.PRIMARY_DARK);

        cancelBtn.addActionListener(e  -> handleCancelMyBooking());
        refreshBtn.addActionListener(e -> loadMyBookings());

        btnPanel.add(cancelBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────

    private void loadMovies() {
        refreshMovieList(movieDAO.getAllMovies());
    }

    private void searchMovies() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) { loadMovies(); return; }

        DefaultListModel<Movie> filteredModel = new DefaultListModel<>();
        for (Movie m : movieDAO.getAllMovies()) {
            if (m.getTitle().toLowerCase().contains(keyword) ||
                m.getGenre().toLowerCase().contains(keyword) ||
                m.getRating().toLowerCase().contains(keyword)) {
                filteredModel.addElement(m);
            }
        }
        movieList.setModel(filteredModel);
        if (filteredModel.getSize() > 0) {
            movieList.setSelectedIndex(0);
        } else {
            updateMovieDetails(null);
            JOptionPane.showMessageDialog(this,
                "No movies found for: " + keyword,
                "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshMovieList(List<Movie> movies) {
        movieListModel.clear();
        for (Movie movie : movies) {
            movieListModel.addElement(movie);
        }

        movieList.setModel(movieListModel);
        if (movieListModel.getSize() > 0) {
            movieList.setSelectedIndex(0);
        } else {
            updateMovieDetails(null);
        }
    }

    private void updateMovieDetails(Movie movie) {
        if (movie == null) {
            browseHintLabel.setText("Pick a movie from the left to see more.");
            movieTitleLabel.setText("Select a movie");
            movieMetaLabel.setText("Genre, rating and duration will appear here.");
            movieDescriptionArea.setText("Select a movie to preview its story, duration and rating.");
            moviePosterPanel.setMovie(null);
            viewShowtimesButton.setEnabled(false);
            selectedMovie = null;
            return;
        }

        browseHintLabel.setText("Now showing");
        movieTitleLabel.setText(movie.getTitle());
        movieMetaLabel.setText(movie.getGenre() + "  |  " + movie.getRating() + "  |  " + movie.getDuration() + " mins");
        movieDescriptionArea.setText(movie.getDescription() == null || movie.getDescription().isBlank()
            ? "No description is available for this movie yet."
            : movie.getDescription());
        movieDescriptionArea.setCaretPosition(0);
        moviePosterPanel.setMovie(movie);
        viewShowtimesButton.setEnabled(true);
        selectedMovie = movie;
    }

    private static final class MovieListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Movie movie) {
                label.setText("<html><div style='padding:6px 2px;'><b>" + movie.getTitle() + "</b><br/>"
                    + movie.getGenre() + "  |  " + movie.getRating() + "  |  " + movie.getDuration() + " mins</div></html>");
            }
            label.setBorder(new EmptyBorder(8, 12, 8, 12));
            label.setBackground(isSelected ? new Color(219, 234, 254) : ModernUI.CARD_ALT_BACKGROUND);
            label.setForeground(ModernUI.TEXT_PRIMARY);
            return label;
        }
    }

    private static final class PosterPanel extends JPanel {
        private Movie         movie;
        private BufferedImage posterImage;

        private PosterPanel() {
            setOpaque(false);
        }

        private void setMovie(Movie movie) {
            this.movie = movie;
            this.posterImage = MoviePosterLoader.loadPoster(movie);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            Shape clip = new RoundRectangle2D.Float(0, 0, width, height, 30, 30);

            g2.setColor(new Color(15, 23, 42));
            g2.fill(clip);

            if (posterImage != null) {
                g2.setClip(clip);
                drawScaledImage(g2, posterImage, width, height);
            } else {
                paintPlaceholder(g2, width, height);
            }

            g2.dispose();
        }

        private void drawScaledImage(Graphics2D g2, BufferedImage image, int width, int height) {
            double scale = Math.max((double) width / image.getWidth(), (double) height / image.getHeight());
            int drawWidth = (int) Math.round(image.getWidth() * scale);
            int drawHeight = (int) Math.round(image.getHeight() * scale);
            int x = (width - drawWidth) / 2;
            int y = (height - drawHeight) / 2;

            g2.drawImage(image, x, y, drawWidth, drawHeight, null);
        }

        private void paintPlaceholder(Graphics2D g2, int width, int height) {
            g2.setColor(new Color(30, 41, 59));
            g2.fillRoundRect(8, 8, width - 16, height - 16, 24, 24);

            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            String text = movie == null ? "No poster selected" : "Add poster PNG";
            FontMetrics metrics = g2.getFontMetrics();
            int x = (width - metrics.stringWidth(text)) / 2;
            int y = height / 2 - 6;
            g2.drawString(text, x, y);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            String hint = movie == null ? "assets/movies" : movie.getMovieID() + ".png";
            FontMetrics hintMetrics = g2.getFontMetrics();
            int hintX = (width - hintMetrics.stringWidth(hint)) / 2;
            g2.drawString(hint, hintX, y + 26);
        }
    }

    private void loadMyBookings() {
        bookingsTableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getBookingsByCustomer(loggedInCustomer.getCustomerID());
        for (Booking b : bookings) {
            bookingsTableModel.addRow(new Object[]{
                b.getBookingID(),
                b.getMovieTitle(),
                b.getShowDate(),
                b.getShowTime(),
                String.format("%.2f", b.getTotalPrice()),
                b.getStatus()
            });
        }
    }

    private void handleCancelMyBooking() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking to cancel.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    bookingID = (int)    bookingsTableModel.getValueAt(row, 0);
        String status    = (String) bookingsTableModel.getValueAt(row, 5);

        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "This booking is already cancelled.",
                "Already Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel Booking #" + bookingID + "?",
            "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookingDAO.cancelBooking(bookingID);
                JOptionPane.showMessageDialog(this,
                    "Booking #" + bookingID + " cancelled.",
                    "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadMyBookings();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showShowtimesDialog() {
        if (selectedMovie == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a movie first.",
                "No Movie Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Showtime> allShowtimes = showtimeDAO.getShowtimesByMovie(selectedMovie.getMovieID());

        List<Showtime> showtimes = new ArrayList<>();
        for (Showtime s : allShowtimes) {
            if (seatDAO.getAvailableSeatsCount(s.getShowtimeID()) > 0) {
                showtimes.add(s);
            }
        }

        if (showtimes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No showtimes available for this movie with available seats.",
                "No Showtimes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JDialog dialog = new JDialog(this, "Showtimes for " + selectedMovie.getTitle(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new BorderLayout(12, 12));
        dialogPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        dialogPanel.setBackground(ModernUI.APP_BACKGROUND);

        JLabel titleLabel = new JLabel("Available Showtimes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        dialogPanel.add(titleLabel, BorderLayout.NORTH);

        // Create showtime list
        DefaultListModel<Showtime> listModel = new DefaultListModel<>();
        for (Showtime s : showtimes) {
            listModel.addElement(s);
        }

        JList<Showtime> showtimeListUI = new JList<>(listModel);
        showtimeListUI.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showtimeListUI.setFixedCellHeight(60);
        showtimeListUI.setBackground(ModernUI.CARD_ALT_BACKGROUND);
        showtimeListUI.setForeground(ModernUI.TEXT_PRIMARY);
        showtimeListUI.setSelectionBackground(new Color(219, 234, 254));
        showtimeListUI.setSelectionForeground(ModernUI.TEXT_PRIMARY);
        showtimeListUI.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JScrollPane scrollPane = ModernUI.wrapScroll(showtimeListUI);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton bookButton = new JButton("Book This Showtime");
        JButton cancelButton = new JButton("Cancel");

        ModernUI.styleButton(bookButton, ModernUI.SUCCESS, Color.WHITE);
        ModernUI.styleButton(cancelButton, new Color(226, 232, 240), ModernUI.TEXT_PRIMARY);

        bookButton.addActionListener(e -> {
            Showtime selected = showtimeListUI.getSelectedValue();
            if (selected != null) {
                dialog.dispose();
                proceedToBooking(selected);
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a showtime.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

    private void proceedToBooking(Showtime showtime) {
        // Add "Book a Ticket" tab if not already present (at index 1)
        if (tabbedPane.getTabCount() == 2) {
            tabbedPane.insertTab("Book a Ticket", null, bookingPanel, null, 1);
        }

        // Set the selected showtime in BookingPanel
        // This must be done AFTER the tab is inserted
        bookingPanel.setSelectedShowtime(showtime);

        // Switch to the "Book a Ticket" tab (now at index 1)
        tabbedPane.setSelectedIndex(1);
    }

}
