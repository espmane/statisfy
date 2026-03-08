package org.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataParser {
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<Song>>() {}.getType();

    public List<Song> getSongs(String folderPath) throws IOException {
        var folder = new File(folderPath);
        List<Song> songs = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + folderPath);
        }

        File[] files = folder.listFiles((file -> file.isFile() && file.getName().endsWith(".json")));
        for (File file : files) {
            try {
                String content = Files.readString(file.toPath());
                List<Song> parsed = GSON.fromJson(content, LIST_TYPE);
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
