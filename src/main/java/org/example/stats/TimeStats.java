package org.example.stats;

public enum TimeStats {

    BY_YEAR("""
            SELECT STRFTIME('%Y', time_stamp)                        AS year,
                   COUNT(*)                                          AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1)              AS hours_played,
                   COUNT(DISTINCT master_metadata_album_artist_name) AS unique_artists,
                   COUNT(DISTINCT spotify_track_uri)                 AS unique_tracks
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY year
            ORDER BY year;"""),

    BY_MONTH("""
            SELECT STRFTIME('%Y-%m', time_stamp)        AS month,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS hours_played
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY month
            ORDER BY month;"""),

    MOST_ACTIVE_MONTHS("""
            SELECT STRFTIME('%Y-%m', time_stamp)        AS month,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS hours_played,
                   COUNT(*)                             AS play_count
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY month
            ORDER BY hours_played DESC
            LIMIT 12;"""),

    BY_DAY_OF_WEEK("""
            SELECT CASE STRFTIME('%w', time_stamp)
                       WHEN '0' THEN '0_Sunday'
                       WHEN '1' THEN '1_Monday'
                       WHEN '2' THEN '2_Tuesday'
                       WHEN '3' THEN '3_Wednesday'
                       WHEN '4' THEN '4_Thursday'
                       WHEN '5' THEN '5_Friday'
                       WHEN '6' THEN '6_Saturday'
                       END                              AS day_of_week,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS hours_played,
                   ROUND(AVG(ms_played) / 1000.0, 0)    AS avg_seconds_per_play
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY STRFTIME('%w', time_stamp)
            ORDER BY day_of_week;"""),

    BY_HOUR("""
            SELECT STRFTIME('%H', time_stamp)           AS hour_of_day,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS hours_played
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY hour_of_day
            ORDER BY hour_of_day;"""),

    HEATMAP("""
            SELECT CASE STRFTIME('%w', time_stamp)
                       WHEN '0' THEN '0_Sun'
                       WHEN '1' THEN '1_Mon'
                       WHEN '2' THEN '2_Tue'
                       WHEN '3' THEN '3_Wed'
                       WHEN '4' THEN '4_Thu'
                       WHEN '5' THEN '5_Fri'
                       WHEN '6' THEN '6_Sat'
                       END                              AS day_of_week,
                   STRFTIME('%H', time_stamp)           AS hour,
                   COUNT(*)                             AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 2) AS hours_played
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY STRFTIME('%w', time_stamp), hour
            ORDER BY day_of_week, hour;"""),

    ROLLING_30D("""
            SELECT DATE(time_stamp)  AS day,
                   SUM(SUM(ms_played)) OVER (
                       ORDER BY DATE(time_stamp)
                       ROWS BETWEEN 29 PRECEDING AND CURRENT ROW
                       ) / 3600000.0 AS rolling_30d_hours
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY DATE(time_stamp)
            ORDER BY day;"""),

    LONGEST_SESSIONS("""
            WITH gaps AS (SELECT *,
                                 CAST(
                                         (JULIANDAY(time_stamp) - JULIANDAY(
                                                 LAG(time_stamp) OVER (ORDER BY time_stamp)
                                                                  )) * 86400
                                     AS INT) AS secs_since_last
                          FROM songs
                          WHERE spotify_track_uri IS NOT NULL),
                 session_labels AS (SELECT *,
                                           SUM(CASE
                                                   WHEN secs_since_last > 1800 OR secs_since_last IS NULL THEN 1
                                                   ELSE 0 END)
                                               OVER (ORDER BY time_stamp) AS session_id
                                    FROM gaps)
            SELECT session_id,
                   DATE(MIN(time_stamp))                             AS session_date,
                   MIN(time_stamp)                                   AS started_at,
                   MAX(time_stamp)                                   AS ended_at,
                   COUNT(*)                                          AS tracks_played,
                   ROUND(SUM(ms_played) / 3600000.0, 2)              AS hours_played,
                   COUNT(DISTINCT master_metadata_album_artist_name) AS artists_heard
            FROM session_labels
            GROUP BY session_id
            ORDER BY hours_played DESC
            LIMIT 20;"""),

