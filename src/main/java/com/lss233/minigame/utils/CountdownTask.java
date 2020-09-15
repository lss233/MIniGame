package com.lss233.minigame.utils;

import com.lss233.minigame.Game;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CountdownTask extends CountdownAction {
    private final Runnable task;
    protected CountdownTask(Game game, AtomicInteger seconds, Consumer<Integer> countingAction, Runnable task) {
        super(game, seconds.get(), () -> countingAction.accept(seconds.getAndDecrement()));
        this.task = task;
    }

    @Override
    public void run() {
        task.run();
    }

    public static Builder builder(){
        return new Builder();
    }
    public static class Builder{
        private int seconds;
        private Runnable task;
        private Consumer<Integer> countingAction;
        public Builder count(Consumer<Integer> count){
            this.countingAction = count;
            return this;
        }

        public Builder task(Runnable task) {
            this.task = task;
            return this;
        }
        public Builder seconds(int seconds) {
            this.seconds = seconds;
            return this;
        }

        public CountdownTask build(Game game){
            return new CountdownTask(game, new AtomicInteger(seconds), countingAction, task);
        }


    }
}
