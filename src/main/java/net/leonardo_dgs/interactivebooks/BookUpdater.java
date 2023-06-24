package net.leonardo_dgs.interactivebooks;

import de.tr7zw.changeme.nbtapi.NBTItem;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Used to update the internal ID stored in book items.
 * <p>
 * Known as Remorse medicine :)
 */
public class BookUpdater {
    private final Map<String, String> migrationEntries;

    public BookUpdater() {
        migrationEntries = new HashMap<>();
    }

    public void addEntry(String oldBookId, String newBookId) {
        migrationEntries.put(oldBookId.toLowerCase(Locale.ROOT), newBookId);
    }

    public void removeEntry(String oldBookId) {
        migrationEntries.remove(oldBookId.toLowerCase(Locale.ROOT));
    }

    public void clearEntry() {
        migrationEntries.clear();
    }

    public void migrate(NBTItem bookItem) {
        String oldBookId = bookItem.getString(Constants.BOOK_ID_KEY);
        String newBookId = migrationEntries.get(oldBookId.toLowerCase(Locale.ROOT));
        bookItem.setString(Constants.BOOK_ID_KEY, newBookId);
    }

    public boolean shouldMigrate(NBTItem bookItem) {
        String oldBookId = bookItem.getString(Constants.BOOK_ID_KEY);
        return migrationEntries.containsKey(oldBookId.toLowerCase(Locale.ROOT));
    }
}