    SESSIONS_BY_DAY_OF_WEEK("""
            WITH gaps AS (SELECT *,
                                 CAST(
                                         (JULIANDAY(time_stamp) - JULIANDAY(
                                                 LAG(time_stamp) OVER (ORDER BY time_stamp)
                                                                  )) * 86400
                                     AS INT) AS secs_since_last
                          FROM songs
                          WHERE spotify_track_uri IS NOT NULL),
                 session_labels AS (SELECT *,
                                           SUM(CASE
                                                   WHEN secs_since_last > 1800 OR secs_since_last IS NULL THEN 1
                                                   ELSE 0 END)
                                               OVER (ORDER BY time_stamp) AS session_id
                                    FROM gaps),
                 session_stats AS (SELECT session_id,
                                          STRFTIME('%w', MIN(time_stamp)) AS dow,
                                          SUM(ms_played)                  AS session_ms
                                   FROM session_labels
                                   GROUP BY session_id)
            SELECT CASE dow
                       WHEN '0' THEN '0_Sunday'
                       WHEN '1' THEN '1_Monday'
                       WHEN '2' THEN '2_Tuesday'
                       WHEN '3' THEN '3_Wednesday'
                       WHEN '4' THEN '4_Thursday'
                       WHEN '5' THEN '5_Friday'
                       WHEN '6' THEN '6_Saturday'
                       END                               AS day_of_week,
                   COUNT(*)                              AS session_count,
                   ROUND(AVG(session_ms) / 3600000.0, 2) AS avg_session_hours
            FROM session_stats
            GROUP BY dow
            ORDER BY dow;"""),

    FIRST_SONG_PER_YEAR("""
            SELECT STRFTIME('%Y', time_stamp)        AS year,
                   master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   time_stamp
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
              AND time_stamp = (SELECT MIN(time_stamp)
                                FROM songs s2
                                WHERE STRFTIME('%Y', s2.time_stamp) = STRFTIME('%Y', songs.time_stamp)
                                  AND s2.master_metadata_track_name IS NOT NULL)
            ORDER BY year;"""),

    LAST_SONG_PER_YEAR("""
            SELECT STRFTIME('%Y', time_stamp)        AS year,
                   master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   time_stamp
            FROM songs
            WHERE master_metadata_track_name IS NOT NULL
              AND time_stamp = (SELECT MAX(time_stamp)
                                FROM songs s2
                                WHERE STRFTIME('%Y', s2.time_stamp) = STRFTIME('%Y', songs.time_stamp)
                                  AND s2.master_metadata_track_name IS NOT NULL)
            ORDER BY year;"""),

    NEW_YEARS_DAY("""
            SELECT STRFTIME('%Y', time_stamp)        AS year,
                   master_metadata_track_name        AS track,
                   master_metadata_album_artist_name AS artist,
                   time_stamp
            FROM songs
            WHERE STRFTIME('%m-%d', time_stamp) = '01-01'
              AND master_metadata_track_name IS NOT NULL
            ORDER BY time_stamp;"""),

    UNIQUE_ARTISTS_PER_MONTH("""
            SELECT STRFTIME('%Y-%m', time_stamp)                     AS month,
                   COUNT(DISTINCT master_metadata_album_artist_name) AS unique_artists,
                   COUNT(DISTINCT spotify_track_uri)                 AS unique_tracks,
                   COUNT(*)                                          AS total_plays
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY month
            ORDER BY month;"""),

    VARIETY_SCORE("""
            SELECT STRFTIME('%Y-%m', time_stamp)                                  AS month,
                   COUNT(DISTINCT spotify_track_uri)                              AS unique_tracks,
                   COUNT(*)                                                       AS total_plays,
                   ROUND(100.0 * COUNT(DISTINCT spotify_track_uri) / COUNT(*), 1) AS variety_pct
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY month
            ORDER BY month;"""),

    BIGGEST_LISTENING_DAY("""
            SELECT DATE(time_stamp)                     AS day,
                   ROUND(SUM(ms_played) / 3600000.0, 2) AS hours_played,
                   COUNT(*)                             AS tracks_played
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY DATE(time_stamp)
            ORDER BY hours_played DESC
            LIMIT 1;"""),

    DAYS_OVER_8_HOURS("""
            SELECT DATE(time_stamp)                                  AS day,
                   ROUND(SUM(ms_played) / 3600000.0, 1)              AS hours_played,
                   COUNT(*)                                          AS tracks_played,
                   COUNT(DISTINCT master_metadata_album_artist_name) AS artists_heard
            FROM songs
            WHERE spotify_track_uri IS NOT NULL
            GROUP BY DATE(time_stamp)
            HAVING hours_played > 8
            ORDER BY hours_played DESC;""");

    private final String query;

    TimeStats(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}