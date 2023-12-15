package repository;

import DbConnect.PostgresDbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import model.TransferHistory;

public class TransferHistoryCrudOperation {

  PostgresDbConnection dbConnection = new PostgresDbConnection();
  Connection connection = dbConnection.getConnection();

  public TransferHistory save(TransferHistory transferHistory) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT INTO transfer_history (account_id_debitor, account_id_creditor, amount, transfer_date) VALUES (?, ?, ?, ?)")) {

      preparedStatement.setString(1, transferHistory.getDebitorTransactionId());
      preparedStatement.setString(2, transferHistory.getCreditorTransactionId());
      preparedStatement.setDouble(3, transferHistory.getTransferAmount());
      preparedStatement.setTimestamp(4, Timestamp.valueOf(transferHistory.getTransferDate()));

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return transferHistory;
  }


}
