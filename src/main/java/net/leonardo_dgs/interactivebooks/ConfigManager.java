package net.leonardo_dgs.interactivebooks;

import de.leonhard.storage.Config;
import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.internal.settings.ReloadSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigManager {
    @Getter
    private static Config config;
    @Getter
    private static Config updater;

    public static void loadAll() {
        config = SimplixBuilder
            .fromFile(new File(InteractiveBooks.getInstance().getDataFolder().getPath(), "config.yml"))
            .setReloadSettings(ReloadSettings.INTELLIGENT)
            .addInputStreamFromResource("config.yml")
            .createConfig();
        updater = SimplixBuilder
            .fromFile(new File(InteractiveBooks.getInstance().getDataFolder(), "updater.yml"))
            .setReloadSettings(ReloadSettings.INTELLIGENT)
            .addInputStreamFromResource("updater.yml")
            .createConfig();
        loadBookConfig();
        loadUpdaterConfig();
    }

    private static void loadBookConfig() {
        for (String key : new ArrayList<>(InteractiveBooks.getBooks().keySet()))
            InteractiveBooks.unregisterBook(key);

        File booksFolder = new File(InteractiveBooks.getInstance().getDataFolder(), "books");
        if (!booksFolder.exists()) {
            try {
                if (!booksFolder.mkdirs())
                    throw new IOException();
                InputStream input = Objects.requireNonNull(InteractiveBooks.getInstance().getResource("example_book.yml"));
                Path target = new File(booksFolder, "example_book.yml").toPath();
                Files.copy(input, target);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (File bookFile : Objects.requireNonNull(booksFolder.listFiles())) {
            if (bookFile.getName().endsWith(".yml")) {
                String bookId = bookFile.getName().substring(0, bookFile.getName().length() - 4);
                SimplixBuilder
                    .fromFile(bookFile)
                    .setReloadSettings(ReloadSettings.INTELLIGENT)
                    .reloadCallback(flatFile -> {
                        InteractiveBooks.unregisterBook(bookId);
                        InteractiveBooks.registerBook(new IBook(bookId, flatFile));
                    })
                    .createConfig();
            }
        }
    }

    private static void loadUpdaterConfig() {
        InteractiveBooks.getMigrator().clearEntry();
        List<Map<String, String>> updates = getUpdater().getListParameterized("updates");
        for (Map<String, String> entry : updates) {
            String oldBookId = entry.get("old");
            String newBookId = entry.get("new");
            InteractiveBooks.getMigrator().addEntry(oldBookId, newBookId);
        }
    }
}
