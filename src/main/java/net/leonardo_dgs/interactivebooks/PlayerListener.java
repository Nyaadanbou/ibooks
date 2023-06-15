package net.leonardo_dgs.interactivebooks;

import net.leonardo_dgs.interactivebooks.util.BooksUtils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public final class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String openBookId;
        List<String> booksToGiveIds;
        if (event.getPlayer().hasPlayedBefore()) {
            openBookId = Settings.getConfig().getString("open_book_on_join");
            booksToGiveIds = Settings.getConfig().getStringList("books_on_join");
        } else {
            openBookId = Settings.getConfig().getString("open_book_on_first_join");
            booksToGiveIds = Settings.getConfig().getStringList("books_on_first_join");
        }
        if (openBookId != null && !openBookId.equals("")) {
            IBook book = InteractiveBooks.getBook(openBookId);
            if (book != null) {
                book.open(event.getPlayer());
            }
        }

        booksToGiveIds.forEach(id -> {
            IBook book = InteractiveBooks.getBook(id);
            if (book != null) {
                event.getPlayer().getInventory().addItem(book.getItem(event.getPlayer()));
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY)
            return;
        if (!event.getAction().isRightClick())
            return;

        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();

        if (itemInMainHand.getType() != Material.WRITTEN_BOOK)
            return;
        if (!Settings.getConfig().getBoolean("update_books_on_use"))
            return;

        String bookId = BooksUtils.getBookId(itemInMainHand);
        if (bookId == null)
            return;

        if (InteractiveBooks.getInstance().getUpdater().shouldUpdate(itemInMainHand)) {
            InteractiveBooks.getInstance().getUpdater().update(itemInMainHand);
        }

        IBook iBook = InteractiveBooks.getBook(bookId);
        if (iBook == null)
            return;

        ItemStack bookItem = iBook.getItem(event.getPlayer());

        bookItem.editMeta(BookMeta.class, meta -> {
            // preserve generation for old books
            BookMeta oldBookMeta = (BookMeta) itemInMainHand.getItemMeta();
            meta.setGeneration(oldBookMeta.getGeneration());
        });

        bookItem.setAmount(itemInMainHand.getAmount());
        event.getPlayer().getInventory().setItemInMainHand(bookItem);
    }
}
