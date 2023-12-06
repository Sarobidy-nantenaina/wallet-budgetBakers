import java.sql.Connection;

public class Main {
  public static void main(String[] args) {

    PostgresDbConnection dbConnection = new PostgresDbConnection();

    Connection connection = dbConnection.getConnection();

    if (connection != null) {
      System.out.println("La base de données est connectée.");

    } else {
      System.out.println("Échec de la connexion à la base de données.");
    }
  }
}