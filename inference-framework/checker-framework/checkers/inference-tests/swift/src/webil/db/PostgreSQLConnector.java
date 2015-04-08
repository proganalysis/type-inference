package webil.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import webil.signature.Server;

/**
 * A utility class for establishing a connection to a PostgreSQL database.
 */
public class PostgreSQLConnector implements Connector, Server {
    public static final PostgreSQLConnector INSTANCE = new PostgreSQLConnector();
    
    private PostgreSQLConnector() {
    }
    
    public Connection connect(String host, String database,
        String username, String password) throws SQLException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Unable to load PostgreSQL driver." + e.getMessage());
        }

        return DriverManager.getConnection("jdbc:postgresql://" + host + "/"
            + database, username, password);
    }
}
