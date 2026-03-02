package org.example;

import com.google.gson.annotations.SerializedName;

public record Song(
        @SerializedName("ts") String timeStamp,
        @SerializedName("username") String username,
        @SerializedName("platform") String platform,
        @SerializedName("ms_played") Long msPlayed,
        @SerializedName("master_metadata_track_name") String masterMetadataTrackName,
        @SerializedName("master_metadata_album_artist_name") String masterMetadataAlbumArtistName,
        @SerializedName("master_metadata_album_album_name") String masterMetadataAlbumName,
        @SerializedName("spotify_track_uri") String spotifyTrackUri,
        @SerializedName("episode_name") String episodeName,
        @SerializedName("episode_show_name") String episodeShowName,
        @SerializedName("spotify_episode_uri") String spotifyEpisodeUri,
        @SerializedName("reason_start") String reasonStart,
        @SerializedName("reason_end") String reasonEnd,
        @SerializedName("shuffle") Boolean shuffle,
        @SerializedName("skipped") Boolean skipped,
        @SerializedName("offline") Boolean offline,
        @SerializedName("offline_timestamp") Long offlineTimestamp,
        @SerializedName("incognito_mode") boolean incognitoMode) {
}