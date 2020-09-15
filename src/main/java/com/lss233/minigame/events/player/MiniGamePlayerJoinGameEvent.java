package com.lss233.minigame.events.player;

import com.lss233.minigame.Game;
import com.lss233.minigame.GamePlayer;
import org.bukkit.event.Cancellable;

public class MiniGamePlayerJoinGameEvent extends MiniGamePlayerEvent implements Cancellable {
    private boolean cancelled = false;

    public MiniGamePlayerJoinGameEvent(Game game, GamePlayer gamePlayer) {
        super(game, gamePlayer);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
