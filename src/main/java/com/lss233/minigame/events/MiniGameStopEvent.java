package com.lss233.minigame.events;

import com.lss233.minigame.Game;

public class MiniGameStopEvent extends MiniGameEvent {
    public MiniGameStopEvent(Game game) {
        super(game);
    }
    public static class Pre extends MiniGameStopEvent {

        public Pre(Game game) {
            super(game);
        }
    }

    public static class Post extends MiniGameStopEvent {

        public Post(Game game) {
            super(game);
        }
    }
}
