package org.example.stats;

public enum TrackStats implements Stat {

    TOP_50_BY_PLAY_COUNT("""
            SELECT master_metadata_track_name           AS track,
                   master_metadata_album_artist_name    AS artist,
                   master_metadata_album_name           AS album,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 2) AS total_hours,
                   ROUND(AVG(ms_played) / 1000.0, 0)    AS avg_seconds_per_play
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            GROUP BY spotify_track_uri
            ORDER BY play_count DESC
            LIMIT 50;"""),

    TOP_50_BY_TOTAL_TIME("""
            SELECT master_metadata_track_name           AS track,
                   master_metadata_album_artist_name    AS artist,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 2) AS total_hours
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            GROUP BY spotify_track_uri
            ORDER BY total_hours DESC
            LIMIT 50;"""),

    PLAYED_100_PLUS_TIMES("""
            SELECT master_metadata_track_name           AS track,
                   master_metadata_album_artist_name    AS artist,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS total_hours
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            GROUP BY spotify_track_uri
            HAVING play_count >= 100
            ORDER BY play_count DESC;"""),

    ONE_AND_DONE("""
            SELECT master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   DATE(time_stamp)                  AS played_on,
                   ROUND(ms_played / 60000.0, 1)     AS minutes_played
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            GROUP BY spotify_track_uri
            HAVING COUNT(*) = 1
            ORDER BY played_on DESC;"""),

    LONGEST_SINGLE_PLAY("""
            SELECT master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   time_stamp,
                   ROUND(ms_played / 60000.0, 1)     AS minutes_played
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            ORDER BY ms_played DESC
            LIMIT 20;"""),

    BINGE_DAYS("""
            SELECT DATE(time_stamp)                  AS day,
                   master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   COUNT(*)                          AS plays_that_day
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            GROUP BY DATE(time_stamp), spotify_track_uri
            ORDER BY plays_that_day DESC
            LIMIT 20;"""),

    MOST_VARIED_PLAY_TIME("""
            SELECT master_metadata_track_name                           AS track,
                   master_metadata_album_artist_name                    AS artist,
                   COUNT(*)                                             AS play_count,
                   ROUND(AVG(ms_played) / 1000.0, 0)                    AS avg_seconds,
                   ROUND(MIN(ms_played) / 1000.0, 0)                    AS min_seconds,
                   ROUND(MAX(ms_played) / 1000.0, 0)                    AS max_seconds,
                   ROUND((MAX(ms_played) - MIN(ms_played)) / 1000.0, 0) AS range_seconds
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            GROUP BY spotify_track_uri
            HAVING play_count >= 10
            ORDER BY range_seconds DESC
            LIMIT 30;"""),

    EARLY_SKIP("""
            SELECT master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   COUNT(*)                          AS play_count,
                   ROUND(AVG(ms_played) / 1000.0, 1) AS avg_seconds_played
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
            GROUP BY spotify_track_uri
            HAVING play_count >= 5
               AND avg_seconds_played < 30
            ORDER BY avg_seconds_played ASC
            LIMIT 30;"""),

    MOST_SKIPPED("""
            SELECT master_metadata_track_name                AS track,
                   master_metadata_album_artist_name         AS artist,
                   COUNT(*)                                  AS total_plays,
                   SUM(skipped)                              AS skip_count,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 1) AS skip_rate_pct
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
              AND skipped IS NOT NULL
            GROUP BY spotify_track_uri
            HAVING total_plays >= 5
            ORDER BY skip_rate_pct DESC
            LIMIT 30;"""),

    MOST_COMPLETED("""
            SELECT master_metadata_track_name                             AS track,
                   master_metadata_album_artist_name                      AS artist,
                   COUNT(*)                                               AS total_plays,
                   ROUND(100.0 * (COUNT(*) - SUM(skipped)) / COUNT(*), 1) AS completion_rate_pct
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
              AND skipped IS NOT NULL
            GROUP BY spotify_track_uri
            HAVING total_plays >= 5
            ORDER BY completion_rate_pct DESC, total_plays DESC
            LIMIT 30;""");

    private final String query;

    TrackStats(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}