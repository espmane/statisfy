package org.example;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String jdbcUrl;
    private final SongWriter songWriter;
    private final DatabaseManager dbManager;
    private final DataParser dataParser;


    public DatabaseConnection(final String dbPath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath;
        this.dataParser = new DataParser();
        this.songWriter = new SongWriter();
        this.dbManager = new DatabaseManager();
    }

    public void connect(final String jsonPath) {
        try (var connection = DriverManager.getConnection(jdbcUrl)) {
            connection.setAutoCommit(false);
            try {
                final var songs = dataParser.getSongs(jsonPath);
                dbManager.createTable(connection);
                songWriter.insertSongs(connection, songs);
            } catch (final Exception e) {
                connection.rollback();
                System.err.println(e.getMessage());
            }
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}