package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DataParser {
    private static final Gson gson = new Gson();
    private static final Type songType = new TypeToken<List<Song>>() {}.getType();

    public List<Song> getSongs(String folderPath) throws IOException {
        var folder = new File(folderPath);
        List<Song> songs = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + folderPath);
        }

        File[] files = folder.listFiles((file -> file.isFile()));
        for (File file : files) {
            try {
                String content = Files.readString(file.toPath());
                List<Song> parsed = gson.fromJson(content, songType);
                if (parsed != null) {
                    songs.addAll(parsed);
                }
            } catch (IOException e) {
                throw new IOException("Failed to read file: " + file.getPath(), e);
            }
        }
        return songs;
    }
}

