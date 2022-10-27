package net.leonardo_dgs.interactivebooks.command.command;

import cloud.commandframework.Command;
import net.leonardo_dgs.interactivebooks.IBook;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.AbstractCommand;
import net.leonardo_dgs.interactivebooks.command.IBooksCommands;
import net.leonardo_dgs.interactivebooks.command.argument.IBookArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandGet extends AbstractCommand {

    public CommandGet(InteractiveBooks plugin, IBooksCommands manager) {
        super(plugin, manager);
    }

    @Override
    public void register() {
        Command<CommandSender> getBookCommand = manager.commandBuilder("ibooks")
                .literal("get")
                .argument(IBookArgument.of("book"))
                .senderType(Player.class)
                .handler(context -> {
                    Player sender = (Player) context.getSender();
                    IBook book = context.get("book");
                    book.getItem(sender);
                    sender.sendMessage("§a获得书籍: §6%book_id%§a.".replace("%book_id%", book.getId()));
                })
                .build();
        manager.register(List.of(getBookCommand));
    }

}
