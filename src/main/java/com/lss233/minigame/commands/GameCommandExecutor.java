package com.lss233.minigame.commands;

import com.lss233.minigame.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class GameCommandExecutor implements CommandExecutor {
    protected final Plugin plugin;
    protected final String label;
    protected final CommandExecutor defaultExecutor;
    public GameCommandExecutor(Plugin plugin, String label){
        this.plugin = plugin;
        this.label = label;
        this.defaultExecutor = plugin.getJavaPlugin().getCommand(label).getExecutor();
        this.plugin.getJavaPlugin().getCommand(label).setExecutor(this);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(defaultExecutor != null && defaultExecutor != this && defaultExecutor instanceof GameCommandExecutor){
            return defaultExecutor.onCommand(sender, command, label, args);
        } else {
            commandNotFound(sender);
        }
        return true;
    }

    protected abstract void commandNotFound(CommandSender sender);

    protected String getPrefix(){
        return "/" + this.label;
    }
    protected abstract List<String> menu();
    protected boolean help(CommandSender sender){
        menu().forEach(sender::sendMessage);
        if(defaultExecutor != null && defaultExecutor != this && defaultExecutor instanceof GameCommandExecutor){
            ((GameCommandExecutor)defaultExecutor).help(sender);
        }
        return true;
    }
}
