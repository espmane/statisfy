package org.example;

public class Test {
    static void main() {
        var parser = new DataParser();
        var sss = new SongStatsService();
        String path = "/home/esp/Downloads/spotify";


        try {
            System.out.println(sss.getTotalListeningTime(parser.getSongs(path)));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
