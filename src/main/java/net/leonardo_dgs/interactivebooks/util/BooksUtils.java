package net.leonardo_dgs.interactivebooks.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BooksUtils {
    @Getter
    private static final boolean isBookGenerationSupported = MinecraftVersion.getRunningVersion().isAfterOrEqual(MinecraftVersion.parse("1.10"));

    private BooksUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @Nullable Generation ofGeneration(String generation) {
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
}
