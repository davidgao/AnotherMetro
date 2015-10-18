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
        synchronized (Game.class) {
            if (instance == null) {
                instance = new Game(new MapDatum[480][640]);
                return instance;
            } else throw new GameException("Bad game state.");
        }
    }
    public static Game create(MapDatum[][] map) throws GameException {
        synchronized (Game.class) {
            if (instance == null) {
                instance = new Game(map);
                return instance;
            } else throw new GameException("Bad game state.");
        }
    }

    public void start() throws GameException {
        synchronized (this) {
            if (state == GameState.NEW) {
                state = GameState.PAUSED;
                initGrowth();
            } else throw new GameException("Bad game state.");
        }
    }

    public void run() throws GameException {
        synchronized (this) {
            if (state == GameState.PAUSED) {
                state = GameState.RUNNING;
            } else throw new GameException("Bad game state.");
        }
        tickTimer.schedule(tickTask, tickInterval, tickInterval);
    }

    public void pause() throws GameException {
        synchronized (this) {
            if (state == GameState.RUNNING) {
                state = GameState.PAUSED;
            } else throw new GameException("Bad game state.");
        }
        tickTimer.cancel();
    }

    public void kill() throws GameException {
        synchronized (this) {
            if (state == GameState.RUNNING) {
                pause();
            }
            if (state == GameState.NEW || state == GameState.PAUSED) {
                state = GameState.ZOMBIE;
            } else throw new GameException("Bad game state.");
        }
    }

    public void destroy() throws GameException {
        synchronized (this) {
            if (state == GameState.ZOMBIE) {
                Game.instance = null;
            } else throw new GameException("Bad game state.");
        }
    }

    /* Getting and setting */
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
    public int[] getSize() {
        int[] tmp = {sizeX, sizeY};
        return tmp;
    }
    public int[] getRoi() {
        int[] tmp = {roiX1, roiX2, roiY1, roiY2};
        return tmp;
    }
    public MapDatum getMapDatum(int x, int y) {
        return map[x][y];
    }

    /* Private */
    private GameState state = GameState.NEW;
    private Game(MapDatum[][] map) {
        this.map = map;
        sizeX = map.length;
        sizeY = map[0].length;
        roiX1 = roiX1Base = (int)((float)sizeX * 0.4);
        roiX2 = roiX2Base = (int)((float)sizeX * 0.6);
        roiY1 = roiY1Base = (int)((float)sizeY * 0.4);
        roiY2 = roiY2Base = (int)((float)sizeY * 0.6);
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
        synchronized (this) {
            tickCounter += 1;
            if (tickCounter >= nextGrowth && growth < maxGrowth) grow();
        }
    }

    /* Growth and map */
    private long growthInterval = 10; /* in ticks */
    private long nextGrowth;
    private int maxGrowth = 20; /* stages */
    private int growth = 0;
    private MapDatum[][] map;
    private int sizeX, sizeY;
    private int roiX1, roiX2, roiY1, roiY2;
    private int roiX1Base, roiX2Base, roiY1Base, roiY2Base;
    private void initGrowth() {
        nextGrowth = growthInterval;
    }
    private void grow() {
        /* NOTE: Caller should always sync */
        nextGrowth += growthInterval;
        float rate = (float)growth / (float)maxGrowth;
        float delta = 1 - rate;
        roiX1 = (int)((float)roiX1Base * delta);
        roiX2 = (int)((float)sizeX * rate + (float)roiX2Base * delta);
        roiY1 = (int)((float)roiY1Base * delta);
        roiY2 = (int)((float)sizeY * rate + (float)roiY2Base * delta);
    }
}
