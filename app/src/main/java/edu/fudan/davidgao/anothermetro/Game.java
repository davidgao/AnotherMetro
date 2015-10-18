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
    public static Game create() throws GameException {
        if (instance == null){
            instance = new Game();
            return instance;
        } else throw new GameException("Bad game state.");
    }

    public void start() throws GameException {
        if (state == GameState.NEW) {
            state = GameState.PAUSED;
            initGrowth();
        } else throw new GameException("Bad game state.");
    }

    public void run() throws GameException {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            tickTimer.schedule(tickTask, tickInterval, tickInterval);
        } else throw new GameException("Bad game state.");
    }

    public void pause() throws GameException {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            tickTimer.cancel();
        } else throw new GameException("Bad game state.");
    }

    public void kill() throws GameException {
        if (state == GameState.NEW || state == GameState.PAUSED || state == GameState.RUNNING) {
            state = GameState.ZOMBIE;
        } else throw new GameException("Bad game state.");
    }

    public void destroy() throws GameException {
        if (state == GameState.ZOMBIE) {
            Game.instance = null;
        } else throw new GameException("Bad game state.");
    }

    /* General information */
    public GameState getState() {
        return state;
    }
    public long getTickCounter() {
        return tickCounter;
    }
    public long getTickInterval() {
        return tickInterval;
    }
    public void setTickInterval(long interval) {
        if (state == GameState.NEW) {
            tickInterval = interval;
        }
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
        tryGrow();
    }

    /* Growth */
    private long growthInterval = 10; /* in ticks */
    private long nextGrowth;
    private int maxGrowth = 20; /* stages */
    private int growth = 0;
    private void initGrowth() {
        nextGrowth = growthInterval;
    }
    private void tryGrow() {

    }
}