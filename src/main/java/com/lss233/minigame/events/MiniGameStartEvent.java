package com.lss233.minigame.events;

import com.lss233.minigame.Game;
import org.bukkit.event.Cancellable;

public class MiniGameStartEvent extends MiniGameEvent implements Cancellable{
    private boolean cancel = false;
    public MiniGameStartEvent(Game game) {
        super(game);
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
