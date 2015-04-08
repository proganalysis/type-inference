package shop;

public class User {
  public String username;
  public String name;
  public String email;
  public String password;
  public Integer ccard;
  public String billingAddr;

  public User(String user, String name, String email, String password) {
    this(user, name, email, password, null, null);
  }

  public User(String user, String name, String email, String password,
      Integer ccard, String billingAddr) {
    this.username = user;
    this.name = name;
    this.email = email;
    this.password = password;
    this.ccard = ccard;
    this.billingAddr = billingAddr;
  }
}

