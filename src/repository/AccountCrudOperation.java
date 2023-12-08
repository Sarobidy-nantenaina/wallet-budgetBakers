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
        // La mise à jour a réussi, vous pouvez retourner le compte mis à jour
        return toUpdate;
      } else {
        // La mise à jour n'a pas eu lieu, le compte n'a peut-être pas été trouvé
        return null;
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Gérez l'exception selon vos besoins
      return null;
    }
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
