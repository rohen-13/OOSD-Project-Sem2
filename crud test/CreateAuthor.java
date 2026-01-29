import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAuthor {

    public static void main(String[] args) {

        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost/crud";

        Connection connection = null;
        PreparedStatement pstat = null;

        String firstname = "Lisa";
        String lastname = "Storozhuk";
        int i = 0;

        try {

            // establish connection to database
            connection = DriverManager.getConnection(
                    DATABASE_URL, "root", "130507SanyaQsd13fg9m"
            );

            // create Prepared Statement for inserting data into table
            pstat = connection.prepareStatement(
                    "INSERT INTO Authors (FirstName, LastName) VALUES (?,?)"
            );

            pstat.setString(1, firstname);
            pstat.setString(2, lastname);

            // insert data into table
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully added to the table.");

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