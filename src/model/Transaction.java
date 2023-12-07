package model;

import java.time.LocalDateTime;

public class Transaction {

  private String id;
  private String label;
  private double amount;
  private LocalDateTime dateTime;
  private TransactionType type;

  public enum TransactionType {
    DEBIT, CREDIT
  }

  public Transaction(String id,String label, double amount, LocalDateTime dateTime,
                     TransactionType type) {
    this.id = id;
    this.label = label;
    this.amount = amount;
    this.dateTime = dateTime;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }
}
