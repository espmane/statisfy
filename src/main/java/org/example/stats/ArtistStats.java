package org.example.stats;

public enum ArtistStats implements Stat {

    TOP_50_BY_PLAY_COUNT("""
            SELECT master_metadata_album_artist_name          AS artist,
                   COUNT(*)                                   AS play_count,
                   ROUND(SUM(ms_played) / 3600000.0, 1)       AS total_hours,
                   COUNT(DISTINCT spotify_track_uri)          AS unique_tracks,
                   COUNT(DISTINCT master_metadata_album_name) AS unique_albums
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            ORDER BY play_count DESC
            LIMIT 50;"""),

    TOP_50_BY_TOTAL_TIME("""
            SELECT master_metadata_album_artist_name    AS artist,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS total_hours,
                   COUNT(*)                             AS play_count,
                   COUNT(DISTINCT spotify_track_uri)    AS unique_tracks
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            ORDER BY total_hours DESC
            LIMIT 50;"""),

    TOP_PER_YEAR("""
            WITH ranked AS (SELECT STRFTIME('%Y', time_stamp)        AS year,
                                   master_metadata_album_artist_name AS artist,
                                   SUM(ms_played)                    AS ms,
                                   RANK() OVER (
                                       PARTITION BY STRFTIME('%Y', time_stamp)
                                       ORDER BY SUM(ms_played) DESC
                                       )                             AS rnk
                            FROM songs
                            WHERE master_metadata_album_artist_name IS NOT NULL
                              AND spotify_track_uri IS NOT NULL
                            GROUP BY year, master_metadata_album_artist_name)
            SELECT year, artist, ROUND(ms / 3600000.0, 1) AS hours
            FROM ranked
            WHERE rnk = 1
            ORDER BY year;"""),

    TOP_3_PER_YEAR("""
            WITH ranked AS (SELECT STRFTIME('%Y', time_stamp)        AS year,
                                   master_metadata_album_artist_name AS artist,
                                   SUM(ms_played)                    AS ms,
                                   RANK() OVER (
                                       PARTITION BY STRFTIME('%Y', time_stamp)
                                       ORDER BY SUM(ms_played) DESC
                                       )                             AS rnk
                            FROM songs
                            WHERE master_metadata_album_artist_name IS NOT NULL
                              AND spotify_track_uri IS NOT NULL
                            GROUP BY year, master_metadata_album_artist_name)
            SELECT year, rnk AS rank, artist, ROUND(ms / 3600000.0, 1) AS hours
            FROM ranked
            WHERE rnk <= 3
            ORDER BY year, rnk;"""),

    FIRST_AND_LAST_LISTEN("""
            SELECT master_metadata_album_artist_name                                    AS artist,
                   DATE(MIN(time_stamp))                                                AS first_listen,
                   DATE(MAX(time_stamp))                                                AS last_listen,
                   CAST(JULIANDAY(MAX(time_stamp)) - JULIANDAY(MIN(time_stamp)) AS INT) AS days_in_library,
                   COUNT(*)                                                             AS total_plays,
                   ROUND(SUM(ms_played) / 3600000.0, 1)                                 AS total_hours,
                   COUNT(DISTINCT spotify_track_uri)                                    AS unique_tracks
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            ORDER BY total_hours DESC
            LIMIT 50;"""),

    ABANDONED("""
            SELECT master_metadata_album_artist_name    AS artist,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS total_hours,
                   COUNT(*)                             AS total_plays,
                   DATE(MAX(time_stamp))                AS last_listened
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            HAVING total_hours > 5
               AND last_listened < DATE((SELECT MAX(time_stamp) FROM songs), '-365 days')
            ORDER BY total_hours DESC
            LIMIT 30;"""),

    NEWLY_DISCOVERED("""
            SELECT master_metadata_album_artist_name    AS artist,
                   DATE(MIN(time_stamp))                AS first_heard,
                   COUNT(*)                             AS plays_since_discovery,
                   ROUND(SUM(ms_played) / 3600000.0, 1) AS hours_since_discovery
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            HAVING first_heard >= DATE((SELECT MAX(time_stamp) FROM songs), '-365 days')
            ORDER BY hours_since_discovery DESC
            LIMIT 30;"""),

