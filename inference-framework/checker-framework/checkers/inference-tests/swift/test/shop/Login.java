package shop;

import java.sql.SQLException;
import webil.ui.*;

public class Login {
    public final Main main;
    public final ShopDB db;
    
    public final Panel statusPanel;
    private Text message;
    
    private Panel loginPanel;
    private TextBox username;
    private PasswordTextBox password;
    private Button logoutButton;

    public Login(Main main) {
        this.main = main; 
        this.db = main.db;

        statusPanel = new VerticalPanel();
        loginPanel = new VerticalPanel();
        message = new Text("");
        logoutButton = new Button("Log out");
        username = new TextBox("");
        password = new PasswordTextBox();

        statusPanel.addChild(message);
        statusPanel.addChild(logoutButton);
        logoutButton.setVisible(false);
        logoutButton.addListener(new LogoutButtonListener(this));
        
        loginPanel.addChild(new Text(" "));
        loginPanel.addChild(new Text("Log in:"));
        loginPanel.addChild(new Text(" "));

        Panel user = new HorizontalPanel();
        user.addChild(new Text("User ID: "));
        user.addChild(new Text(" "));
        user.addChild(username);
        loginPanel.addChild(user);

        Panel pass = new HorizontalPanel();
        pass.addChild(new Text("Password: "));
        pass.addChild(new Text(" "));
        pass.addChild(password);
        loginPanel.addChild(pass);
        
        Panel buttons = new HorizontalPanel();
        loginPanel.addChild(buttons);

        Button b = new Button("Log in");
        b.addListener(new LoginSubmit(this));
        buttons.addChild(b);
        
        Button registerButton = new Button("Register new user");
        registerButton.addListener(new RegisterButtonListener(main));
        buttons.addChild(registerButton);
    }

    public void show() {
        show("");
    }

    public void show(String message) {
        this.message.setText(message);
        this.logoutButton.setVisible(false);
        
        username.setText("");
        password.setText("");
        
        main.mainPanel.removeAllChildren();
        main.mainPanel.addChild(loginPanel);
    }
    
    public void userLoggedIn(String name) {
        this.message.setText("You are logged in as " + name);
        this.logoutButton.setVisible(true);
    }

    public String getUser() {
        return username.getText();
    }

    public String getPassword() {
        return password.getText();
    }
}

class LoginSubmit implements ClickListener {
    private Login login;

    public LoginSubmit(Login login) {
        this.login = login;
    }

    public void onClick(Widget w) {
        String username = login.getUser();
        String password = login.getPassword();
        try {
            User user = login.db.authenticate(username, password);
            if (user != null) {
                login.userLoggedIn(user.name);
                login.main.theShop.show(user);
            } else {
                login.show("Invalid username or password. Please try again.");
            }
        } catch (SQLException e) {
            login.show("DB error. Please try again later.");
        }
    }
}

class LogoutButtonListener implements ClickListener {
    private Login login;

    public LogoutButtonListener(Login login) {
        this.login = login;
    }

    public void onClick(Widget w) {
        login.show();
    }
}

class RegisterButtonListener implements ClickListener {
    private Register register;
    
    public RegisterButtonListener(Main main) {
        this.register = new Register(main);
    }
    
    public void onClick(Widget w) {
        register.show();
    }
}
