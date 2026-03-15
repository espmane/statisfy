package org.example;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class SQLiteConnection {
    private static final DataParser parser = new DataParser();
    private static final String jsonPath = "/home/esp/Downloads/spotify";
    private static final String dbPath = "my.db"; // need to be changed to absolute path
    private static final String jdbcUrl = "jdbc:sqlite:" + dbPath;
    private static final String sqlCreateTable = """
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
                 shuffle INTEGER, -- boolean
                 skipped INTEGER, -- boolean
                 offline INTEGER, -- boolean
                 offline_timestamp INTEGER,
                 incognito_mode INTEGER,  -- boolean
                 UNIQUE(time_stamp, ms_played, spotify_track_uri, incognito_mode, reason_start, reason_end, offline, shuffle)
                );""";

    private static final String sqlInsert = """
            INSERT INTO songs (
                time_stamp, username, platform, ms_played,
                master_metadata_track_name, master_metadata_album_artist_name, master_metadata_album_name,
                spotify_track_uri, episode_name, episode_show_name, spotify_episode_uri,
                reason_start, reason_end, shuffle, skipped, offline, offline_timestamp, incognito_mode
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT DO NOTHING;
            """;

    public void connect() {
        try (var connection = DriverManager.getConnection(jdbcUrl)) {
            executeTransaction(connection);
        } catch (final SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void executeTransaction(final Connection connection) throws SQLException {
        try {
            connection.setAutoCommit(false);

            createTable(connection);
            insertSongs(connection, parser.getSongs(jsonPath));
        } catch (final Exception e) {
            connection.rollback();
            System.err.println(e.getMessage());
        }
    }

    private void createTable(final Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sqlCreateTable);
        }
    }

    private void insertSongs(final Connection connection, final List<Song> songs) throws SQLException {
        try (var statement = connection.prepareStatement(sqlInsert)) {
            prepareBatch(statement, songs);

            System.out.println("Populating database...");
            final long start = System.currentTimeMillis();
            final var results = statement.executeBatch();
            final double elapsed = (System.currentTimeMillis() - start) / 1000.0;
            System.out.println("Inserted " + Arrays.stream(results).filter(r -> r > 0).count() + " rows in " + elapsed + "seconds");

            connection.commit();
        }
    }

    private static void prepareBatch(final PreparedStatement statement, final List<Song> songList) throws SQLException {
        for (final Song song : songList) {
            statement.setString(1, song.timeStamp());
            statement.setString(2, song.username());
            statement.setString(3, song.platform());
            statement.setLong(4, song.msPlayed() != null ? song.msPlayed() : 0L);
            statement.setString(5, song.masterMetadataTrackName());
            statement.setString(6, song.masterMetadataAlbumArtistName());
            statement.setString(7, song.masterMetadataAlbumName());
            statement.setString(8, song.spotifyTrackUri());
            statement.setString(9, song.episodeName());
            statement.setString(10, song.episodeShowName());
            statement.setString(11, song.spotifyEpisodeUri());
            statement.setString(12, song.reasonStart());
            statement.setString(13, song.reasonEnd());
            statement.setInt(14, song.shuffle() ? 1 : 0);
            statement.setInt(15, song.skipped() ? 1 : 0);
            statement.setInt(16, song.offline() ? 1 : 0);
            statement.setLong(17, song.offlineTimestamp() != null ? song.offlineTimestamp() : 0L);
            statement.setInt(18, song.incognitoMode() ? 1 : 0);
            statement.addBatch();
        }
    }
}
