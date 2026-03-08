package org.example;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteTest {

  public static void main(String[] args) {

    connect();
  }

  public static PreparedStatement prepareSql(PreparedStatement statement) {
    var parser = new DataParser();
    String path = "/home/esp/Downloads/spotify";
    try {
      var songList = parser.getSongs(path);
      for (Song song : songList) {
        String longString = song.timeStamp()
            + song.username()
            + song.platform()
            + song.msPlayed()
            + song.masterMetadataTrackName()
            + song.masterMetadataAlbumArtistName()
            + song.masterMetadataAlbumName()
            + song.spotifyTrackUri()
            + song.episodeName()
            + song.episodeShowName()
            + song.spotifyEpisodeUri()
            + song.reasonStart()
            + song.reasonEnd()
            + song.shuffle()
            + song.skipped()
            + song.offline()
            + song.offlineTimestamp()
            + song.incognitoMode();

        return statement;
      }
    } catch (Exception e) {
      e.fillInStackTrace();
    }
    return null;
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
        var createStatement = prepareSql(createStatement);
        var insertStatement = conn.prepareStatement(sqlInsert);

        var statement = conn.createStatement();
        statement.execute(sqlCreate);

      }

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }
}
