package repository;

import DbConnect.PostgresDbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Account;
import model.Currency;
import model.Transaction;

public class AccountCrudOperation {

  PostgresDbConnection dbConnection = new PostgresDbConnection();
  Connection connection = dbConnection.getConnection();
  public Account save(Account toSave) {


    try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT INTO account (id, name, balance,transactions, currency, type) VALUES (?, ?, ?, ?, ?)")) {

      // Utilisez le PreparedStatement pour insérer le compte dans la base de données
      preparedStatement.setString(1, toSave.getId());
      preparedStatement.setString(2, toSave.getName());
      preparedStatement.setDouble(3, toSave.getBalance());
      preparedStatement.setString(4, toSave.getCurrency());
      preparedStatement.setString(5, toSave.getType().name());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return toSave;
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

  // Méthode pour mapper un ResultSet vers un objet Account
  private Account mapResultSetToAccount(ResultSet resultSet) throws SQLException {
    String id = resultSet.getString("id");
    String name = resultSet.getString("name");
    double balance = resultSet.getDouble("balance");
    // Obtenez le code de devise à partir de la colonne "code"
    String currencyCode = resultSet.getString("currency");

    // Créez un objet Currency avec le code de devise obtenu
    Currency currency = new Currency();
    currency.setCode(currencyCode);

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
