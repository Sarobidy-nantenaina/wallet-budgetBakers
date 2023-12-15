package model;

import java.time.LocalDateTime;

  public class BalanceHistory {
    private String account_id;
    private LocalDateTime dateTimeFrom;
    private LocalDateTime dateTimeTo;
    private double balance;

    public BalanceHistory(String account_id, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                          double balance) {
      this.account_id = account_id;
      this.dateTimeFrom = dateTimeFrom;
      this.dateTimeTo = dateTimeTo;
      this.balance = balance;
    }

    public String getAccount_id() {
      return account_id;
    }

    public void setAccount_id(String account_id) {
      this.account_id = account_id;
    }

    public LocalDateTime getDateTimeFrom() {
      return dateTimeFrom;
    }

    public void setDateTimeFrom(LocalDateTime dateTimeFrom) {
      this.dateTimeFrom = dateTimeFrom;
    }

    public LocalDateTime getDateTimeTo() {
      return dateTimeTo;
    }

    public void setDateTimeTo(LocalDateTime dateTimeTo) {
      this.dateTimeTo = dateTimeTo;
    }

    public double getBalance() {
      return balance;
    }

    public void setBalance(double balance) {
      this.balance = balance;
    }
  }
