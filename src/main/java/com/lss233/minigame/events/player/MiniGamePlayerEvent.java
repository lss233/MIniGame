package com.lss233.minigame.events.player;

import com.lss233.minigame.Game;
import com.lss233.minigame.GamePlayer;
import com.lss233.minigame.events.MiniGameEvent;

public class MiniGamePlayerEvent extends MiniGameEvent {
    private final GamePlayer player;

    public MiniGamePlayerEvent(Game game, GamePlayer gamePlayer) {
        super(game);
        this.player = gamePlayer;
    }
    public GamePlayer getPlayer() {
        return player;
    }
}
