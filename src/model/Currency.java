package model;

public class Currency {

  private String id;
  private Name name;
  private Code code;

  public enum Name {
    EURO,ARIARY,DOLLAR
  }
  public enum Code {
    EUR,AR,USD
  }

  public Currency(String id, Name name, Code code) {
    this.id = id;
    this.name = name;
    this.code = code;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public Code getCode() {
    return code;
  }

  public void setCode(Code code) {
    this.code = code;
  }
}
