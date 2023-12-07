import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDbConnection {
  public static Connection getConnection() {
    // Database connection information
    String url = System.getenv("DB_URL");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");

    Connection connection = null;

    try {
      // Load JDBC driver
      Class.forName("org.postgresql.Driver");

      // Establish the connection
      connection = DriverManager.getConnection(url, user, password);

    } catch (ClassNotFoundException | SQLException e) {
      // Handle exceptions
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return connection;

  }

}
