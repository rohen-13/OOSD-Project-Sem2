package dao;

import model.Customer;
import util.DBConnection;
import util.ErrorLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Customer table.
 * Handles authentication and CRUD operations.
 */
public class CustomerDAO {

    private Connection conn;

    public CustomerDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Authenticates a user by email and password.
     * Returns a Customer object if successful, null if credentials are wrong.
     *
     * @param email the user's email
     * @param password the plain-text password entered at login
     * @return Customer if login succeeds, null otherwise
     */
    public Customer authenticate(String email, String password) {
        String sql = "SELECT CustomerID, Name, Email, Phone, Password, IsAdmin " +
                     "FROM Customer WHERE Email = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("Password");

                // Simple plain-text check (replace with BCrypt in production)
                if (storedPassword.equals(password)) {
                    return new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getBoolean("IsAdmin")
                    );
                }
            }
        } catch (SQLException e) {
            ErrorLogger.log("Authentication error for email: " + email, e);
        }

        return null;
    }

    /**
     * Registers a new customer account.
     *
     * @param name full name
     * @param email email address (must be unique)
     * @param password plain-text password
     * @param phone phone number
     * @return true if registration succeeded
     */
    public boolean register(String name, String email, String password, String phone) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }

        String sql = "INSERT INTO Customer (Name, Email, Password, Phone, IsAdmin) VALUES (?, ?, ?, ?, FALSE)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            ErrorLogger.log("Registration error for email: " + email, e);
            return false;
        }
    }

    public Customer getCustomerByID(int customerID) {
        String sql = "SELECT CustomerID, Name, Email, Phone, IsAdmin FROM Customer WHERE CustomerID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                    rs.getInt("CustomerID"),
                    rs.getString("Name"),
                    rs.getString("Email"),
                    rs.getString("Phone"),
                    rs.getBoolean("IsAdmin")
                );
            }
        } catch (SQLException e) {
            ErrorLogger.log("Error fetching customer by ID " + customerID + ".", e);
        }
        return null;
    }

    public boolean updateCustomer(int customerID, String name, String phone) {
        String sql = "UPDATE Customer SET Name = ?, Phone = ? WHERE CustomerID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setInt(3, customerID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ErrorLogger.log("Error updating customer ID " + customerID + ".", e);
            return false;
        }
    }

    public boolean deleteCustomer(int customerID) {
        String sql = "DELETE FROM Customer WHERE CustomerID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ErrorLogger.log("Error deleting customer ID " + customerID + ".", e);
            return false;
        }
    }
}
