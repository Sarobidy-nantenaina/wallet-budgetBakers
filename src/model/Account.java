package model;

import java.time.LocalDateTime;
import java.util.List;

public class Account {

  private String id;
  private String name;
  private double balance;
  private LocalDateTime lastUpdateDate;
  private List<Transaction> transactions;
  private Currency currency;
  private AccountType type;

  public enum AccountType {
    BANK, CASH, MOBILE_MONEY
  }

  public Account(String id, String name, double balance, LocalDateTime lastUpdateDate,
                 List<Transaction> transactions, Currency currency, AccountType type) {
    this.id = id;
    this.name = name;
    this.balance = balance;
    this.lastUpdateDate = lastUpdateDate;
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

  public LocalDateTime getLastUpdateDate() {
    return lastUpdateDate;
  }

  public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
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
