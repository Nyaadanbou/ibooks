package net.leonardo_dgs.interactivebooks.command.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import net.leonardo_dgs.interactivebooks.IBook;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.AbstractCommand;
import net.leonardo_dgs.interactivebooks.command.IBooksCommands;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class CommandCreate extends AbstractCommand {
    public CommandCreate(InteractiveBooks plugin, IBooksCommands manager) {
        super(plugin, manager);
    }

    @Override
    public void register() {
        Command<CommandSender> createBookCommand = manager.commandBuilder("ibooks")
            .literal("create")
            .permission("interactivebooks.command.create")
            .argument(StringArgument.of("book-id"))
            .argument(StringArgument.of("name"))
            .argument(StringArgument.of("title"))
            .argument(StringArgument.of("author"))
            .argument(EnumArgument.of(BookMeta.Generation.class, "generation"))
            .handler(context -> {
                CommandSender sender = context.getSender();
                String bookId = context.get("book-id");
                if (InteractiveBooks.getBook(bookId) != null) {
                    sender.sendMessage("§c创建失败! 指定的书籍ID已存在.");
                    return;
                }
                String name = context.get("name");
                String title = context.get("title");
                String author = context.get("author");
                BookMeta.Generation generation = context.get("generation");

                IBook createdBook = new IBook(bookId, name, null, title, author, generation, null);
                createdBook.save();
                InteractiveBooks.registerBook(createdBook);
                sender.sendMessage("§a已创建新书籍!");
            })
            .build();
        manager.register(List.of(createBookCommand));
    }
}
