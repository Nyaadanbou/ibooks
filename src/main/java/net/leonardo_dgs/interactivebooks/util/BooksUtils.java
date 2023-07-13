package net.leonardo_dgs.interactivebooks.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.leonardo_dgs.interactivebooks.Constants;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BooksUtils {
    private static final NamespacedKey KEY = new NamespacedKey("ibooks", Constants.BOOK_ID_KEY);

    public static @Nullable Generation ofGeneration(@Nullable String generation) {
        if (generation == null) {
            return null;
        }

        return switch (generation.toUpperCase()) {
            case "ORIGINAL" -> Generation.ORIGINAL;
            case "COPY_OF_ORIGINAL" -> Generation.COPY_OF_ORIGINAL;
            case "COPY_OF_COPY" -> Generation.COPY_OF_COPY;
            case "TATTERED" -> Generation.TATTERED;
            default -> null;
        };
    }

    public static @NotNull Component parsePage(@NotNull String page, @NotNull Player player) {
        return MiniMessage.miniMessage().deserialize(parsePlaceholder(player, page));
    }

    public static @NotNull String parsePlaceholder(@NotNull Player player, @NotNull String s) {
        return PlaceholderHook.setPlaceholders(player, s);
    }

    public static @NotNull Component asComponent(@NotNull String s) {
        return MiniMessage.miniMessage().deserialize(s);
    }

    public static @Nullable String getBookId(@NotNull ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return null;

        String bookId = null;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(KEY, PersistentDataType.STRING)) {
            bookId = container.get(KEY, PersistentDataType.STRING);
        }
        return bookId;
    }

    public static @Nullable ItemStack setBookId(@NotNull ItemStack item, @NotNull String bookId) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(KEY, PersistentDataType.STRING, bookId);
        item.setItemMeta(itemMeta);
        return item;
    }

    private BooksUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
