package webil.db;

import java.sql.Connection;
import java.sql.SQLException;

import webil.signature.Server;

/**
 * Interface for utility classes that connect to a database.
 */
public interface Connector extends Server {
    Connection connect(String host, String database, String username,
        String password) throws SQLException;
}
