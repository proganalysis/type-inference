package shop;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import webil.db.*;

public class ShopDB {
    private static final Connector connector = PostgreSQLConnector.INSTANCE;

    private final String host;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;

    private Connection db;

    public ShopDB(String host, String dbName, String dbUser, String dbPassword) {
        this.host = host;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    private ResultSet doQuery(String sqlQuery) throws SQLException {
        if (db == null) {
            db = connector.connect(host, dbName, dbUser, dbPassword);
        }

        try {
            return db.createStatement().executeQuery(sqlQuery);
        } catch (SQLException e) {
            db = null;
            throw e;
        }
    }

    private void executeSQL(String sql) throws SQLException {
        if (db == null) {
            db = connector.connect(host, dbName, dbUser, dbPassword);
        }

        try {
            db.createStatement().execute(sql);
        } catch (SQLException e) {
            db = null;
            throw e;
        }
    }

    public User authenticate(String user, String password) throws SQLException {
        if (user == null || password == null) return null;

        ResultSet rs =
            doQuery("SELECT * FROM shop_users WHERE username = '" + user
                + "' AND password = '" + password + "'");
        if (!rs.next()) return null;

        Integer ccard = new Integer(rs.getInt("ccard"));
        if (ccard.intValue() == 0) ccard = null;

        return new User(user, rs.getString("name"), rs.getString("email"),
            password, ccard, rs.getString("billing_addr"));
    }

    public Map getInventory() throws SQLException {
        ResultSet rs =
            doQuery("SELECT id, name, quantity, price FROM shop_items "
                + "ORDER BY id");

        Map result = new TreeMap();
        while (rs.next()) {
            result.put(new Integer(rs.getInt("id")), new Item(rs
                .getString("name"), rs.getInt("quantity"), rs.getInt("price")));
        }

        return result;
    }

    public boolean saveBillingInfo(String username, String billingAddr,
        int ccard) {

        try {
            executeSQL("UPDATE shop_users SET billing_addr = '" + billingAddr
                + "', ccard = '" + ccard + "' WHERE username = '" + username
                + "'");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean placeOrder(String username, Map cartContents)
        throws SQLException {

        executeSQL("BEGIN TRANSACTION");
        if (placeOrderHelper(cartContents)) {
            executeSQL("COMMIT");
            return true;
        }
        executeSQL("ROLLBACK");
        return false;
    }

    private boolean placeOrderHelper(Map cartContents) throws SQLException {
        for (Iterator it = cartContents.keySet().iterator(); it.hasNext();) {
            Integer itemID = (Integer)it.next();
            ResultSet rs =
                doQuery("SELECT quantity FROM shop_items WHERE id = '" + itemID
                    + "'");
            if (!rs.next()) return false;

            int inStock = rs.getInt("quantity");
            inStock -= ((Integer)cartContents.get(itemID)).intValue();
            if (((Integer)cartContents.get(itemID)).intValue() < 0 || inStock < 0)
                return false;

            executeSQL("UPDATE shop_items SET quantity = '" + inStock
                + "' WHERE id = '" + itemID + "'");
        }

        return true;
    }

    public boolean registerUser(String username, String name, String email,
        String password) throws SQLException {

        executeSQL("BEGIN TRANSACTION");
        ResultSet rs =
            doQuery("SELECT * FROM shop_users WHERE username = '" + username
                + "'");
        if (rs.next()) {
            executeSQL("ROLLBACK");
            return false;
        }

        executeSQL("INSERT INTO shop_users (username, name, email, password) "
            + "VALUES ('" + username + "', '" + name + "', '" + email + "', '"
            + password + "')");
        executeSQL("COMMIT");
        return true;
    }
}
