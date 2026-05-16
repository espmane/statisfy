package org.example;

import org.example.db.DatabaseConnection;
import org.example.stats.*;

public class Main {
    private static final String JSON_PATH = "/home/esp/Downloads/spotify";
    private static final String DB_PATH = "my.db";

    static void main() {
        final var stats = StatCollector.of(ArtistStats.class, BehaviourStats.class);
        new DatabaseConnection(DB_PATH, stats).connect(JSON_PATH);
    }
}