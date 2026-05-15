package org.example.db;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryPrinter {
    private final Connection connection;

    public QueryPrinter(final Connection connection) {
        this.connection = connection;
    }

    public void print(final String label, final String sql) {
        System.out.println("\n--- " + label + " ---");
        try (var statement = connection.createStatement();
             var rs = statement.executeQuery(sql)) {
            final var meta = rs.getMetaData();
            final int cols = meta.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.println(meta.getColumnLabel(i) + ": " + rs.getString(i));
                }
                System.out.println();
            }
        } catch (final SQLException e) {
            System.err.println("Query failed [" + label + "]: " + e.getMessage());
        }
    }
}