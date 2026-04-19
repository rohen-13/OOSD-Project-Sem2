package ui;

import dao.CustomerDAO;
import model.Customer;
import util.ModernUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JFrame {

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private JButton        registerButton;
    private JLabel         statusLabel;

    private CustomerDAO customerDAO;

    public LoginPanel() {
        customerDAO = new CustomerDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Cinema Booking System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ModernUI.APP_BACKGROUND);

        JPanel shell = new JPanel(new GridBagLayout());
        shell.setBorder(new EmptyBorder(24, 24, 24, 24));
        shell.setBackground(ModernUI.APP_BACKGROUND);

        JPanel mainPanel = ModernUI.createSurfacePanel(new BorderLayout(0, 18), 28);
        mainPanel.setPreferredSize(new Dimension(400, 340));

        JLabel titleLabel = new JLabel("Cinema Booking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);

        JLabel subtitleLabel = ModernUI.createMutedLabel("Sign in to manage bookings or reserve seats.");

        emailField    = new JTextField();
        passwordField = new JPasswordField();
        emailField.setToolTipText("Enter your email address");
        passwordField.setToolTipText("Enter your password");
        ModernUI.styleTextField(emailField);
        ModernUI.styleTextField(passwordField);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        loginButton    = new JButton("Login");
        registerButton = new JButton("Register");
        ModernUI.styleButton(loginButton, ModernUI.PRIMARY, Color.WHITE);
        ModernUI.styleButton(registerButton, new Color(226, 232, 240), ModernUI.TEXT_PRIMARY);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(ModernUI.DANGER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setPreferredSize(new Dimension(320, 32));

        formPanel.add(styledLabel("Email"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(styledLabel("Password"));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(statusLabel);
        formPanel.add(Box.createVerticalStrut(10));

        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(loginButton);
        actionsPanel.add(registerButton);
        formPanel.add(actionsPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitleLabel);

        JLabel hintLabel = new JLabel("Admin demo: admin@cinema.com / admin123");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hintLabel.setForeground(ModernUI.TEXT_MUTED);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(hintLabel, BorderLayout.SOUTH);

        shell.add(mainPanel);
        add(shell);

        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(this::handleRegister);
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin(ActionEvent e) {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            setStatus("Please enter email and password.");
            return;
        }
        if (!email.contains("@")) {
            setStatus("Please enter a valid email address.");
            return;
        }

        Customer customer = customerDAO.authenticate(email, password);

        if (customer == null) {
            setStatus("Incorrect email or password.");
            passwordField.setText("");
            return;
        }

        setStatus(" ");

        dispose();

        if (customer.isAdmin()) {
            SwingUtilities.invokeLater(() -> new AdminMainFrame(customer).setVisible(true));
        } else {
            SwingUtilities.invokeLater(() -> new CustomerMainFrame(customer).setVisible(true));
        }
    }

    private void handleRegister(ActionEvent e) {
        JTextField nameField   = new JTextField();
        JTextField regEmail    = new JTextField();
        JTextField phoneField  = new JTextField();
        JPasswordField regPass = new JPasswordField();

        Object[] fields = {
            "Full Name:", nameField,
            "Email:",     regEmail,
            "Phone:",     phoneField,
            "Password:",  regPass
        };

        int result = JOptionPane.showConfirmDialog(
            this, fields, "Register New Account",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name     = nameField.getText().trim();
            String email    = regEmail.getText().trim();
            String phone    = phoneField.getText().trim();
            String password = new String(regPass.getPassword());

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Invalid Email", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters.",
                    "Weak Password", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = customerDAO.register(name, email, password, phone);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Account created! You can now log in.",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                emailField.setText(email);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Registration failed. Email may already be in use.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ModernUI.TEXT_PRIMARY);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void setStatus(String message) {
        statusLabel.setText("<html><body style='width: 300px'>" + message + "</body></html>");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}
