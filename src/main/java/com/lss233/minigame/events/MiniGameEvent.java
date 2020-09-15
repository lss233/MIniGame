package com.lss233.minigame.events;

import com.lss233.minigame.Game;
import com.lss233.minigame.Plugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MiniGameEvent extends Event {


    private final Game game;

    private final Plugin plugin;

    public MiniGameEvent(Game game) {
        this.game = game;
        this.plugin = game.getPlugin();
    }

    public Game getGame() {
        return game;
    }
    private final static HandlerList handlerList = new HandlerList();

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
