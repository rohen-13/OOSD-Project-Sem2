import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateAuthor {

    public static void main(String[] args) {

        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost/crud";

        String firstname = "Lisa";
        String lastname = "Brennan";

        Connection connection = null;
        PreparedStatement pstat = null;
        int i = 0;

        try {

            // establish connection to database
            connection = DriverManager.getConnection(
                    DATABASE_URL, "root", "130507SanyaQsd13fg9m"
            );

            // create Prepared Statement for updating data in the table
            pstat = connection.prepareStatement(
                    "Update Authors SET LastName=? Where FirstName=?"
            );

            pstat.setString(1, lastname);
            pstat.setString(2, firstname);

            // Update data in the table
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully updated in the table.");

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

        } finally {

            try {
                pstat.close();
                connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}