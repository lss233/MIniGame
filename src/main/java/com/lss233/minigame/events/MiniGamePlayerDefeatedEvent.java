package com.lss233.minigame.events;

import com.lss233.minigame.GamePlayer;
import com.lss233.minigame.events.player.MiniGamePlayerEvent;
import org.bukkit.event.Cancellable;

public class MiniGamePlayerDefeatedEvent extends MiniGamePlayerEvent implements Cancellable {
    private boolean cancelled = false;
    private GamePlayer winner, loser;
    public MiniGamePlayerDefeatedEvent(GamePlayer winner, GamePlayer loser) {
        super(winner.getGame(), loser);
        this.winner = winner;
        this.loser = loser;
    }

    public GamePlayer getWinner() {
        return winner;
    }

    public void setWinner(GamePlayer winner) {
        this.winner = winner;
    }

    public GamePlayer getLoser() {
        return loser;
    }

    public void setLoser(GamePlayer loser) {
        this.loser = loser;
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
