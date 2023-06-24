package net.leonardo_dgs.interactivebooks.command.command;

import cloud.commandframework.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.AbstractCommand;
import net.leonardo_dgs.interactivebooks.command.IBooksCommands;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandList extends AbstractCommand {
    public CommandList(InteractiveBooks plugin, IBooksCommands manager) {
        super(plugin, manager);
    }

    @Override
    public void register() {
        Command<CommandSender> listBookCommand = manager.commandBuilder("ibooks")
            .literal("list")
            .permission("interactivebooks.command.list")
            .handler(context -> {
                CommandSender sender = context.getSender();
                Component join = Component.join(
                    JoinConfiguration.commas(true),
                    InteractiveBooks.getBooks().keySet().stream().map(b -> Component.text(b).color(NamedTextColor.GOLD)).toList()
                );
                sender.sendMessage(Component.text("所有书籍: ").color(NamedTextColor.YELLOW).append(join));
            })
            .build();
        manager.register(List.of(listBookCommand));
    }
}
