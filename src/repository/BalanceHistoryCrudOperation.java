package repository;

import DbConnect.PostgresDbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.BalanceHistory;

public class BalanceHistoryCrudOperation {

  PostgresDbConnection dbConnection = new PostgresDbConnection();
  Connection connection = dbConnection.getConnection();

  public BalanceHistory save(BalanceHistory toSave) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT INTO balance_history (account_id, datetime_from, datetime_to, balance) VALUES (?, ?, ?, ?)")) {

      preparedStatement.setString(1, toSave.getAccount_id());
      preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(toSave.getDateTimeFrom()));
      preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(toSave.getDateTimeTo()));
      preparedStatement.setDouble(4,toSave.getBalance());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return toSave;
  }

  public List<BalanceHistory> findAll() {
    List<BalanceHistory> balanceHistories = new ArrayList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM balance_history");
         ResultSet resultSet = preparedStatement.executeQuery()) {

      while (resultSet.next()) {
        BalanceHistory balanceHistory = mapResultSetToBalanceHistory(resultSet);
        balanceHistories.add(balanceHistory);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return balanceHistories;
  }

  public BalanceHistory update(BalanceHistory toUpdate) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "UPDATE balance_history SET datetime_from = ?, datetime_to = ?, balance = ? WHERE account_id = ?")) {

      preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(toUpdate.getDateTimeFrom()));
      preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(toUpdate.getDateTimeTo()));
      preparedStatement.setDouble(3, toUpdate.getBalance());
      preparedStatement.setString(4, toUpdate.getAccount_id());

      int rowsUpdated = preparedStatement.executeUpdate();

      if (rowsUpdated > 0) {
        return toUpdate;
      } else {
        return null;
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  private BalanceHistory mapResultSetToBalanceHistory(ResultSet resultSet) throws SQLException {
    String accountId = resultSet.getString("account_id");
    LocalDateTime dateTimeFrom = resultSet.getTimestamp("datetime_from").toLocalDateTime();
    LocalDateTime dateTimeTo = resultSet.getTimestamp("datetime_to").toLocalDateTime();
    double balance = resultSet.getDouble("balance");

    return new BalanceHistory(accountId, dateTimeFrom, dateTimeTo, balance);
  }


}
