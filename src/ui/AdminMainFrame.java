package ui;

import dao.BookingDAO;
import model.Booking;
import model.Customer;
import util.BookingReportWriter;
import util.ModernUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AdminMainFrame extends JFrame {

    private Customer   loggedInAdmin;
    private BookingDAO bookingDAO = new BookingDAO();

    private JTable            bookingsTable;
    private DefaultTableModel bookingsTableModel;

    public AdminMainFrame(Customer admin) {
        this.loggedInAdmin = admin;
        initUI();
    }

    private void initUI() {
        setTitle("Cinema Admin Panel - " + loggedInAdmin.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(ModernUI.APP_BACKGROUND);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ModernUI.CARD_BACKGROUND);
        topBar.setBorder(ModernUI.createCardBorder(24, 14));

        JLabel roleLabel = new JLabel("Admin Panel  |  " + loggedInAdmin.getName());
        roleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton logoutBtn = new JButton("Logout");
        ModernUI.styleButton(logoutBtn, new Color(254, 226, 226), ModernUI.DANGER);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginPanel().setVisible(true);
        });

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(logoutBtn);

        topBar.add(roleLabel, BorderLayout.WEST);
        topBar.add(actionPanel, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        ModernUI.styleTabs(tabs);

        tabs.addTab("Manage Movies",    new MoviePanel());
        tabs.addTab("Manage Showtimes", new ShowtimePanel());
        tabs.addTab("All Bookings",     buildBookingsPanel());
        tabs.addTab("Reports",          buildReportsPanel());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 2) loadBookings();
        });

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(ModernUI.APP_BACKGROUND);
        root.add(topBar, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);
    }

    // ── BOOKINGS PANEL ────────────────────────────────────────────────────

    private JPanel buildBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(ModernUI.APP_BACKGROUND);

        JLabel title = new JLabel("All Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ModernUI.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Customer", "Movie", "Date", "Time", "Total (EUR)", "Status"};
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

        cancelBtn.addActionListener(e  -> handleCancelBooking());
        refreshBtn.addActionListener(e -> loadBookings());

        btnPanel.add(cancelBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadBookings();
        return panel;
    }

    // ── REPORTS PANEL ─────────────────────────────────────────────────────

    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        panel.setBackground(ModernUI.APP_BACKGROUND);

        JPanel card = ModernUI.createSurfacePanel(new GridBagLayout(), 28);
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Reports", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ModernUI.TEXT_PRIMARY);

        JLabel desc = new JLabel(
            "<html><center>Export the current booking list to a text report with totals and revenue.</center></html>",
            SwingConstants.CENTER);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(ModernUI.TEXT_MUTED);

        JButton exportBtn = new JButton("Export Booking Report to File");
        ModernUI.styleButton(exportBtn, ModernUI.SUCCESS, Color.WHITE);
        exportBtn.setPreferredSize(new Dimension(280, 45));
        exportBtn.addActionListener(e -> handleExportReport());

        cardGbc.gridy = 0;
        card.add(title, cardGbc);
        cardGbc.gridy = 1;
        card.add(desc, cardGbc);
        cardGbc.gridy = 2;
        card.add(exportBtn, cardGbc);

        panel.add(card);
        return panel;
    }

    // ── HANDLERS ──────────────────────────────────────────────────────────

    private void loadBookings() {
        bookingsTableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAllBookingsWithDetails();
        for (Booking b : bookings) {
            bookingsTableModel.addRow(new Object[]{
                b.getBookingID(),
                b.getCustomerName(),
                b.getMovieTitle(),
                b.getShowDate(),
                b.getShowTime(),
                String.format("%.2f", b.getTotalPrice()),
                b.getStatus()
            });
        }
    }

    private void handleCancelBooking() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking to cancel.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingID = (int) bookingsTableModel.getValueAt(row, 0);
        String status = (String) bookingsTableModel.getValueAt(row, 6);

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
                loadBookings();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportReport() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookingsWithDetails();
            BookingReportWriter.writeReport(bookings);
            JOptionPane.showMessageDialog(this,
                "Report exported!\nFile: " + BookingReportWriter.getReportFilePath(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error writing report: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
