package repository;

import DbConnect.PostgresDbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Account;
import model.Currency;
import model.Transaction;
import model.TransferHistory;

public class AccountCrudOperation {

  PostgresDbConnection dbConnection = new PostgresDbConnection();
  Connection connection = dbConnection.getConnection();
  public Account save(Account toSave) {


    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT INTO account (id, name, balance,transactions, currency, type) VALUES (?, ?, ?, ?, ?, ?)")) {

      // Utilisez le PreparedStatement pour insérer le compte dans la base de données
      preparedStatement.setString(1, toSave.getId());
      preparedStatement.setString(2, toSave.getName());
      preparedStatement.setDouble(3, toSave.getBalance());
      String transactionsAsString = convertTransactionsToString(toSave.getTransactions());
      preparedStatement.setString(4, transactionsAsString);
      preparedStatement.setString(5, toSave.getCurrency().getCode().name());
      preparedStatement.setString(6, toSave.getType().name());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return toSave;
  }

  private String convertTransactionsToString(List<Transaction> transactions) {
    StringBuilder jsonBuilder = new StringBuilder("[");

    for (Transaction transaction : transactions) {
      jsonBuilder.append("{");
      jsonBuilder.append("\"id\": \"" + transaction.getId() + "\",");
      jsonBuilder.append("\"label\": \"" + transaction.getLabel() + "\",");
      jsonBuilder.append("\"amount\": " + transaction.getAmount() + ",");
      jsonBuilder.append("\"dateTime\": \"" + transaction.getDateTime().toString() + "\",");
      jsonBuilder.append("\"type\": \"" + transaction.getType().toString() + "\",");
      jsonBuilder.append("\"account_id\": \"" + transaction.getAccount_id() + "\"");
      // Ajoutez d'autres propriétés selon vos besoins
      jsonBuilder.append("},");
    }

    if (!transactions.isEmpty()) {
      // Supprimez la virgule finale
      jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
    }

    jsonBuilder.append("]");

    return jsonBuilder.toString();
  }



  public List<Account> findAll() {
    List<Account> accounts = new ArrayList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account");
         ResultSet resultSet = preparedStatement.executeQuery()) {

      while (resultSet.next()) {
        Account account = mapResultSetToAccount(resultSet);
        accounts.add(account);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Gérez l'exception selon vos besoins
    }

    return accounts;
  }

