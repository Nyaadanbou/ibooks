package net.leonardo_dgs.interactivebooks.command;

import cloud.commandframework.Command;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.command.CommandCreate;
import net.leonardo_dgs.interactivebooks.command.command.CommandGet;
import net.leonardo_dgs.interactivebooks.command.command.CommandGive;
import net.leonardo_dgs.interactivebooks.command.command.CommandList;
import net.leonardo_dgs.interactivebooks.command.command.CommandOpen;
import net.leonardo_dgs.interactivebooks.command.command.CommandReload;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class IBooksCommands extends PaperCommandManager<CommandSender> {
    public IBooksCommands(InteractiveBooks plugin) throws Exception {
        super(
            plugin,
            AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build(),
            Function.identity(),
            Function.identity()
        );

        // ---- Register Brigadier ----
        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            final @Nullable CloudBrigadierManager<CommandSender, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            plugin.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        // ---- Register Asynchronous Completion Listener ----
        if (hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            registerAsynchronousCompletions();
            plugin.getLogger().info("Successfully registered asynchronous command completion listener.");
        }

        // ---- Change default exception messages ----
        new MinecraftExceptionHandler<CommandSender>()
            .withDefaultHandlers()
            .apply(this, sender -> AudienceProvider.nativeAudience().apply(sender));

        // ---- Register all commands ----
        Stream.of(
            new CommandCreate(plugin, this),
            new CommandGet(plugin, this),
            new CommandGive(plugin, this),
            new CommandList(plugin, this),
            new CommandOpen(plugin, this),
            new CommandReload(plugin, this)
        ).forEach(AbstractCommand::register);
    }

    public void register(final List<Command<CommandSender>> commands) {
        commands.forEach(this::command);
    }
}
