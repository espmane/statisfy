package org.example;

import java.io.File;
import java.util.List;

public class GuiTest {
    final DataParser parser = new DataParser();
    File folderPath;
    List<Song> songs;

    void main() {
        GUI gui = new GUI();



        folderPath = gui.folder;
        try {
            songs = parser.getSongs(folderPath.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Song song : songs) {
            System.out.println(song);
        }
    }


}