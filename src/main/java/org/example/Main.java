package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final var songStats = new SongStatsService();
        final var parser = new DataParser();
        List<Song> songs = new ArrayList<>();
        var folderPath = "/lorem/ipsum/dolor/sit/amet";

        try {
            songs = parser.getSongs(folderPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        if (!songs.isEmpty()) {
            for (Song song : songs) {
                System.out.println(song);
            }
            System.out.println(songStats.getTotalListeningTime(songs));
        }
    }
}
