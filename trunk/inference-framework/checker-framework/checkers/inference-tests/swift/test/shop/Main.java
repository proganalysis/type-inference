package shop;

import webil.ui.Panel;
import webil.ui.RootPanel;
import webil.ui.Text;
import webil.ui.VerticalPanel;

public class Main {
    public final Panel mainPanel;
    public final Login login;
    public final Shop theShop;
    public final ShopDB db;

    public Main() {
        mainPanel = new VerticalPanel();
        db = new ShopDB("localhost", "swiftdb", "swift", "password");
        login = new Login(this);
        theShop = new Shop(this);
    }

    public void run() {
        RootPanel root = RootPanel.getRootPanel();
        root.addChild(new Text("Welcome to Our Shop!"));
        root.addChild(login.statusPanel);
        root.addChild(mainPanel);

        login.show();
    }

    public static void main() {
        new Main().run();
    }
}
