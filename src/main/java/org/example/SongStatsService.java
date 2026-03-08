package org.example;

import java.util.List;

public class SongStatsService {

    //    getTotalListeningTime()
    public Long getTotalListeningTime(List<Song> songs) {
        Long time = 0L;
        for (Song song : songs) {
            time += song.msPlayed();
        }
        return msConvert(time, 3);
    }

//    getListeningTimePerArtist()
//    getListeningTimePerAlbum()
//    getListeningTimePerSong()
//    getListeningTimePerYear()
//    getListeningTimePerMonth()
//    getPeakListeningHours()

//    getMostPlayedSong()
//    getMostPlayedArtist()
//    getMostPlayedAlbum()
//    getUniqueArtistCount()
//    getUniqueAlbumCount()
//    getUniqueSongCount()
//    getPlayCountForSong()
//    getSkipCountForSong()
//
//    getSkipRate()
//    getSkipRateForArtist()
//    getShuffleListeningTime()
//    getOfflineListeningTime()
//    getListeningTimePerPlatform()

//    getPodcastListeningTime()
//    getMusicListeningTime()

    public static Long msConvert(Long time, int input) {
        return (long) switch (input) {
            case 1 -> time / 1000; // to seconds
            case 2 -> time / 60000; // to minutes
            case 3 -> time / 3600000; // to hours
            case 4 -> time / 86400000; // to days
            default -> 0;
        };
    }

}
