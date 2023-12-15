package model;

import java.time.LocalDate;

public class CurrencyValue {

  private int id;
  private int idCurrencySource;
  private int idCurrencyDestination;
  private double value;
  private LocalDate dateValue;

  public CurrencyValue(int id, int idCurrencySource, int idCurrencyDestination, double value,
                       LocalDate dateValue) {
    this.id = id;
    this.idCurrencySource = idCurrencySource;
    this.idCurrencyDestination = idCurrencyDestination;
    this.value = value;
    this.dateValue = dateValue;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIdCurrencySource() {
    return idCurrencySource;
  }

  public void setIdCurrencySource(int idCurrencySource) {
    this.idCurrencySource = idCurrencySource;
  }

  public int getIdCurrencyDestination() {
    return idCurrencyDestination;
  }

  public void setIdCurrencyDestination(int idCurrencyDestination) {
    this.idCurrencyDestination = idCurrencyDestination;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public LocalDate getDateValue() {
    return dateValue;
  }

  public void setDateValue(LocalDate dateValue) {
    this.dateValue = dateValue;
  }
}
