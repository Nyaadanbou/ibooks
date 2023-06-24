package net.leonardo_dgs.interactivebooks.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.jetbrains.annotations.NotNull;

public class BooksUtils {
    @Getter
    private static final boolean isBookGenerationSupported = MinecraftVersion.getRunningVersion().isAfterOrEqual(MinecraftVersion.parse("1.10"));

    public static Generation ofGeneration(String generation) {
        return generation == null ? Generation.ORIGINAL : Generation.valueOf(generation.toUpperCase());
    }

    public static Component parsePage(String page, Player player) {
        return MiniMessage.miniMessage().deserialize(parsePlaceholder(player, page));
    }

    @NotNull
    public static String parsePlaceholder(Player player, String s) {
        return PlaceholderHook.setPlaceholders(player, s);
    }

    public static Component asComponent(@NotNull String s) {
        return MiniMessage.miniMessage().deserialize(s);
    }
}
