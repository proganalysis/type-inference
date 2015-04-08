package loginapp;

import webil.ui.*;

public class Login {

	private static int i = 0;

	public static void main() {
		new Login().run();
	}
	
	public TextBox username, password;
	public ButtonListenerApp goodie;

	public void run() {
		Panel root = RootPanel.getRootPanel();
		VerticalPanel v = new VerticalPanel();
		v.addChild(new Text(Integer.toString(Login.i)));
		i = i + 1;
		v.addChild(new Text("Please enter your username and password."));
		v.addChild(new Text(" "));
		HorizontalPanel user = new HorizontalPanel();
		user.addChild(new Text("Username: "));
		user.addChild(new Text(" "));
		username = new TextBox("");
		user.addChild(username);
		v.addChild(user);
		HorizontalPanel pass = new HorizontalPanel();
		pass.addChild(new Text("Password: "));
		pass.addChild(new Text(" "));
		password = new TextBox("");
		pass.addChild(password);
		v.addChild(pass);
		v.addChild(new Text(" "));
		Button b = new Button("Submit");
		b.addListener(new LoginSubmit(this));
		v.addChild(b);
		root.addChild(v);
		goodie = new ButtonListenerApp();
	}
	
}

class LoginSubmit implements ClickListener {

	private Login app;

	public LoginSubmit(Login app) {
		this.app = app;
	}
	
	public void onClick(Widget w) {
		String u = app.username.getText();
		String p = app.password.getText();
		
		if (u.equals("foo") && p.equals("bar")) {
			Panel root = RootPanel.getRootPanel();
			root.removeAllChildren();
			app.goodie.run();
		} else {
			((Button) w).setText("Try again");
		}
	}

}

class ButtonListenerApp {

private String serverMsg;
private Text text;
public TextBox textbox;

public int clickCount;

    public void run() {
        RootPanel p = RootPanel.getRootPanel();
        HorizontalPanel h = new HorizontalPanel();
        
        h.addChild(new Text("Hi there!"));
        h.addChild(new Text(" "));
        textbox = new TextBox("");
        h.addChild(textbox);
        Button b = new Button("Click me");
        b.addListener(new SubmitListener(this));
        h.addChild(b);
        h.addChild(new Text(" "));
        text = new Text("");
        h.addChild(text);
        p.addChild(h);
        serverMsg = "I'm on the server!";
        text.setText(serverMsg);
        showMsg();
    }
    
    private void showMsg() {
        textbox.setText("I'm in a method call!");
    }
    
}

class SubmitListener implements ClickListener {

    public ButtonListenerApp app;
    
    public SubmitListener(ButtonListenerApp app) {
        this.app = app;
    }
    
    public void onClick(Widget w) {
        app.clickCount = app.clickCount + 1;
        String s;
        
        Object o;
        o = new ArithmeticException();
        Object o2;
        o2 = o;
        
        if (app.clickCount == 1) {
            s = "You clicked me!";
        } else if (app.clickCount == 2) {
            s = "You clicked me again!";
        } else {
            s = ((Button) w).getText();
        }
        
        app.textbox.setText(s);
    }
    
}