    NEW_PER_YEAR("""
            SELECT STRFTIME('%Y', first_heard) AS year,
                   COUNT(*)                    AS new_artists
            FROM (SELECT master_metadata_album_artist_name,
                         MIN(time_stamp) AS first_heard
                  FROM songs
                  WHERE master_metadata_album_artist_name IS NOT NULL
                  GROUP BY master_metadata_album_artist_name)
            GROUP BY year
            ORDER BY year;"""),

    COMEBACK("""
            WITH artist_plays AS (SELECT master_metadata_album_artist_name AS artist,
                                         time_stamp,
                                         LAG(time_stamp) OVER (
                                             PARTITION BY master_metadata_album_artist_name
                                             ORDER BY time_stamp
                                             )                             AS prev_time_stamp
                                  FROM songs
                                  WHERE master_metadata_album_artist_name IS NOT NULL
                                    AND spotify_track_uri IS NOT NULL),
                 gaps AS (SELECT artist,
                                 prev_time_stamp                                                 AS gap_start,
                                 time_stamp                                                      AS gap_end,
                                 CAST(JULIANDAY(time_stamp) - JULIANDAY(prev_time_stamp) AS INT) AS gap_days
                          FROM artist_plays
                          WHERE prev_time_stamp IS NOT NULL)
            SELECT artist,
                   DATE(gap_start) AS stopped_listening,
                   DATE(gap_end)   AS came_back,
                   gap_days        AS days_away
            FROM gaps
            WHERE gap_days >= 365
            ORDER BY gap_days DESC
            LIMIT 30;"""),

    MOST_SKIPPED("""
            SELECT master_metadata_album_artist_name         AS artist,
                   COUNT(*)                                  AS total_plays,
                   SUM(skipped)                              AS skip_count,
                   ROUND(100.0 * SUM(skipped) / COUNT(*), 1) AS skip_rate_pct
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
              AND skipped IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            HAVING total_plays >= 10
            ORDER BY skip_rate_pct DESC
            LIMIT 30;"""),

    NEVER_SKIPPED("""
            SELECT master_metadata_album_artist_name                      AS artist,
                   COUNT(*)                                               AS total_plays,
                   ROUND(100.0 * (COUNT(*) - SUM(skipped)) / COUNT(*), 1) AS completion_rate_pct
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
              AND skipped IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            HAVING total_plays >= 10
            ORDER BY completion_rate_pct DESC, total_plays DESC
            LIMIT 30;"""),

    SHUFFLE_VS_LINEAR("""
            SELECT master_metadata_album_artist_name                                         AS artist,
                   SUM(CASE WHEN shuffle = 1 THEN 1 ELSE 0 END)                              AS shuffle_plays,
                   SUM(CASE WHEN shuffle = 0 THEN 1 ELSE 0 END)                              AS linear_plays,
                   ROUND(100.0 * SUM(CASE WHEN shuffle = 1 THEN 1 ELSE 0 END) / COUNT(*), 1) AS shuffle_pct
            FROM songs
            WHERE master_metadata_album_artist_name IS NOT NULL
              AND shuffle IS NOT NULL
            GROUP BY master_metadata_album_artist_name
            HAVING COUNT(*) >= 20
            ORDER BY shuffle_pct DESC
            LIMIT 30;"""),

    CONCENTRATION_PER_YEAR("""
            WITH yearly_total AS (SELECT STRFTIME('%Y', time_stamp) AS year, SUM(ms_played) AS total_ms
                                  FROM songs
                                  WHERE spotify_track_uri IS NOT NULL
                                  GROUP BY year),
                 artist_yearly AS (SELECT STRFTIME('%Y', time_stamp)        AS year,
                                          master_metadata_album_artist_name AS artist,
                                          SUM(ms_played)                    AS artist_ms,
                                          RANK() OVER (
                                              PARTITION BY STRFTIME('%Y', time_stamp)
                                              ORDER BY SUM(ms_played) DESC
                                              )                             AS rnk
                                   FROM songs
                                   WHERE master_metadata_album_artist_name IS NOT NULL
                                     AND spotify_track_uri IS NOT NULL
                                   GROUP BY year, master_metadata_album_artist_name)
            SELECT ay.year,
                   ROUND(100.0 * SUM(ay.artist_ms) / yt.total_ms, 1) AS top10_artists_pct_of_time
            FROM artist_yearly ay
                     JOIN yearly_total yt ON ay.year = yt.year
            WHERE ay.rnk <= 10
            GROUP BY ay.year
            ORDER BY ay.year;""");

    private final String query;

    ArtistStats(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}