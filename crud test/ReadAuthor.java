import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReadAuthor {

    public static void main(String args[]) {

        // database URL
        final String DATABASE_URL = "jdbc:mysql://localhost/crud";

        Connection connection = null;
        PreparedStatement pstat = null;
        ResultSet resultSet = null;

        try {

            // establish connection to database
            connection = DriverManager.getConnection(
                    DATABASE_URL, "root", "130507SanyaQsd13fg9m"
            );

            // create Prepared Statement for querying data in the table
            pstat = connection.prepareStatement(
                    "SELECT AuthorID, FirstName, LastName FROM Authors"
            );

            // query data in the table
            resultSet = pstat.executeQuery();

            // process query results
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            System.out.println("Authors Table of Books Database:\n");

            for (int i = 1; i <= numberOfColumns; i++)
                System.out.print(metaData.getColumnName(i) + "\t");

            System.out.println();

            while (resultSet.next()) {
                for (int i = 1; i <= numberOfColumns; i++)
                    System.out.print(resultSet.getObject(i) + "\t\t");
                System.out.println();
            }

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

        } finally {

            try {
                resultSet.close();
                pstat.close();
                connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}