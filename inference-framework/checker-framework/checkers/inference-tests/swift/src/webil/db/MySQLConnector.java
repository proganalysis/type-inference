package webil.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import webil.signature.Server;

/**
 * A utility class for establishing a connection to a MySQL database.
 */
public class MySQLConnector implements Connector, Server {
    public static final MySQLConnector INSTANCE = new MySQLConnector();

    private MySQLConnector() {
    }

    public Connection connect(String host, String database, String username,
        String password) throws SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Unable to load MySQL driver." + e.getMessage());
        }

        return DriverManager.getConnection("jdbc:mysql://" + host + "/"
            + database, username, password);
    }
}
