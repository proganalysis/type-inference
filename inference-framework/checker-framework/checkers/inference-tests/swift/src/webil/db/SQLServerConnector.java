package webil.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import webil.signature.Server;

/**
 * A utility class for establishing a connection to a SQL Server database.
 */
public class SQLServerConnector implements Connector, Server {
    public static final SQLServerConnector INSTANCE = new SQLServerConnector();

    private SQLServerConnector() {}

    public Connection connect(String host, String database, String username,
        String password) throws SQLException {

        try {
            Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Unable to load SQL Server driver."
                + e.getMessage());
        }

        return DriverManager.getConnection("jdbc:sqlserver://" + host + ":1433"
            + ";databaseName=" + database + ";user=" + username + ";password="
            + password + ";");
    }
}
