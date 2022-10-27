package net.leonardo_dgs.interactivebooks.command.command;

import cloud.commandframework.Command;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.AbstractCommand;
import net.leonardo_dgs.interactivebooks.command.IBooksCommands;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.List;

public class CommandList extends AbstractCommand {

    public CommandList(InteractiveBooks plugin, IBooksCommands manager) {
        super(plugin, manager);
    }

    @Override
    public void register() {
        Command<CommandSender> listAllBooksCommand = manager.commandBuilder("ibooks")
                .literal("list")
                .handler(context -> {
                    CommandSender sender = context.getSender();
                    StringBuilder sb = new StringBuilder();
                    Iterator<String> iterator = InteractiveBooks.getBooks().keySet().iterator();
                    boolean hasNext = iterator.hasNext();
                    while (hasNext) {
                        String bookId = iterator.next();
                        sb.append("§6");
                        sb.append(bookId);
                        hasNext = iterator.hasNext();
                        if (hasNext)
                            sb.append("§7, ");
                    }
                    sender.sendMessage("§e所有书籍: " + sb);
                })
                .build();
        manager.register(List.of(listAllBooksCommand));
    }

}
