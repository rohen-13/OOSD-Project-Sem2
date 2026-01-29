import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAuthor {

    public static void main(String[] args) {

        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost/crud";

        Connection connection = null;
        PreparedStatement pstat = null;

        int i = 0;
        int authorID = 2;

        try {

            // establish connection to database
            connection = DriverManager.getConnection(
                    DATABASE_URL, "root", "130507SanyaQsd13fg9m"
            );

            // create Prepared Statement for deleting data from the table
            pstat = connection.prepareStatement(
                    "Delete From Authors Where AuthorID=?"
            );

            pstat.setInt(1, authorID);

            // delete data from the table
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully removed from the table.");

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