package org.example;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteTest {
    private static final DataParser parser = new DataParser();
    private static final String path = "/home/esp/Downloads/spotify";

    public static void main(String[] args) {
        connect();
    }

    public static PreparedStatement prepareSql(PreparedStatement statement) {
        try {
            var songList = parser.getSongs(path);
            for (Song song : songList) {
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
                statement.setInt(14, song.shuffle() != null && song.shuffle() ? 1 : 0);
                statement.setInt(15, song.skipped() != null && song.skipped() ? 1 : 0);
                statement.setInt(16, song.offline() != null && song.offline() ? 1 : 0);
                statement.setLong(17, song.offlineTimestamp() != null ? song.offlineTimestamp() : 0L);
                statement.setInt(18, song.incognitoMode() ? 1 : 0); // why is this a boolean??
                statement.addBatch();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return statement;
    }

    public static void connect() {
        String relativePath = "my.db"; // Get path from user later, and change to absolute path
        String url = "jdbc:sqlite:" + relativePath;
        var sqlCreate = "CREATE TABLE IF NOT EXISTS songs ("
                + "timeStamp TEXT, "
                + "username TEXT, "
                + "platform TEXT, "
                + "msPlayed INTEGER, "
                + "masterMetadataTrackName TEXT, "
                + "masterMetadataAlbumArtistName TEXT, "
                + "masterMetadataAlbumName TEXT, "
                + "spotifyTrackUri TEXT, "
                + "episodeName TEXT, "
                + "episodeShowName TEXT, "
                + "spotifyEpisodeUri TEXT, "
                + "reasonStart TEXT, "
                + "reasonEnd TEXT, "
                + "shuffle INTEGER, " // boolean
                + "skipped INTEGER, " // boolean
                + "offline INTEGER, " // boolean
                + "offlineTimestamp INTEGER, "
                + "incognitoMode INTEGER" // boolean
                + ");";

        var sqlInsert = "INSERT INTO songs("
                + "timeStamp,"
                + "username,"
                + "platform,"
                + "msPlayed,"
                + "masterMetadataTrackName,"
                + "masterMetadataAlbumArtistName,"
                + "masterMetadataAlbumName,"
                + "spotifyTrackUri,"
                + "episodeName,"
                + "episodeShowName,"
                + "spotifyEpisodeUri,"
                + "reasonStart,"
                + "reasonEnd,"
                + "shuffle," // boolean,
                + "skipped," // boolean,
                + "offline," // boolean,
                + "offlineTimestamp,"
                + "incognitoMode"; // boolean,

        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var createStatement = conn.prepareStatement(sqlCreate);

                var insertStatement = conn.prepareStatement(sqlInsert);
                prepareSql(insertStatement);
                insertStatement.executeBatch();


                var statement = conn.createStatement();
                statement.execute(sqlCreate);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
