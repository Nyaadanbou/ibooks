package net.leonardo_dgs.interactivebooks;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.inventory.Book;
import net.leonardo_dgs.interactivebooks.util.BooksUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IBook {
    // ---- Internal ----
    private final String id;
    private FlatFile bookConfig;

    // ---- Item ---- (stored in MiniMessage format)
    private final String displayName;
    private final List<String> lore;

    // ---- BookMeta ---- (stored in MiniMessage format)
    private final String title;
    private final String author;
    private final BookMeta.Generation generation;
    private final List<String> pages;

    // ---- Extra ----
    private final Set<String> openCommands;

    /**
     * Constructor for {@link IBook} that takes data from the supplied configuration.
     *
     * @param id         the id of the book
     * @param bookConfig configuration from which to take data to crete the book
     */
    IBook(String id, FlatFile bookConfig) {
        this(
            id,
            bookConfig.getString("name"),
            bookConfig.getStringList("lore"),
            bookConfig.getString("title"),
            bookConfig.getString("author"),
            BooksUtils.ofGeneration(bookConfig.getString("generation")),
            mergeLines(bookConfig.getSection("pages")),
            bookConfig.getStringList("open_command")
        );
        this.bookConfig = bookConfig;
    }

    /**
     * Constructor for {@link IBook} with the supplied data.
     *
     * @param id           the id of the book
     * @param displayName  the display name of the book item
     * @param lore         the lore of the book item
     * @param title        the title of the book item
     * @param author       the author of the book item
     * @param generation   the generation of the book item
     * @param pages        the pages that will be converted to the book item pages
     * @param openCommands the commands that will open the book
     */
    public IBook(
        @NotNull String id,
        @NotNull String displayName,
        @Nullable List<String> lore,
        @NotNull String title,
        @NotNull String author,
        @NotNull BookMeta.Generation generation,
        @Nullable List<String> pages,
        @Nullable List<String> openCommands
    ) {
        this.id = id;
        this.displayName = displayName;
        this.lore = new ArrayList<>();
        this.title = title;
        this.author = author;
        this.generation = generation;
        this.pages = new ArrayList<>();
        this.openCommands = new HashSet<>();

        // ---- Selectively add nullable params ----

        if (lore != null) this.lore.addAll(lore);
        if (pages != null) this.pages.addAll(pages);
        if (openCommands != null) {
            openCommands.stream().map(s -> s.toLowerCase(Locale.ROOT)).forEach(this.openCommands::add);
        }
    }

    /**
     * Constructor for {@link IBook} with the supplied data.
     *
     * @param id          the id of the book
     * @param displayName the display name of the book item
     * @param lore        the lore of the book item
     * @param title       the title of the book item
     * @param author      the author of the book item
     * @param generation  the generation of the book item
     * @param pages       the pages that will be converted to the book item pages
     */
    public IBook(
        @NotNull String id,
        @NotNull String displayName,
        @Nullable List<String> lore,
        @NotNull String title,
        @NotNull String author,
        @NotNull BookMeta.Generation generation,
        @Nullable List<String> pages
    ) {
        this(id, displayName, lore, title, author, generation, pages, null);
    }

    /**
     * Gets the id of the book.
     *
     * @return the book id.
     */
    public String getId() {
        return id;
    }

    /**
     * Opens the book to the specified player.
     *
     * @param player the player to which open the book
     */
    public void open(Player player) {
        if (bookConfig.hasChanged()) {
            bookConfig.forceReload();
            InteractiveBooks.getBook(id).open(player);
        } else {
            Book.Builder builder = Book.builder(); // Opening a book for a player only requires page content
            pages.forEach(p -> builder.addPage(BooksUtils.parsePage(p, player)));
            player.openBook(builder);
        }
    }

    /**
     * Gets the book item without replacing placeholders.
     *
     * @return the book item
     */
    public ItemStack getItem() {
        return this.getItem(null);
    }

    /**
     * Gets the book item replacing its placeholders with the specified player data.
     *
     * @param player the player to get the data from for replacing placeholders
     * @return the book item with placeholders replaced with the specified player data
     */
    public ItemStack getItem(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        updateBookMeta(book, player);
        NBTItem nbtItem = new NBTItem(book);
        nbtItem.setString(Constants.BOOK_ID_KEY, getId());
        return nbtItem.getItem();
    }

    /**
     * Gets the {@link BookMeta} replacing its placeholders with the specified player data.
     *
     * @param player the player to get the data from for replacing placeholders
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void updateBookMeta(ItemStack book, Player player) {
        if (bookConfig.hasChanged()) {
            bookConfig.forceReload();
            InteractiveBooks.getBook(id).updateBookMeta(book, player);
        } else {
            BookMeta bookMeta = (BookMeta) book.getItemMeta();

            // ---- Set placeholders for displayName, title, author, lore ----
            Optional.ofNullable(this.displayName)
                .map(s -> BooksUtils.asComponent(BooksUtils.parsePlaceholder(player, s)))
                .ifPresent(bookMeta::displayName);
            Optional.ofNullable(this.title)
                .map(s -> BooksUtils.asComponent(BooksUtils.parsePlaceholder(player, s)))
                .ifPresent(bookMeta::title);
            Optional.ofNullable(this.author)
                .map(s -> BooksUtils.asComponent(BooksUtils.parsePlaceholder(player, s)))
                .ifPresent(bookMeta::author);
            bookMeta.lore(this.lore.stream()
                .map(s -> BooksUtils.asComponent(BooksUtils.parsePlaceholder(player, s)))
                .toList());
            bookMeta.setGeneration(generation);

            // ---- Set placeholders for pages ----
            pages.forEach(page -> bookMeta.addPages(BooksUtils.parsePage(page, player)));

            book.setItemMeta(bookMeta);
        }
    }

    /**
     * Gets the list of pages of this book.
     *
     * @return the {@link List} containing all pages of this book
     */
    public List<String> getPages() {
        return pages;
    }

    /**
     * Gets the commands that can be used to open this book.
     *
     * @return a {@link Set} containing the commands that can be used to open this book
     */
    public Set<String> getOpenCommands() {
        return openCommands;
    }

    /**
     * Saves this book to his config file.
     */
    public void save() {
        File file = new File(new File(InteractiveBooks.getInstance().getDataFolder(), "books"), getId() + ".yml");
        try {
            file.createNewFile();
            YamlConfiguration bookConfig = YamlConfiguration.loadConfiguration(file);
            bookConfig.set("name", displayName);
            bookConfig.set("title", title);
            bookConfig.set("author", author);
            if (BooksUtils.isBookGenerationSupported())
                bookConfig.set("generation", Optional.ofNullable(generation).orElse(Generation.ORIGINAL).name());
            bookConfig.set("lore", lore);
            bookConfig.set("open_command", String.join(" ", getOpenCommands()));
            if (getPages().isEmpty()) {
                List<String> tempPages = new ArrayList<>();
                tempPages.add("");
                bookConfig.set("pages.1", tempPages);
            }
            for (int i = 0; i < getPages().size(); i++)
                bookConfig.set("pages." + (i + 1), getPages().get(i).split("\n"));
            bookConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!getClass().equals(obj.getClass()))
            return false;
        IBook other = (IBook) obj;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private static List<String> mergeLines(FlatFileSection section) {
        List<String> pages = new ArrayList<>();
        if (section != null) {
            section.singleLayerKeySet().forEach(key -> {
                StringBuilder sb = new StringBuilder();
                section.getStringList(key).forEach(line -> sb.append("\n").append(line == null ? "" : line));
                pages.add(sb.toString().replaceFirst("\n", ""));
            });
        }
        return pages;
    }
}
