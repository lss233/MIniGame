package com.lss233.minigame.events.player;

import com.lss233.minigame.Game;
import com.lss233.minigame.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Triggered when the plugin is saving player's data
 * which is stored before join in the game. <br/>
 * This event will be automatically triggered
 * within a {@link com.lss233.minigame.events.MiniGameStartEvent}. <br/>
 * We have help you store the players {@link Player#getExp()}, {@link Player#getFoodLevel()}, {@link Player#getHealth()},
 * {@link Player#getInventory()} and max health for you. <br/>
 * Handle this event if you have anything else to store.
 */
public class MiniGamePlayerDataSavingEvent extends MiniGamePlayerEvent implements Cancellable {
    private boolean cancel = false;
    public MiniGamePlayerDataSavingEvent(Game game, GamePlayer gamePlayer) {
        super(game, gamePlayer);
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
