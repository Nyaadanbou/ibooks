package net.leonardo_dgs.interactivebooks;

import de.tr7zw.changeme.nbtapi.NBTItem;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Used to update the internal ID stored in book items.
 * <p>
 * Known as remorse medicine :)
 */
public class BookIdentityUpdater {
    private final Map<String, String> updateEntries;

    public BookIdentityUpdater() {
        updateEntries = new HashMap<>();
    }

    public void addEntry(String oldBookId, String newBookId) {
        updateEntries.put(oldBookId.toLowerCase(Locale.ROOT), newBookId);
    }

    public void removeEntry(String oldBookId) {
        updateEntries.remove(oldBookId.toLowerCase(Locale.ROOT));
    }

    public void clearEntry() {
        updateEntries.clear();
    }

    public void update(NBTItem bookItem) {
        String oldBookId = bookItem.getString(Constants.BOOK_ID_KEY);
        String newBookId = updateEntries.get(oldBookId.toLowerCase(Locale.ROOT));
        bookItem.setString(Constants.BOOK_ID_KEY, newBookId);
    }

    public boolean shouldUpdate(NBTItem bookItem) {
        String oldBookId = bookItem.getString(Constants.BOOK_ID_KEY);
        return updateEntries.containsKey(oldBookId.toLowerCase(Locale.ROOT));
    }
}
