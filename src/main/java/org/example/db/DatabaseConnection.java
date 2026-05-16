package org.example.db;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import org.example.parser.DataParser;
import org.example.stats.Stat;

public class DatabaseConnection {
    private final String jdbcUrl;
    private final SongWriter songWriter;
    private final DatabaseManager dbManager;
    private final DataParser dataParser;
    private final Set<Stat> stats;

    public DatabaseConnection(final String dbPath, final Set<Stat> stats) {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath;
        this.dataParser = new DataParser();
        this.songWriter = new SongWriter();
        this.dbManager = new DatabaseManager();
        this.stats = stats;
    }

    public void connect(final String jsonPath) {
        try (var connection = DriverManager.getConnection(jdbcUrl)) {
            connection.setAutoCommit(false);
            try {
                final var songs = dataParser.getSongs(jsonPath);
                dbManager.createTable(connection);
                songWriter.insertSongs(connection, songs);
                new StatsRunner(connection, stats).run();
            } catch (final Exception e) {
                connection.rollback();
                System.err.println(e.getMessage());
            }
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}