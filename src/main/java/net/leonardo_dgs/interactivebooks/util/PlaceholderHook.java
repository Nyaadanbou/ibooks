package net.leonardo_dgs.interactivebooks.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderHook {
    public static String setPlaceholders(String text) {
        return setPlaceholders(null, text);
    }

    public static String setPlaceholders(@Nullable CommandSender sender, @NotNull String text) {
        if (isPlaceholderAPISupported()) {
            if (sender instanceof OfflinePlayer) {
                return PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, text);
            } else {
                return PlaceholderAPI.setPlaceholders(null, text);
            }
        }
        return text;
    }

    private static boolean isPlaceholderAPISupported() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    private PlaceholderHook() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
