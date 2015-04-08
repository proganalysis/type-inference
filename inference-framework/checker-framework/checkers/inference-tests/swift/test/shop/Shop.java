package shop;

import webil.ui.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

public class Shop {
    public final Main main;
    private Grid inventoryGrid;
    private Cart cart;

    /**
     * Maps ItemIDs to "Quantity" NumberTextBoxes.
     */
    private Map quantityMap;

    /**
     * Maps ItemIDs to "Add to cart" Buttons.
     */
    private Map addToCartMap;

    public Shop(Main main) {
        this.main = main;

        inventoryGrid = new Grid(1, 5);
        cart = new Cart(main);
        quantityMap = new LinkedHashMap();
        addToCartMap = new LinkedHashMap();

        // Set up the inventory panel.
        inventoryGrid.setWidget(0, 0, new Text("Item"));
        inventoryGrid.setWidget(0, 1, new Text("Price"));
        inventoryGrid.setWidget(0, 2, new Text("In Stock"));
        inventoryGrid.setWidget(0, 3, new Text("Amount to buy"));
    }

    public void refreshInventory() {
        try {
            Map inventory = main.db.getInventory();
            quantityMap.keySet().retainAll(inventory.keySet());
            addToCartMap.keySet().retainAll(inventory.keySet());
            for (Iterator it = inventory.keySet().iterator(); it.hasNext();) {
                Integer itemID = (Integer)it.next();
                if (!quantityMap.containsKey(itemID)) {
                    NumberTextBox input = new NumberTextBox();
                    Button addToCart = new Button("Add");
                    addToCart.addListener(new AddToCartListener(cart, itemID,
                        input));
                    quantityMap.put(itemID, input);
                    addToCartMap.put(itemID, addToCart);
                }
            }

            inventoryGrid.resizeRows(quantityMap.size() + 1);
            int rowNum = 1;
            for (Iterator it = quantityMap.keySet().iterator(); it.hasNext();) {
                Integer itemID = (Integer)it.next();
                Item item = (Item)inventory.get(itemID);
                NumberTextBox input = (NumberTextBox)quantityMap.get(itemID);
                Button addToCart = (Button)addToCartMap.get(itemID);

                inventoryGrid.setWidget(rowNum, 0, new Text(item.name));
                inventoryGrid.setWidget(rowNum, 1, new Text(new Integer(
                    item.price).toString()));
                inventoryGrid.setWidget(rowNum, 2, new Text(new Integer(
                    item.stock).toString()));
                inventoryGrid.setWidget(rowNum, 3, input);
                inventoryGrid.setWidget(rowNum, 4, addToCart);
                rowNum++;
            }

            cart.notifyInventory(inventory);
        } catch (SQLException e) {
            main.login.show("DB error. Please come back later.");
        }
    }
    
    public void show() {
        main.mainPanel.removeAllChildren();
        
        cart.checkoutMode(false);

        main.mainPanel.addChild(inventoryGrid);
        main.mainPanel.addChild(cart.panel);
    }

    public void show(User user) {
        if (cart.setUser(user)) {
            inventoryGrid.resizeRows(1);
            quantityMap.clear();
            addToCartMap.clear();
            refreshInventory();
        }
        
        show();
    }
}

class AddToCartListener implements ClickListener {
    private Cart cart;
    private Integer itemID;
    private NumberTextBox quantityInput;

    public AddToCartListener(Cart cart, Integer itemID,
        NumberTextBox quantityInput) {

        this.cart = cart;
        this.itemID = itemID;
        this.quantityInput = quantityInput;
    }

    public void onClick(Widget w) {
        cart.add(itemID, quantityInput.getInteger().intValue());
        quantityInput.setText("");
    }
}
