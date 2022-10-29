package net.leonardo_dgs.interactivebooks.command.command;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import net.leonardo_dgs.interactivebooks.IBook;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.AbstractCommand;
import net.leonardo_dgs.interactivebooks.command.IBooksCommands;
import net.leonardo_dgs.interactivebooks.command.argument.IBookArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class CommandOpen extends AbstractCommand {

    public CommandOpen(InteractiveBooks plugin, IBooksCommands manager) {
        super(plugin, manager);
    }

    @Override
    public void register() {
        Command<CommandSender> openBookCommand = manager.commandBuilder("ibooks")
                .literal("open")
                .permission("interactivebooks.command.open")
                .argument(IBookArgument.of("book"))
                .argument(PlayerArgument.optional("player"))
                .handler(context -> {
                    CommandSender sender = context.getSender();
                    IBook book = context.get("book");
                    Optional<Player> player = context.getOptional("player");
                    if (player.isPresent()) {
                        book.open(player.get());
                        sender.sendMessage("§a强制让玩家 §6%player%§a 查看书籍: §6%book_id%§a."
                                .replace("%book_id%", book.getId())
                                .replace("%player%", player.get().getName())
                        );
                    } else if (sender instanceof Player self) { // Player argument not present and sender is of Player
                        book.open(self);
                    } else { // Player argument not present and sender is NOT of Player
                        sender.sendMessage("§c必须指定一个玩家名.");
                    }
                })
                .build();
        manager.register(List.of(openBookCommand));
    }

}
