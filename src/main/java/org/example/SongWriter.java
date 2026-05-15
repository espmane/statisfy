package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SongWriter {
    private static final String SQL_INSERT = """
            INSERT INTO songs (
                time_stamp, username, platform, ms_played,
                master_metadata_track_name, master_metadata_album_artist_name, master_metadata_album_name,
                spotify_track_uri, episode_name, episode_show_name, spotify_episode_uri,
                reason_start, reason_end, shuffle, skipped, offline, offline_timestamp, incognito_mode
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT DO NOTHING;
            """;

    private void prepareBatch(final PreparedStatement statement, final List<Song> songs) throws SQLException {
        for (final Song song : songs) {
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