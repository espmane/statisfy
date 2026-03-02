package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataParser {
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<Song>>() {
    }.getType();

        public List<Song> getSongs(String folderPath) throws IOException {
        var folder = Path.of(folderPath);
        List<Song> songs = new ArrayList<>();
        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Not a directory: " + folderPath);
        }

        try (var paths = Files.list(folder)) {
            paths.filter(path ->
                            Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            List<Song> parsed = GSON.fromJson(Files.readString(path), LIST_TYPE);
                            if (parsed != null) songs.addAll(parsed);
                        } catch (IOException e) {
                            throw new UncheckedIOException("Failed to read file: " + path.getFileName(), e);
                        }
                    });
        }
        return songs;
    }
}

