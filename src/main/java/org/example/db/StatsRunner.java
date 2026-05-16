package org.example.db;

import org.example.stats.Stat;

import java.sql.Connection;
import java.util.Set;

public class StatsRunner {
    private final Connection connection;
    private final Set<Stat> stats;

    public StatsRunner(final Connection connection, final Set<Stat> stats) {
        this.connection = connection;
        this.stats = stats;
    }

    public void run() {
        final var printer = new QueryPrinter(connection);
        stats.forEach(stat -> printer.print(stat.name(), stat.getQuery()));
    }
}