  public Account update(Account toUpdate) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "UPDATE account SET name = ?, balance = ?, currency = ?, type = ? WHERE id = ?")) {

      preparedStatement.setString(1, toUpdate.getName());
      preparedStatement.setDouble(2, toUpdate.getBalance());
      preparedStatement.setString(3, toUpdate.getCurrency().getCode().name());
      preparedStatement.setString(4, toUpdate.getType().name());
      preparedStatement.setString(5, toUpdate.getId());

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


  public Account findAccountById(String accountId) {
    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE id = ?")) {
      preparedStatement.setString(1, accountId);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToAccount(resultSet);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  public void saveTransferHistory(String debitorTransactionId, String creditorTransactionId, LocalDateTime transferDate) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT INTO TransferHistory (ID, debitor_transaction_id, creditor_transaction_id, transfer_date) VALUES (?, ?, ?, ?)")) {
      preparedStatement.setString(1, UUID.randomUUID().toString());
      preparedStatement.setString(2, debitorTransactionId);
      preparedStatement.setString(3, creditorTransactionId);
      preparedStatement.setObject(4, transferDate);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<TransferHistory> getTransferHistoryInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    List<TransferHistory> transferHistoryList = new ArrayList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "SELECT * FROM TransferHistory WHERE transfer_date BETWEEN ? AND ?")) {
      preparedStatement.setObject(1, startDate);
      preparedStatement.setObject(2, endDate);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String debitorTransactionId = resultSet.getString("debitor_transaction_id");
          String creditorTransactionId = resultSet.getString("creditor_transaction_id");
          LocalDateTime transferDate = resultSet.getTimestamp("transfer_date").toLocalDateTime();

          Transaction debitorTransaction = getTransactionById(debitorTransactionId);
          Transaction creditorTransaction = getTransactionById(creditorTransactionId);

          TransferHistory transferHistory = new TransferHistory(debitorTransactionId, creditorTransactionId, transferDate);
          transferHistoryList.add(transferHistory);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return transferHistoryList;
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


  private TransferHistory mapResultSetToTransferHistory(ResultSet resultSet) throws SQLException {
    // Utilisez les données du ResultSet pour créer un objet TransferHistory
    String debitorTransactionId = resultSet.getString("debitor_transaction_id");
    String creditorTransactionId = resultSet.getString("creditor_transaction_id");
    LocalDateTime transferDate = resultSet.getTimestamp("transfer_date").toLocalDateTime();

    // Obtenez les détails des transactions débitrice et créditrice
    Transaction debitorTransaction = getTransactionById(debitorTransactionId);
    Transaction creditorTransaction = getTransactionById(creditorTransactionId);

    // Obtenez les comptes débiteur et créditeur
    String debitorAccount = String.valueOf(getAccountByTransactionId(debitorTransactionId));
    String creditorAccount = String.valueOf(getAccountByTransactionId(creditorTransactionId));

    // Obtenez le montant du transfert
    double transferAmount = getTransactionAmount(debitorTransaction);

    // Créez et retournez un objet TransferHistory
    return new TransferHistory(debitorAccount, creditorAccount, transferAmount, transferDate);
  }

  public double getTransactionAmount(Transaction transaction) {
    return transaction.getAmount();
  }



  public Account getAccountByTransactionId(String transactionId) {
    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE id IN (SELECT account_id FROM transaction WHERE transaction_id = ?)")) {
      preparedStatement.setString(1, transactionId);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToAccount(resultSet);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }


  private Transaction getTransactionById(String transactionId) {
    // Ajoutez la logique pour récupérer une transaction par son ID depuis la base de données
    // Vous pouvez réutiliser une méthode existante si vous en avez une
    return null;
  }


  // Méthode pour mapper un ResultSet vers un objet Account
  private Account mapResultSetToAccount(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String name = resultSet.getString("name");
    double balance = resultSet.getDouble("balance");
    String currencyCode = resultSet.getString("currency");
    Currency.Code currencyCodeEnum = Currency.Code.valueOf(currencyCode);
    Currency currency = new Currency(id, Currency.Name.valueOf(currencyCodeEnum.name()), currencyCodeEnum);
    String typeString = resultSet.getString("type");
    Account.AccountType type = (typeString != null) ? Account.AccountType.valueOf(typeString) : null;


    List<Transaction> transactions = getTransactionsForAccount(id); // Obtenir les transactions associées

    // Créez et retournez un objet Account avec la liste des transactions
    return new Account(id, name, balance, transactions, currency, type);
  }

  private List<Transaction> getTransactionsForAccount(String accountId) {
    List<Transaction> transactions = new ArrayList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transaction WHERE account_id = ?");
         ResultSet resultSet = preparedStatement.executeQuery()) {

      preparedStatement.setString(1, accountId); // Paramètre pour la clause WHERE

      while (resultSet.next()) {
        Transaction transaction = mapResultSetToTransaction(resultSet);
        transactions.add(transaction);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Gérez l'exception selon vos besoins
    }

    return transactions;
  }



  private Transaction mapResultSetToTransaction(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("transaction_id");
    String label = resultSet.getString("label");
    double amount = resultSet.getDouble("amount");
    LocalDateTime dateTime = resultSet.getTimestamp("datetime").toLocalDateTime();
    String typeString = resultSet.getString("transaction_type");
    Transaction.TransactionType type = (typeString != null) ? Transaction.TransactionType.valueOf(typeString) : null;
    String accountId = resultSet.getString("account_id");

    // Créez et retournez un objet Transaction
    return new Transaction(id, label, amount, dateTime, type, accountId);
  }



}
