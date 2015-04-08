package shop;

import webil.ui.*;

public class Checkout {
    public final Main main;
    public final Cart cart;

    private final User user;

    private Panel checkoutPanel;

    private Text billingText;
    private Text ccardText;

    private TextBox billingTextBox;
    private NumberTextBox ccardTextBox;

    private Button placeOrderButton;
    private Button updateButton;
    private Button saveButton;
    private Button cancelButton;
    private Button keepShoppingButton;

    public Checkout(Cart cart) {
        this.main = cart.main;
        this.cart = cart;
        this.user = cart.user();

        cart.checkoutMode(true);
        checkoutPanel = new VerticalPanel();
        if (user.billingAddr == null) {
            billingText = new Text("");
        } else {
            billingText = new Text(user.billingAddr);
        }
        if (user.ccard == null) {
            ccardText = new Text("");
        } else {
            ccardText = new Text(user.ccard.toString());
        }
        billingTextBox = new TextBox("");
        ccardTextBox = new NumberTextBox();

        placeOrderButton = new Button("Place order >");
        placeOrderButton.addListener(new PlaceOrderListener(cart));

        updateButton = new Button("Change billing info");
        updateButton.addListener(new UpdateListener(this));

        saveButton = new Button("Save billing info");
        saveButton.addListener(new SaveListener(this));

        cancelButton = new Button("Cancel update");
        cancelButton.addListener(new CancelListener(this));

        keepShoppingButton = new Button("< Keep shopping");
        keepShoppingButton
            .addListener(new KeepShoppingButtonListener(main.theShop));

        Panel billingPanel = new HorizontalPanel();
        billingPanel.addChild(new Text("Billing address: "));
        billingPanel.addChild(billingText);
        billingPanel.addChild(billingTextBox);

        Panel ccardPanel = new HorizontalPanel();
        ccardPanel.addChild(new Text("Credit card: "));
        ccardPanel.addChild(ccardText);
        ccardPanel.addChild(ccardTextBox);

        Panel buttonPanel = new HorizontalPanel();
        buttonPanel.addChild(keepShoppingButton);
        buttonPanel.addChild(updateButton);
        buttonPanel.addChild(saveButton);
        buttonPanel.addChild(cancelButton);
        buttonPanel.addChild(placeOrderButton);

        checkoutPanel.addChild(billingPanel);
        checkoutPanel.addChild(ccardPanel);
        checkoutPanel.addChild(buttonPanel);

        boolean needInfo = user.billingAddr == null || user.ccard == null;
        setEditMode(needInfo);
        if (needInfo) cancelButton.setVisible(false);
    }

    public void saveBillingInfo() {
        String billing = billingTextBox.getText().trim();
        int ccard = ccardTextBox.getInteger().intValue();

        if ("".equals(billing)) {
            Popup.showMessage("Billing address cannot be empty.");
            return;
        }

        if (ccard == 0) {
            Popup.showMessage("Invalid credit card number.");
            return;
        }

        if (!main.db.saveBillingInfo(user.username, billing, ccard)) {
            Popup.showMessage("DB error while saving billing info.");
            return;
        }
        
        billingText.setText(billing);
        ccardText.setText(""+ccard);
        
        user.billingAddr = billing;
        user.ccard = new Integer(ccard);
        
        setEditMode(false);
    }

    public void show() {
        main.mainPanel.removeAllChildren();
        main.mainPanel.addChild(cart.panel);
        main.mainPanel.addChild(checkoutPanel);
    }

    public void setEditMode(boolean editMode) {
        if (editMode) {
            // Populate the text boxes.
            billingTextBox.setText(billingText.getText());
            ccardTextBox.setText(ccardText.getText());
        }

        billingText.setVisible(!editMode);
        billingTextBox.setVisible(editMode);
        ccardText.setVisible(!editMode);
        ccardTextBox.setVisible(editMode);
        updateButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        cancelButton.setVisible(editMode);
        placeOrderButton.setVisible(!editMode);
    }
}

class PlaceOrderListener implements ClickListener {
    private Cart cart;

    public PlaceOrderListener(Cart cart) {
        this.cart = cart;
    }

    public void onClick(Widget w) {
        cart.placeOrder();
    }
}

class UpdateListener implements ClickListener {
    private Checkout checkout;

    public UpdateListener(Checkout checkout) {
        this.checkout = checkout;
    }

    public void onClick(Widget w) {
        checkout.setEditMode(true);
    }
}

class SaveListener implements ClickListener {
    private Checkout checkout;

    public SaveListener(Checkout checkout) {
        this.checkout = checkout;
    }

    public void onClick(Widget w) {
        checkout.saveBillingInfo();
    }
}

class CancelListener implements ClickListener {
    private Checkout checkout;

    public CancelListener(Checkout checkout) {
        this.checkout = checkout;
    }

    public void onClick(Widget w) {
        checkout.setEditMode(false);
    }
}

class KeepShoppingButtonListener implements ClickListener {
    private Shop theShop;

    public KeepShoppingButtonListener(Shop theShop) {
        this.theShop = theShop;
    }

    public void onClick(Widget w) {
        theShop.show();
    }
}
