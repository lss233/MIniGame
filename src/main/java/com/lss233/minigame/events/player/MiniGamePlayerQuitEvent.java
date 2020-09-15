package com.lss233.minigame.events.player;

import com.lss233.minigame.Game;
import com.lss233.minigame.GamePlayer;
import org.bukkit.event.Cancellable;

public class MiniGamePlayerQuitEvent extends MiniGamePlayerEvent{
    protected Game.QuitReason reason;
    public MiniGamePlayerQuitEvent(Game game, GamePlayer gamePlayer, Game.QuitReason reason) {
        super(game, gamePlayer);
        this.reason = reason;
    }

    public Game.QuitReason getReason() {
        return reason;
    }

    public static class Pre extends MiniGamePlayerQuitEvent implements Cancellable {
        private boolean cancelled = false;
        public Pre(Game game, GamePlayer gamePlayer, Game.QuitReason reason) {
            super(game, gamePlayer, reason);
        }


        public void setReason(Game.QuitReason reason) {
            this.reason = reason;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }
    }

    public static class Post extends MiniGamePlayerQuitEvent {

        public Post(Game game, GamePlayer gamePlayer, Game.QuitReason reason) {
            super(game, gamePlayer, reason);
        }
    }
}
