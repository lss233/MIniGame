package com.lss233.minigame.utils;

import com.lss233.minigame.Game;
import com.lss233.minigame.Plugin;

public abstract  class CountdownAction implements Runnable {
    private final Game game;
    private final Plugin plugin;
    private final int seconds;
    private final Runnable countingAction;
    private final int scheduledTaskId;
    private final int counterTaskId;

    protected CountdownAction(Game game, int seconds, Runnable countingAction) {
        this.game = game;
        this.plugin = game.getPlugin();
        this.seconds = seconds;
        this.countingAction = countingAction;
        counterTaskId = plugin.getJavaPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(plugin.getJavaPlugin(), countingAction, 0, 20);
        plugin.getJavaPlugin().getServer().getScheduler().runTaskLater(plugin.getJavaPlugin(), () -> plugin.getJavaPlugin().getServer().getScheduler().cancelTask(counterTaskId), seconds * 20);
        this.scheduledTaskId = plugin.getJavaPlugin().getServer().getScheduler().runTaskLater(plugin.getJavaPlugin(), this, seconds * 20).getTaskId();
        game.getCountdownActions().add(this);
    }
    public void cancel(){
        plugin.getJavaPlugin().getServer().getScheduler().cancelTask(counterTaskId);
        plugin.getJavaPlugin().getServer().getScheduler().cancelTask(scheduledTaskId);
    }

    public Game getGame() {
        return game;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
