package shop;

import webil.ui.*;
import java.sql.SQLException;

public class Register {
    private final Main main;
    private final Panel panel;
    private final TextBox username;
    private final TextBox name;
    private final TextBox email;
    private final PasswordTextBox password;
    private final PasswordTextBox confirm;

    public Register(Main main) {
        this.main = main;
        this.panel = new VerticalPanel();
        this.username = new TextBox("");
        this.name = new TextBox("");
        this.email = new TextBox("");
        this.password = new PasswordTextBox();
        this.confirm = new PasswordTextBox();

        panel.addChild(new Text("Register new user"));

        Grid grid = new Grid(5, 2);
        grid.setWidget(0, 0, new Text("User ID:"));
        grid.setWidget(0, 1, username);
        grid.setWidget(1, 0, new Text("Full name:"));
        grid.setWidget(1, 1, name);
        grid.setWidget(2, 0, new Text("E-mail:"));
        grid.setWidget(2, 1, email);
        grid.setWidget(3, 0, new Text("Password:"));
        grid.setWidget(3, 1, password);
        grid.setWidget(4, 0, new Text("Confirm:"));
        grid.setWidget(4, 1, confirm);

        panel.addChild(grid);
        
        Panel buttons = new HorizontalPanel();
        panel.addChild(buttons);

        Button registerButton = new Button("Register");
        registerButton.addListener(new RegistrationListener(this));
        buttons.addChild(registerButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.addListener(new CancelRegistrationListener(main));
        buttons.addChild(cancelButton);
    }

    public void show() {
        username.setText("");
        name.setText("");
        email.setText("");
        password.setText("");
        confirm.setText("");
        
        main.mainPanel.removeAllChildren();
        main.mainPanel.addChild(panel);
    }
    
    public void register() {
        String username = this.username.getText().trim();
        String name = this.name.getText().trim();
        String email = this.email.getText().trim();
        String password = this.password.getText();
        String confirm = this.confirm.getText();
        
        if (username.equals("")) {
            Popup.showMessage("No User ID given.");
            return;
        }
        
        if (name.equals("")) {
            Popup.showMessage("No name given.");
            return;
        }
        
        if (email.equals("")) {
            Popup.showMessage("No e-mail address given.");
            return;
        }
        
        if (!password.equals(confirm)) {
            Popup.showMessage("Passwords do not match.");
            return;
        }
        
        if (password.equals("")) {
            Popup.showMessage("No password given.");
            return;
        }
        
        try {
            if (!main.db.registerUser(username, name, email, password)) {
                Popup.showMessage("That user ID is already taken.");
                return;
            }
        } catch (SQLException e) {
            Popup.showMessage("DB error.  Please try again later.");
            return;
        }
        
        main.login.show();
        Popup.showMessage("Registration succeeded. Please log in.");
    }
}

class RegistrationListener implements ClickListener {
    private Register register;

    public RegistrationListener(Register register) {
        this.register = register;
    }
    
    public void onClick(Widget w) {
        register.register();
    }
}

class CancelRegistrationListener implements ClickListener {
    private Main main;

    public CancelRegistrationListener(Main main) {
        this.main = main;
    }
    
    public void onClick(Widget w) {
        main.login.show();
    }
}

