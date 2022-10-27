package net.leonardo_dgs.interactivebooks.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.ComponentMessageThrowable;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.command.CommandCreate;
import net.leonardo_dgs.interactivebooks.command.command.CommandGet;
import net.leonardo_dgs.interactivebooks.command.command.CommandGive;
import net.leonardo_dgs.interactivebooks.command.command.CommandList;
import net.leonardo_dgs.interactivebooks.command.command.CommandOpen;
import net.leonardo_dgs.interactivebooks.command.command.CommandReload;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class IBooksCommands extends PaperCommandManager<CommandSender> {

    public static final CloudKey<InteractiveBooks> PLUGIN = SimpleCloudKey.of("gemseconomy:plugin", TypeToken.get(InteractiveBooks.class));
    private static final Component NULL = Component.text("null");
    private final Map<String, CommandFlag.Builder<?>> flagRegistry = new HashMap<>();

    public IBooksCommands(InteractiveBooks plugin) throws Exception {
        super(
                plugin,
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build(),
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

        // ---- Inject instances into the command context ----
        this.registerCommandPreProcessor(ctx -> ctx.getCommandContext().store(PLUGIN, plugin));

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

    public CommandFlag.Builder<?> getFlag(final String name) {
        return flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<?> flagBuilder) {
        flagRegistry.put(name, flagBuilder);
    }

    public void register(final List<Command<CommandSender>> commands) {
        commands.forEach(this::command);
    }

}
