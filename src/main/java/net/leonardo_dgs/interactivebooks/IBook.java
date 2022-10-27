package net.leonardo_dgs.interactivebooks;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.leonardo_dgs.interactivebooks.util.BooksUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class IBook {

    private static final String bookIdKey = "InteractiveBooks|Book-Id";

    private final String id;
    private BookMeta bookMeta;
    private List<String> pages;

    private final Set<String> openCommands = new HashSet<>();

    private FlatFile bookConfig;

    private Integer hashCode;

    /**
     * Constructor for {@link IBook} that takes data from the supplied configuration.
     *
     * @param id         the id of the book
     * @param bookConfig configuration from which to take data to crete the book
     */
    IBook(String id, FlatFile bookConfig) {
        this(id,
                bookConfig.getString("name"),
                bookConfig.getString("title"),
                bookConfig.getString("author"),
                bookConfig.getString("generation"),
                bookConfig.getStringList("lore"),
                mergeLines(bookConfig.getSection("pages")),
                bookConfig.getString("open_command") == null || bookConfig.getString("open_command").equals("") ? null : bookConfig.getString("open_command").split(" ")
        );
        this.bookConfig = bookConfig;
    }

    /**
     * Constructor for {@link IBook} with the supplied data.
     *
     * @param id           the id of the book
     * @param displayName  the display name of the book item
     * @param title        the title of the book item
     * @param author       the author of the book item
     * @param lore         the lore of the book item
     * @param pages        the pages that will be converted to the book item pages
     * @param openCommands the commands that will open the book
     */
    public IBook(String id, String displayName, String title, String author, List<String> lore, List<String> pages, String... openCommands) {
        this.id = id;
        BookMeta bookMeta = (BookMeta) new ItemStack(Material.WRITTEN_BOOK).getItemMeta();
        if (lore == null)
            lore = new ArrayList<>();
        if (pages == null)
            pages = new ArrayList<>();
        Objects.requireNonNull(bookMeta).setDisplayName(displayName);
        bookMeta.setTitle(title);
        bookMeta.setAuthor(author);
        bookMeta.setLore(lore);
        this.bookMeta = bookMeta;
        this.pages = pages;
        if (openCommands != null)
            for (String command : openCommands)
                this.openCommands.add(command.toLowerCase());
    }

    /**
     * Constructor for {@link IBook} with the supplied data.
     *
     * @param id           the id of the book
     * @param displayName  the display name of the book item
     * @param title        the title of the book item
     * @param author       the author of the book item
     * @param generation   a string that represents generation of the book item
     * @param lore         the lore of the book item
     * @param pages        the pages that will be converted to the book item pages
     * @param openCommands the commands that will open the book
     */
    public IBook(String id, String displayName, String title, String author, String generation, List<String> lore, List<String> pages, String... openCommands) {
        this(id, displayName, title, author, lore, pages, openCommands);
        if (generation != null && BooksUtils.isBookGenerationSupported())
            bookMeta.setGeneration(BooksUtils.ofGeneration(generation));
    }

    /**
     * Constructor for {@link IBook} with the supplied data.
     *
     * @param id           the id of the book
     * @param displayName  the display name of the book item
     * @param title        the title of the book item
     * @param author       the author of the book item
     * @param generation   the generation of the book item
     * @param lore         the lore of the book item
     * @param pages        the pages that will be converted to the book item pages
     * @param openCommands the commands that will open the book
     */
    public IBook(String id, String displayName, String title, String author, Generation generation, List<String> lore, List<String> pages, String... openCommands) {
        this(id, displayName, title, author, lore, pages, openCommands);
        if (generation != null)
            bookMeta.setGeneration(generation);
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
            player.openBook(getBookMeta(player));
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
        book.setItemMeta(getBookMeta(player));
        NBTItem nbtItem = new NBTItem(book);
        nbtItem.setString(bookIdKey, getId());
        return nbtItem.getItem();
    }

    /**
     * Gets the {@link BookMeta} without replacing placeholders.
     *
     * @return the {@link BookMeta} of the book
     */
    public BookMeta getBookMeta() {
        return this.getBookMeta(null);
    }

    /**
     * Gets the {@link BookMeta} replacing its placeholders with the specified player data.
     *
     * @param player the player to get the data from for replacing placeholders
     * @return the {@link BookMeta} with placeholders replaced with the specified player data
     */
    public BookMeta getBookMeta(Player player) {
        if (bookConfig.hasChanged()) {
            bookConfig.forceReload();
            return InteractiveBooks.getBook(id).getBookMeta(player);
        } else {
            return BooksUtils.parseBookMeta(bookMeta, getPages(), player);
        }
    }

    /**
     * Sets the {@link BookMeta} of this book to the specified one.
     *
     * @param bookMeta the {@link BookMeta} to set
     */
    public void setBookMeta(BookMeta bookMeta) {
        this.bookMeta = bookMeta;
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
     * Sets the book pages to the specified ones.
     *
     * @param pages the {@link List} of the book pages to set.
     */
    public void setPages(List<String> pages) {
        this.pages = pages;
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
            bookConfig.set("name", MiniMessage.miniMessage().serialize(bookMeta.displayName()));
            bookConfig.set("title", MiniMessage.miniMessage().serialize(bookMeta.title()));
            bookConfig.set("author", MiniMessage.miniMessage().serialize(bookMeta.author()));
            if (BooksUtils.isBookGenerationSupported())
                bookConfig.set("generation", Optional.ofNullable(bookMeta.getGeneration()).orElse(Generation.ORIGINAL).name());
            bookConfig.set("lore", bookMeta.lore().stream().map(c -> MiniMessage.miniMessage().serialize(c)).toList());
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
        if (hashCode == null)
            hashCode = getId().hashCode();
        return hashCode;
    }

    private static List<String> mergeLines(FlatFileSection section) {
        List<String> pages = new ArrayList<>();
        if (section != null) {
            section.singleLayerKeySet().forEach(key -> {
                StringBuilder sb = new StringBuilder();
                section.getStringList(key).forEach(line -> sb.append("\n").append(line));
                pages.add(sb.toString().replaceFirst("\n", ""));
            });
        }
        return pages;
    }

}
