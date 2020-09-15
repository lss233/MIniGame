package com.lss233.minigame.utils;

public abstract class GameTaskRunnable implements Runnable {
    protected final long delay, period;
    public GameTaskRunnable(long delay, long period){
        this.delay = delay;
        this.period = period;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }
}
