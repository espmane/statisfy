package org.example;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteTest {

    public static void main(String[] args) {
        connect();
    }

    public static void connect() {
        var sql = "CREATE TABLE IF NOT EXISTS songs ("
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
                + "shuffle INTEGER, " //boolean
                + "skipped INTEGER, " //boolean
                + "offline INTEGER, " //boolean
                + "offlineTimestamp INTEGER, "
                + "incognitoMode INTEGER" //boolean
                + ");";

        String relativePath = "my.db"; // use absolute path later
        String url = "jdbc:sqlite:" + relativePath;

        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var metaData = conn.getMetaData();
                var statement = conn.createStatement();
                statement.execute(sql);
            }


        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
