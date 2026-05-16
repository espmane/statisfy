package org.example.stats;

public enum BehaviourStats {

    OVERALL_SKIP_RATE("""
            SELECT COUNT(*)                                  AS total_plays,
                   SUM(skipped)                              AS skipped_count,
                   COUNT(*) - SUM(skipped)                   AS completed_count,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 2) AS skip_rate_pct
            FROM songs
            WHERE skipped IS NOT NULL
              AND spotify_track_uri IS NOT NULL;"""),

    SKIP_RATE_BY_HOUR("""
            SELECT STRFTIME('%H', time_stamp)                AS hour,
                   COUNT(*)                                  AS total_plays,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 1) AS skip_rate_pct
            FROM songs
            WHERE skipped IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY hour
            ORDER BY hour;"""),

    SKIP_RATE_BY_DAY_OF_WEEK("""
            SELECT CASE STRFTIME('%w', time_stamp)
                       WHEN '0' THEN '0_Sunday'
                       WHEN '1' THEN '1_Monday'
                       WHEN '2' THEN '2_Tuesday'
                       WHEN '3' THEN '3_Wednesday'
                       WHEN '4' THEN '4_Thursday'
                       WHEN '5' THEN '5_Friday'
                       WHEN '6' THEN '6_Saturday'
                       END                                   AS day_of_week,
                   COUNT(*)                                  AS total_plays,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 1) AS skip_rate_pct
            FROM songs
            WHERE skipped IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY STRFTIME('%w', time_stamp)
            ORDER BY day_of_week;"""),

    SKIP_RATE_BY_PLATFORM("""
            SELECT platform,
                   COUNT(*)                                  AS total_plays,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 1) AS skip_rate_pct
            FROM songs
            WHERE skipped IS NOT NULL
              AND platform IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY platform
            ORDER BY skip_rate_pct DESC;"""),

    SHUFFLE_VS_LINEAR("""
            SELECT CASE WHEN shuffle = 1 THEN 'Shuffle' ELSE 'Linear' END AS mode,
                   COUNT(*)                                               AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1)                   AS hours_played,
                   ROUND(100.0 * COUNT(*) / SUM(COUNT(*)) OVER (), 1)     AS pct
            FROM songs
            WHERE shuffle IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY shuffle;"""),

    SKIP_ON_SHUFFLE("""
            SELECT CASE WHEN shuffle = 1 THEN 'Shuffle' ELSE 'Linear' END AS mode,
                   COUNT(*)                                               AS total_plays,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 2)              AS skip_rate_pct
            FROM songs
            WHERE shuffle IS NOT NULL
              AND skipped IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY shuffle;"""),

    SHUFFLE_OVER_YEARS("""
            SELECT STRFTIME('%Y', time_stamp)                                                AS year,
                   ROUND(100.0 * SUM(CASE WHEN shuffle = 1 THEN 1 ELSE 0 END) / COUNT(*), 1) AS shuffle_pct
            FROM songs
            WHERE shuffle IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY year
            ORDER BY year;"""),

    PLATFORM_BREAKDOWN("""
            SELECT platform,
                   COUNT(*)                                                       AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1)                           AS hours_played,
                   ROUND(100.0 * SUM(ms_played) / SUM(SUM(ms_played)) OVER (), 1) AS pct_of_time
            FROM songs
            WHERE platform IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY platform
            ORDER BY hours_played DESC;"""),

    PLATFORM_PER_YEAR("""
            SELECT STRFTIME('%Y', time_stamp)           AS year,
                   platform,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS hours_played
            FROM songs
            WHERE platform IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY year, platform
            ORDER BY year, hours_played DESC;"""),

    OFFLINE_VS_ONLINE("""
            SELECT CASE WHEN offline = 1 THEN 'Offline' ELSE 'Online' END AS mode,
                   COUNT(*)                                               AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1)                   AS hours_played,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 1)              AS skip_rate_pct
            FROM songs
            WHERE offline IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY offline;"""),

    INCOGNITO_BREAKDOWN("""
            SELECT CASE WHEN incognito_mode = 1 THEN 'Incognito' ELSE 'Normal' END AS mode,
                   COUNT(*)                                                        AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1)                            AS hours_played,
                   COUNT(DISTINCT master_metadata_album_artist_name)               AS unique_artists
            FROM songs
            WHERE incognito_mode IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY incognito_mode;"""),

    INCOGNITO_TRACKS("""
            SELECT master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   COUNT(*)                          AS incognito_plays
            FROM songs
            WHERE incognito_mode = 1
              AND master_metadata_track_name IS NOT NULL
            GROUP BY spotify_track_uri
            ORDER BY incognito_plays DESC
            LIMIT 30;"""),

    REASON_START("""
            SELECT reason_start,
                   COUNT(*)                                           AS count,
                   ROUND(100.0 * COUNT(*) / SUM(COUNT(*)) OVER (), 1) AS pct
            FROM songs
            WHERE reason_start IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY reason_start
            ORDER BY count DESC;"""),

    REASON_END("""
            SELECT reason_end,
                   COUNT(*)                                           AS count,
                   ROUND(100.0 * COUNT(*) / SUM(COUNT(*)) OVER (), 1) AS pct
            FROM songs
            WHERE reason_end IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY reason_end
            ORDER BY count DESC;"""),

    REASON_END_LABELLED("""
            SELECT CASE
                       WHEN reason_end = 'trackdone' THEN 'Finished naturally'
                       WHEN reason_end = 'fwdbtn' THEN 'Skipped forward'
                       WHEN reason_end = 'backbtn' THEN 'Went back'
                       WHEN reason_end = 'endplay' THEN 'Stopped playback'
                       WHEN reason_end = 'logout' THEN 'Logged out'
                       WHEN reason_end = 'remote' THEN 'Remote control'
                       ELSE reason_end
                       END                                            AS end_reason_label,
                   COUNT(*)                                           AS count,
                   ROUND(100.0 * COUNT(*) / SUM(COUNT(*)) OVER (), 1) AS pct
            FROM songs
            WHERE reason_end IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY reason_end
            ORDER BY count DESC;"""),

    PER_USER_TOTALS("""
            SELECT username,
                   COUNT(*)                                          AS total_plays,
                   ROUND(SUM(ms_played) / 3600000.0, 1)              AS total_hours,
                   COUNT(DISTINCT master_metadata_album_artist_name) AS unique_artists,
                   COUNT(DISTINCT spotify_track_uri)                 AS unique_tracks
            FROM songs
            WHERE username IS NOT NULL
              AND spotify_track_uri IS NOT NULL
            GROUP BY username
            ORDER BY total_hours DESC;"""),

    PER_USER_TOP_ARTIST("""
            WITH ranked AS (SELECT username,
                                   master_metadata_album_artist_name                                AS artist,
                                   SUM(ms_played)                                                   AS ms,
                                   RANK() OVER (PARTITION BY username ORDER BY SUM(ms_played) DESC) AS rnk
                            FROM songs
                            WHERE username IS NOT NULL
                              AND master_metadata_album_artist_name IS NOT NULL
                              AND spotify_track_uri IS NOT NULL
                            GROUP BY username, master_metadata_album_artist_name)
            SELECT username, artist, ROUND(ms / 3600000.0, 1) AS hours
            FROM ranked
            WHERE rnk = 1;""");

    private final String query;

    BehaviourStats(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}