package edu.fudan.davidgao.anothermetro;

/*
 * A game of AnotherMetro
 */

import java.util.Timer;
import java.util.TimerTask;

public class Game {
    private static Game instance = null;

    public static Game getInstance() {
        return instance;
    }

    /* Life cycle control */
    public static Game create() {
        if (instance == null){
            instance = new Game();
            return instance;
        } else return null;
    }

    public int start() {
        if (state == GameState.NEW) {
            state = GameState.PAUSED;
            return 0;
        } else return 1;
    }

    public int run() {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            tickTimer.schedule(tickTask, tickInterval, tickInterval);
            return 0;
        } else return 1;
    }

    public int pause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            tickTimer.cancel();
            return 0;
        } else return 1;
    }

    /* General information */
    public GameState getState() {
        return state;
    }

    public long getTickCounter() {
        return tickCounter;
    }

    /* Argument */
    public void setTickInterval(long interval) {
        tickInterval = interval;
    }

    public long getTickInterval() {
        return tickInterval;
    }

    /* Private */
    private GameState state = GameState.NEW;
    private Game() {
    }

    /* Game Ticks */
    private long tickInterval = 1000; /* in ms */
    private long tickCounter = 0;
    private Timer tickTimer = new Timer();
    private TimerTask tickTask = new TimerTask() {
        @Override
        public void run() {
            tick();
        }
    };
    private void tick() {
        tickCounter += 1;
    }

    /* Growth */
    //private int growInterval = 10; /* in ticks */
    //private int maxGrowth = 20; /* stages */
    //private int growth = 0;
}
