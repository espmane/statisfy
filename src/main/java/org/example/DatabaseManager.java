package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String SQL_CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS songs (
                id INTEGER PRIMARY KEY,
                time_stamp TEXT,
                username TEXT,
                platform TEXT,
                ms_played INTEGER,
                master_metadata_track_name TEXT,
                master_metadata_album_artist_name TEXT,
                master_metadata_album_name TEXT,
                spotify_track_uri TEXT,
                episode_name TEXT,
                episode_show_name TEXT,
                spotify_episode_uri TEXT,
                reason_start TEXT,
                reason_end TEXT,
                shuffle INTEGER,
                skipped INTEGER,
                offline INTEGER,
                offline_timestamp INTEGER,
                incognito_mode INTEGER,
                UNIQUE(time_stamp, ms_played, spotify_track_uri, incognito_mode, reason_start, reason_end, offline, shuffle)
            );""";

    public void createTable(final Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(SQL_CREATE_TABLE);
        }
    }
}