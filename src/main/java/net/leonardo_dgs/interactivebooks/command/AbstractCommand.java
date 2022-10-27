package net.leonardo_dgs.interactivebooks.command;

import net.leonardo_dgs.interactivebooks.InteractiveBooks;

public abstract class AbstractCommand {

    protected final InteractiveBooks plugin;
    protected final IBooksCommands manager;

    public AbstractCommand(InteractiveBooks plugin, IBooksCommands manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    abstract public void register();

}

