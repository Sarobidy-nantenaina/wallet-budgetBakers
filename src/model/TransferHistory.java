package model;

import java.time.LocalDateTime;

public class TransferHistory {

  private String debitorTransactionId;
  private String creditorTransactionId;
  private double transferAmount;

  private LocalDateTime transferDate;


  public TransferHistory(String debitorTransactionId, String creditorTransactionId,
                         double transferAmount, LocalDateTime transferDate) {
    this.debitorTransactionId = debitorTransactionId;
    this.creditorTransactionId = creditorTransactionId;
    this.transferAmount = transferAmount;
    this.transferDate = transferDate;
  }

  public String getDebitorTransactionId() {
    return debitorTransactionId;
  }

  public void setDebitorTransactionId(String debitorTransactionId) {
    this.debitorTransactionId = debitorTransactionId;
  }

  public String getCreditorTransactionId() {
    return creditorTransactionId;
  }

  public void setCreditorTransactionId(String creditorTransactionId) {
    this.creditorTransactionId = creditorTransactionId;
  }

  public double getTransferAmount() {
    return transferAmount;
  }

  public void setTransferAmount(double transferAmount) {
    this.transferAmount = transferAmount;
  }

  public LocalDateTime getTransferDate() {
    return transferDate;
  }

  public void setTransferDate(LocalDateTime transferDate) {
    this.transferDate = transferDate;
  }
}
