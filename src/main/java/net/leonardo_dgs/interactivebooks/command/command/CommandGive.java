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

public class CommandGive extends AbstractCommand {

    public CommandGive(InteractiveBooks plugin, IBooksCommands manager) {
        super(plugin, manager);
    }

    @Override
    public void register() {
        Command<CommandSender> giveBookCommand = manager.commandBuilder("ibooks")
                .literal("give")
                .permission("interactivebooks.command.give")
                .argument(IBookArgument.of("book"))
                .argument(PlayerArgument.of("player"))
                .handler(context -> {
                    CommandSender sender = context.getSender();
                    IBook book = context.get("book");
                    Player player = context.get("player");
                    sender.sendMessage("§a已给予玩家 §6%player%§a 书籍: §6%book_id%§a."
                            .replace("%book_id%", book.getId())
                            .replace("%player%", player.getName())
                    );
                    player.sendMessage("§a你收到了一本书: §6%book_id%§a.".replace("%book_id%", book.getId()));
                })
                .build();
        manager.register(List.of(giveBookCommand));
    }

}
