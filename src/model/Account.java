package model;

import java.time.LocalDateTime;
import java.util.List;

public class Account {

  private String id;
  private String name;
  private double balance;
  private List<Transaction> transactions;
  private Currency currency;
  private AccountType type;

  public enum AccountType {
    BANK, CASH, MOBILE_MONEY
  }

  public Account performTransaction(String label, double amount, Transaction.TransactionType transactionType) {
    // Create a new transaction
    Transaction newTransaction = new Transaction(id,label, amount, LocalDateTime.now(), transactionType);

    // Update the balance based on the account type
    if (type == AccountType.BANK || (type != AccountType.BANK && balance >= amount)) {
      // For bank accounts or other accounts with sufficient balance
      if (transactionType == Transaction.TransactionType.DEBIT) {
        // For debit transactions, the balance can go negative for bank accounts
        balance -= amount;
      } else {
        // For credit transactions, add to the balance
        balance += amount;
      }

      // Add the transaction to the list of transactions
      transactions.add(newTransaction);

      // Return the updated account information
      return new Account(id, name, balance, transactions, currency);
    }

    // If the transaction cannot be performed, return null
    return null;
  }

  public Account(String id, String name, double balance,
                 List<Transaction> transactions, Currency currency) {
    this.id = id;
    this.name = name;
    this.balance = balance;
    this.transactions = transactions;
    this.currency = currency;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }


  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public AccountType getType() {
    return type;
  }

  public void setType(AccountType type) {
    this.type = type;
  }
}
