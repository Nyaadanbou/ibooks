package net.leonardo_dgs.interactivebooks.command.command;

import cloud.commandframework.Command;
import net.leonardo_dgs.interactivebooks.ConfigManager;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import net.leonardo_dgs.interactivebooks.command.AbstractCommand;
import net.leonardo_dgs.interactivebooks.command.IBooksCommands;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandReload extends AbstractCommand {

    public CommandReload(InteractiveBooks plugin, IBooksCommands manager) {
        super(plugin, manager);
    }

    @Override
    public void register() {
        Command<CommandSender> reloadCommand = manager.commandBuilder("ibooks")
                .literal("reload")
                .permission("interactivebooks.command.reload")
                .handler(context -> {
                    CommandSender sender = context.getSender();
                    ConfigManager.loadAll();
                    sender.sendMessage("§a配置文件已重新载入!");
                })
                .build();
        manager.register(List.of(reloadCommand));
    }

}
