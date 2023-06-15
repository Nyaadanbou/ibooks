package net.leonardo_dgs.interactivebooks;

import net.leonardo_dgs.interactivebooks.util.BooksUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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

    public void update(ItemStack bookItem) {
        String oldBookId = Objects.requireNonNull(BooksUtils.getBookId(bookItem));
        String newBookId = updateEntries.get(oldBookId.toLowerCase(Locale.ROOT));
        BooksUtils.setBookId(bookItem, newBookId);
    }

    public boolean shouldUpdate(ItemStack bookItem) {
        String oldBookId = Objects.requireNonNull(BooksUtils.getBookId(bookItem));
        return updateEntries.containsKey(oldBookId.toLowerCase(Locale.ROOT));
    }
}
