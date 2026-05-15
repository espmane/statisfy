package org.example;

import org.example.db.DatabaseConnection;

public class Main {
    private static final String jsonPath = "/home/esp/Downloads/spotify";
    private static final String dbPath = "my.db"; // need to be changed to absolute path

    static void main() {
        final var db = new DatabaseConnection(dbPath);
        db.connect(jsonPath);
    }
}
