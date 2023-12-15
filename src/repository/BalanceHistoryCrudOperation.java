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
import model.Transaction;

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

  public List<BalanceHistory> getBalanceHistoryforOneAccount(String accountId, LocalDateTime startDate, LocalDateTime endDate) {
    // Vérifier que l'ID du compte et les dates sont valides
    if (accountId == null || startDate == null || endDate == null) {
      throw new IllegalArgumentException("L'ID du compte et les dates ne doivent pas être null");
    }

    // Récupération des transactions du compte
    List<Transaction> transactions = findTransactionsByAccountId(accountId);

    // Création d'une liste pour stocker l'historique du solde
    List<BalanceHistory> balanceHistory = new ArrayList<>();

    // Initialisation du solde initial
    double balance = 0;

    // Parcours des transactions
    for (Transaction transaction : transactions) {
      // Si la transaction a lieu dans l'intervalle de date donné
      if (transaction.getDateTime().isAfter(startDate) && transaction.getDateTime().isBefore(endDate)) {
        // Mise à jour du solde
        if (transaction.getType() == Transaction.TransactionType.CREDIT) {
          balance += transaction.getAmount();
        } else if (transaction.getType() == Transaction.TransactionType.DEBIT) {
          balance -= transaction.getAmount();
        }
      }
    }

    // Création d'une nouvelle instance de BalanceHistory
    BalanceHistory balanceHistoryItem = new BalanceHistory(accountId,startDate,endDate,balance);

    // Mettre à jour les attributs de la BalanceHistory
    balanceHistoryItem.setAccount_id(accountId);
    balanceHistoryItem.setDateTimeFrom(startDate);
    balanceHistoryItem.setDateTimeTo(endDate);
    balanceHistoryItem.setBalance(balance);

    // Ajouter la BalanceHistory à la liste
    balanceHistory.add(balanceHistoryItem);

    // Retourner la liste
    return balanceHistory;
  }

  public List<Transaction> findTransactionsByAccountId(String accountId) {
    String sql = "SELECT * FROM transactions WHERE account_id = ?";
    List<Transaction> transactions = new ArrayList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, accountId);
      ResultSet resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        String transactionId = resultSet.getString("transaction_id");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        Transaction.TransactionType type = Transaction.TransactionType.valueOf(resultSet.getString("type"));
        double amount = resultSet.getDouble("amount");

        transactions.add(new Transaction(transactionId, date, type, amount));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Gérer l'exception
    }

    return transactions;
  }


  private BalanceHistory mapResultSetToBalanceHistory(ResultSet resultSet) throws SQLException {
    String accountId = resultSet.getString("account_id");
    LocalDateTime dateTimeFrom = resultSet.getTimestamp("datetime_from").toLocalDateTime();
    LocalDateTime dateTimeTo = resultSet.getTimestamp("datetime_to").toLocalDateTime();
    double balance = resultSet.getDouble("balance");

    return new BalanceHistory(accountId, dateTimeFrom, dateTimeTo, balance);
  }


}
