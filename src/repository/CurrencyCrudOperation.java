package repository;

import DbConnect.PostgresDbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Currency;

public class CurrencyCrudOperation {

  PostgresDbConnection dbConnection = new PostgresDbConnection();
  Connection connection = dbConnection.getConnection();

  public Currency save(Currency toSave) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT INTO currency (id, name, code) VALUES (?, ?, ?)")) {

      preparedStatement.setString(1, toSave.getId());
      preparedStatement.setString(2, toSave.getName().name());
      preparedStatement.setString(3, toSave.getCode().name());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
      // Gérez l'exception selon vos besoins
    }
    return toSave;
  }

  public List<Currency> findAll() {
    List<Currency> currencies = new ArrayList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currency");
         ResultSet resultSet = preparedStatement.executeQuery()) {

      while (resultSet.next()) {
        Currency currency = mapResultSetToCurrency(resultSet);
        currencies.add(currency);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Gérez l'exception selon vos besoins
    }

    return currencies;
  }

  public Currency update(Currency toUpdate) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "UPDATE currency SET name = ?, code = ? WHERE id = ?")) {

      preparedStatement.setString(1, toUpdate.getName().name());
      preparedStatement.setString(2, toUpdate.getCode().name());
      preparedStatement.setString(3, toUpdate.getId());

      int rowsUpdated = preparedStatement.executeUpdate();

      if (rowsUpdated > 0) {
        // La mise à jour a réussi, vous pouvez retourner la devise mise à jour
        return toUpdate;
      } else {
        // La mise à jour n'a pas eu lieu, la devise n'a peut-être pas été trouvée
        return null;
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Gérez l'exception selon vos besoins
      return null;
    }
  }

  private Currency mapResultSetToCurrency(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    Currency.Name name = Currency.Name.valueOf(resultSet.getString("name"));
    Currency.Code code = Currency.Code.valueOf(resultSet.getString("code"));

    return new Currency(id, name, code);
  }

}
