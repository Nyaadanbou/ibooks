package net.leonardo_dgs.interactivebooks;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.leonardo_dgs.interactivebooks.util.PAPIUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class CommandIBooksNew {

    private static final ArgumentSuggestions IBOOKS;

    static {
        IBOOKS = ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() ->
                InteractiveBooks.getBooks().keySet().stream()
                        .filter(book -> book.startsWith(info.currentArg()))
                        .map(book -> "'" + book + "'")
                        .toArray(String[]::new))
        );
    }

    public CommandIBooksNew() {
        new CommandAPICommand("ibooks")
//                .withAliases("interactivebooks", "ibooks", "ib")
                .withPermission("interactivebooks.command")
                .withSubcommand(new CommandAPICommand("list")
                        .withPermission("interactivebooks.command.list")
                        .executes(((sender, args) -> {
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
                            sender.sendMessage("§e书籍:\n" + sb);
                        })))
                .withSubcommand(new CommandAPICommand("open")
                        .withArguments(new TextArgument("book").replaceSuggestions(IBOOKS))
                        .executesPlayer((sender, args) -> {
                            String bookIdToOpen = PAPIUtil.setPlaceholders(sender, (String) args[0]);
                            if (!sender.hasPermission("interactivebooks.command.open") && !sender.hasPermission("interactivebooks.open." + bookIdToOpen)) {
                                sender.sendMessage("§c你没有使用该指令的权限");
                                return;
                            }
                            if (InteractiveBooks.getBook(bookIdToOpen) == null) {
                                sender.sendMessage("§c你要查看的书籍不存在.");
                                return;
                            }
                            InteractiveBooks.getBook(bookIdToOpen).open(sender);
                        }))
                .withSubcommand(new CommandAPICommand("open")
                        .withArguments(new TextArgument("book").replaceSuggestions(IBOOKS))
                        .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                        .executesConsole((sender, args) -> {
                            Player playerToOpen = (Player) args[1];
                            String bookIdToOpen = PAPIUtil.setPlaceholders(playerToOpen, (String) args[0]);
                            if (InteractiveBooks.getBook(bookIdToOpen) == null) {
                                sender.sendMessage("§c你要查看的书籍不存在.");
                                return;
                            }
                            InteractiveBooks.getBook(bookIdToOpen).open(playerToOpen);
                            sender.sendMessage("§a已给玩家 §6%player%§a 查看书籍 §6%book_id%§a.".replace("%book_id%", bookIdToOpen).replace("%player%", playerToOpen.getName()));
                        }))
                .withSubcommand(new CommandAPICommand("get")
                        .withPermission("interactivebooks.command.get")
                        .withArguments(new TextArgument("book").replaceSuggestions(IBOOKS))
                        .executesPlayer((player, args) -> {
                            String bookIdToGet = PAPIUtil.setPlaceholders(player, (String) args[0]);
                            if (InteractiveBooks.getBook(bookIdToGet) == null) {
                                player.sendMessage("§c你要获取的书籍不存在.");
                                return;
                            }
                            player.getInventory().addItem(InteractiveBooks.getBook(bookIdToGet).getItem(player));
                            player.sendMessage("§a你收到了一本书: §6%book_id%§a.".replace("%book_id%", bookIdToGet));
                        }))
                .withSubcommand(new CommandAPICommand("give")
                        .withPermission("interactivebooks.command.give")
                        .withArguments(new TextArgument("book").replaceSuggestions(IBOOKS))
                        .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                        .executes((sender, args) -> {
                            Player targetPlayer = (Player) args[1];
                            String targetBookId = PAPIUtil.setPlaceholders(targetPlayer, (String) args[0]);
                            if (InteractiveBooks.getBook(targetBookId) == null) {
                                sender.sendMessage("§c你要给予的书籍不存在.");
                                return;
                            }
                            targetPlayer.getInventory().addItem(InteractiveBooks.getBook(targetBookId).getItem(targetPlayer));
                            sender.sendMessage("§a已给予玩家 §6%player%§a 书籍 §6%book_id%§a.".replace("%book_id%", targetBookId).replace("%player%", targetPlayer.getName()));
                            targetPlayer.sendMessage("§a你收到了一本书: §6%book_id%§a.".replace("%book_id%", targetBookId));
                        }))
                .withSubcommand(new CommandAPICommand("create")
                        .withPermission("interactivebooks.command.create")
                        .withArguments(new TextArgument("book-id"))
                        .withArguments(new TextArgument("name"))
                        .withArguments(new TextArgument("title"))
                        .withArguments(new TextArgument("author"))
                        .withArguments(new MultiLiteralArgument("ORIGINAL", "COPY_OF_ORIGINAL", "COPY_OF_COPY", "TATTERED"))
                        .executes((sender, args) -> {
                            if (InteractiveBooks.getBook((String) args[0]) != null) {
                                sender.sendMessage("§c创建失败, 书籍ID重复.");
                                return;
                            }
                            String bookId = (String) args[0];
                            String bookName = (String) args[1];
                            String bookTitle = (String) args[2];
                            String bookAuthor = (String) args[3];

                            IBook createdBook = new IBook(bookId, bookName, bookTitle, bookAuthor, (String) args[4], new ArrayList<>(), new ArrayList<>());
                            createdBook.save();
                            InteractiveBooks.registerBook(createdBook);
                            sender.sendMessage("§a已创建新书籍.");
                        }))
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("interactivebooks.command.reload")
                        .executes((sender, args) -> {
                            ConfigManager.loadAll();
                            sender.sendMessage("§a配置文件已重新载入!");
                        }))
                .register();
    }
}
