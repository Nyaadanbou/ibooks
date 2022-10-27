package net.leonardo_dgs.interactivebooks.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class BooksUtils {

    @Getter
    private static final boolean isBookGenerationSupported = MinecraftVersion.getRunningVersion().isAfterOrEqual(MinecraftVersion.parse("1.10"));

    public static Component getPage(String page, Player player) {
        return MiniMessage.miniMessage().deserialize(parsePlaceholder(player, page));
    }

    public static Generation ofGeneration(String generation) {
        return generation == null ? Generation.ORIGINAL : Generation.valueOf(generation.toUpperCase());
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public static BookMeta parseBookMeta(BookMeta meta, List<String> rawPages, Player player) {
        BookMeta bookMeta = meta.clone();

        // ---- Set placeholders for displayName, title, author, lore ----
        if (bookMeta.hasDisplayName()) {
            Component displayName = Optional.of(bookMeta.getDisplayName())
                    .map(s -> parsePlaceholder(player, s))
                    .map(BooksUtils::asComponent)
                    .get();
            bookMeta.displayName(displayName);
        }
        if (bookMeta.hasTitle()) {
            Component title = Optional.of(bookMeta.getTitle())
                    .map(s -> parsePlaceholder(player, s))
                    .map(BooksUtils::asComponent)
                    .get();
            bookMeta.title(title);
        }
        if (bookMeta.hasAuthor()) {
            Component author = Optional.of(bookMeta.getAuthor())
                    .map(s -> parsePlaceholder(player, s))
                    .map(BooksUtils::asComponent)
                    .get();
            bookMeta.author(author);
        }
        if (bookMeta.hasLore()) {
            List<Component> lore = bookMeta.getLore().stream()
                    .map(s -> parsePlaceholder(player, s))
                    .map(BooksUtils::asComponent)
                    .toList();
            bookMeta.lore(lore);
        }

        // ---- Set placeholders for pages ----
        rawPages.forEach(page -> bookMeta.addPages(getPage(page, player)));

        return bookMeta;
    }

    @NotNull
    private static String parsePlaceholder(Player player, String s) {
        return PAPIUtil.setPlaceholders(player, s);
    }

    private static Component asComponent(@NotNull String s) {
        return MiniMessage.miniMessage().deserialize(s);
    }

}
