package shop;

import webil.ui.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Cart {
    public final Main main;
    public final Panel panel;
    private User user;
    private Grid grid;
    private Text totalText;
    private Map inventory;
    private Button checkoutButton;
    private boolean checkingOut;

    /**
     * Maps ItemIDs to quantity ordered.
     */
    private LinkedHashMap cartContents;

    /**
     * Maps ItemIDs to "Remove from cart" buttons.
     */
    private Map removeFromCartMap;

    public Cart(Main main) {
        this.main = main;
        panel = new VerticalPanel();
        grid = new Grid(1, 5);
        totalText = new Text("");
        cartContents = new LinkedHashMap();
        removeFromCartMap = new HashMap();
        checkingOut = false;
        checkoutButton = new Button("Check out >");

        checkoutButton.addListener(new CheckoutButtonListener(this));

        panel.addChild(new Text("Your cart:"));
        panel.addChild(grid);
        panel.addChild(totalText);
        panel.addChild(checkoutButton);

        // Set up the shopping cart grid header.
        grid.setWidget(0, 0, new Text("Item"));
        grid.setWidget(0, 1, new Text("Quantity"));
        grid.setWidget(0, 2, new Text("Unit Price"));
        grid.setWidget(0, 3, new Text("Subtotal"));

        grid.setVisible(false);
    }

    public boolean setUser(User user) {
        if (user == this.user) return false;

        this.user = user;
        clear();
        return true;
    }

    private void clear() {
        cartContents.clear();
        checkoutMode(false);
    }

    public void notifyInventory(Map inventory) {
        this.inventory = inventory;

        cartContents.keySet().retainAll(inventory.keySet());
        
        // Go through the cart contents and make sure we have enough in stock.
        for (Iterator it = cartContents.keySet().iterator(); it.hasNext();) {
            Integer itemID = (Integer) it.next();
            int inCart = ((Integer) cartContents.get(itemID)).intValue();
            int inStock = ((Item) inventory.get(itemID)).stock;
            
            if (inCart > inStock) {
                cartContents.put(itemID, new Integer(inStock));
            }
        }
        
        refreshDisplay();
    }

    public void add(Integer itemID, int quantity) {
        if (quantity == 0) return;

        if (cartContents.containsKey(itemID))
            quantity += ((Integer)cartContents.get(itemID)).intValue();

        // Make sure we have enough in stock.
        Item item = (Item)inventory.get(itemID);
        boolean adjusted = quantity > item.stock;
        if (adjusted) quantity = item.stock;

        cartContents.put(itemID, new Integer(quantity));
        refreshDisplay();
        if (adjusted) {
            Popup.showMessage("Insufficient quantities of \"" + item.name
                + "\" in stock. Your cart " + "has been adjusted.");
        }
    }

    public void checkoutMode(boolean checkingOut) {
        this.checkingOut = checkingOut;
        checkoutButton.setVisible(!checkingOut);
        refreshDisplay();
    }

    public void remove(Integer itemID) {
        Item item = (Item)inventory.get(itemID);
        cartContents.remove(itemID);
        refreshDisplay();
        Popup.showMessage("Item \"" + item.name + "\" removed from cart.");
    }

    public User user() {
        return user;
    }

    private void refreshDisplay() {
        removeFromCartMap.keySet().retainAll(cartContents.keySet());

        grid.resize(cartContents.size() + 1, checkingOut ? 4 : 5);

        int cartTotal = 0;

        int rowNum = 1;
        for (Iterator it = cartContents.keySet().iterator(); it.hasNext();) {
            Integer itemID = (Integer)it.next();
            Button removeButton = (Button)removeFromCartMap.get(itemID);
            if (removeButton == null) {
                removeButton = new Button("Remove");
                removeButton.addListener(new RemoveFromCartListener(this,
                    itemID));
                removeFromCartMap.put(itemID, removeButton);
            }

            Item item = (Item)inventory.get(itemID);
            int amtInCart = ((Integer)cartContents.get(itemID)).intValue();
            int lineTotal = amtInCart * item.price;
            cartTotal += lineTotal;

            grid.setWidget(rowNum, 0, new Text(item.name));
            grid.setWidget(rowNum, 1, new Text("" + amtInCart));
            grid.setWidget(rowNum, 2, new Text("" + item.price));
            grid.setWidget(rowNum, 3, new Text("" + lineTotal));
            if (!checkingOut) grid.setWidget(rowNum, 4, removeButton);
            rowNum++;
        }

        if (cartContents.isEmpty()) {
            grid.setVisible(false);
            checkoutButton.setEnabled(false);
            totalText.setText("Your shopping cart is empty.");
        } else {
            grid.setVisible(true);
            checkoutButton.setEnabled(true);
            totalText.setText("Shopping cart total: " + cartTotal);
        }
    }

    public void placeOrder() {
        try {
            boolean success = main.db.placeOrder(user.username, cartContents);

            if (success) clear();
            main.theShop.refreshInventory();
            main.theShop.show();
            if (success) {
                Popup.showMessage("Your order has been placed.  Thank you for "
                    + "your money...err, I mean business!");
            } else {
                Popup.showMessage("Sorry, one or more items in your cart has "
                    + "sold out. Your cart has been adjusted accordingly.");
            }
        } catch (SQLException e) {
            Popup.showMessage("DB error while placing order. Try again later.");
            return;
        }
    }
}

class CheckoutButtonListener implements ClickListener {
    private Cart cart;

    public CheckoutButtonListener(Cart cart) {
        this.cart = cart;
    }

    public void onClick(Widget w) {
        new Checkout(cart).show();
    }
}

class RemoveFromCartListener implements ClickListener {
    private Cart cart;
    private Integer itemID;

    public RemoveFromCartListener(Cart cart, Integer itemID) {
        this.cart = cart;
        this.itemID = itemID;
    }

    public void onClick(Widget w) {
        cart.remove(itemID);
    }
}
