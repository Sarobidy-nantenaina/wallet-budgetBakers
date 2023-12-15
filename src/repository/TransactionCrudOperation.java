package repository;

import DbConnect.PostgresDbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Category;
import model.Transaction;

public class TransactionCrudOperation {

  PostgresDbConnection dbConnection = new PostgresDbConnection();
  Connection connection = dbConnection.getConnection();

  public Transaction save(Transaction toSave) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT INTO transaction (id, label, amount, datetime, transaction_type, account_id, categoryId) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

      preparedStatement.setString(1, toSave.getId());
      preparedStatement.setString(2, toSave.getLabel());
      preparedStatement.setDouble(3, toSave.getAmount());
      preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(toSave.getDateTime()));
      preparedStatement.setString(5, toSave.getType().name());
      preparedStatement.setString(6, toSave.getAccount_id());
      preparedStatement.setString(7, toSave.getCategory().getCategoryId());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
      // Gérez l'exception selon vos besoins
    }
    return toSave;
  }

//  public List<Transaction> findAll() {
//    List<Transaction> transactions = new ArrayList<>();
//
//    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transaction");
//         ResultSet resultSet = preparedStatement.executeQuery()) {
//
//      while (resultSet.next()) {
//        Transaction transaction = mapResultSetToTransaction(resultSet);
//        transactions.add(transaction);
//      }
//
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//
//    return transactions;
//  }

  public Transaction update(Transaction toUpdate) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
            "UPDATE transaction SET label = ?, amount = ?, datetime = ?, transaction_type = ?, account_id = ?, categoryId = ? WHERE id = ?")) {

      preparedStatement.setString(1, toUpdate.getLabel());
      preparedStatement.setDouble(2, toUpdate.getAmount());
      preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(toUpdate.getDateTime()));
      preparedStatement.setString(4, toUpdate.getType().name());
      preparedStatement.setString(5, toUpdate.getAccount_id());
      preparedStatement.setString(6, toUpdate.getId());
      preparedStatement.setString(7, toUpdate.getCategory().getCategoryId()); // Ajoutez la catégorie à la requête SQL


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
}

//  private Transaction mapResultSetToTransaction(ResultSet resultSet) throws SQLException {
//    String id = resultSet.getString("id");
//    String label = resultSet.getString("label");
//    double amount = resultSet.getDouble("amount");
//    LocalDateTime dateTime = resultSet.getTimestamp("datetime").toLocalDateTime();
//    Transaction.TransactionType type = Transaction.TransactionType.valueOf(resultSet.getString("transaction_type"));
//    String accountId = resultSet.getString("account_id");
//
//    return new Transaction(id, label, amount, dateTime, type, accountId,category);
//  }
//
//}
