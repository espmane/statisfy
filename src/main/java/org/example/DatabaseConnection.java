package org.example;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String jdbcUrl;
    private final SongWriter songWriter;


    public DatabaseConnection(final String dbPath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath;
        this.songWriter = new SongWriter();
    }

    public void connect(final String jsonPath) {
        try (var connection = DriverManager.getConnection(jdbcUrl)) {
            connection.setAutoCommit(false);
            try {

            } catch (final Exception e) {
                connection.rollback();
                System.err.println(e.getMessage());
            }
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}