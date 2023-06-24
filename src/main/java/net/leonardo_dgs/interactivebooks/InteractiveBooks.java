package net.leonardo_dgs.interactivebooks;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.leonardo_dgs.interactivebooks.command.IBooksCommands;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class InteractiveBooks extends JavaPlugin {
    private static InteractiveBooks INSTANCE;
    private static Map<String, IBook> BOOKS;

    private BookUpdater updater;

    /**
     * Gets the instance of this plugin.
     *
     * @return an instance of the plugin
     */
    public static @NotNull InteractiveBooks getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the registered books.
     *
     * @return a {@link Map} with book ids as keys and the registered books ({@link IBook}) as values
     */
    public static @NotNull Map<String, IBook> getBooks() {
        return Collections.unmodifiableMap(BOOKS);
    }

    /**
     * Gets an {@link IBook} by its id.
     *
     * @param id the id of the book to get
     * @return the book with the specified id if it's registered, or null if not found
     * @see #registerBook(IBook)
     */
    public static @Nullable IBook getBook(@NotNull String id) {
        return BOOKS.get(id);
    }

    /**
     * Gets an {@link IBook} by an itemStack.
     *
     * @param itemStack the itemStack of the book to get
     * @return the book with the specified id if it's registered, or null if not found
     * @see #registerBook(IBook)
     */
    public static @Nullable IBook getBook(@NotNull ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        if (!nbtItem.hasTag(Constants.BOOK_ID_KEY)) return null;
        return InteractiveBooks.getBook(nbtItem.getString(Constants.BOOK_ID_KEY));
    }

    /**
     * Registers a book.
     *
     * @param book the book id to register
     */
    public static void registerBook(@NotNull IBook book) {
        BOOKS.put(book.getId(), book);
    }

    /**
     * Unregisters a book by its id.
     *
     * @param id the book id to unregister
     */
    public static void unregisterBook(@NotNull String id) {
        IBook book = getBook(id);
        if (book != null) {
            BOOKS.remove(id);
        }
    }

    /**
     * Gets the book ID migrator.
     *
     * @return the book ID migrator
     */
    public static @NotNull BookUpdater getMigrator() {
        return INSTANCE.updater;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        BOOKS = new HashMap<>();

        // ---- Initialise item ID migrator ----
        updater = new BookUpdater();

        // ---- NBT-API stuff ----
        de.tr7zw.changeme.nbtapi.utils.MinecraftVersion.replaceLogger(getLogger());
        de.tr7zw.changeme.nbtapi.utils.MinecraftVersion.disableUpdateCheck();
        de.tr7zw.changeme.nbtapi.utils.MinecraftVersion.getVersion();

        // ---- Load all files ----
        ConfigManager.loadAll();

        // ---- Register listeners ----
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        // ---- Metrics ----
        new Metrics(this, 5483);

        // ---- Register commands ----
        try {
            new IBooksCommands(this);
        } catch (Exception e) {
            getLogger().severe("Failed to initialize commands!");
        }
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
    }
}
