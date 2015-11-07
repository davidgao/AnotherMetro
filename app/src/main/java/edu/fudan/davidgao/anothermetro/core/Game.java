package edu.fudan.davidgao.anothermetro.core;

import edu.fudan.davidgao.anothermetro.tools.*;

public abstract class Game {
    /* Singleton */
    public static Game getInstance() {
        return IGame.getInstance();
    }

    /* Game creation */
    public static Game create(int maxGrowth, int baseGrowth) throws GameException {
        return IGame.create(maxGrowth, baseGrowth);
    }
    public static Game create(MapDatum[][] map, int maxGrowth, int baseGrowth)
            throws GameException {
        return IGame.create(map, maxGrowth, baseGrowth);
    }

    /* Life cycle control */
    public abstract void start() throws GameException;
    public abstract void run() throws GameException;
    public abstract void pause() throws GameException;
    public abstract void kill() throws GameException;
    public abstract void destroy() throws GameException;

    /* General information */
    public abstract GameState getState();
    public abstract MapDatum[][] getMap();
    public abstract Rectangle<Integer> getRoi();
    public abstract Point<Integer> getSize();

    /* Tick timer */
    public abstract long getTickInterval();
    public abstract void setTickInterval(long interval) throws GameException;
    public abstract long getTickCounter();

    /* Growth */
    public abstract long getGrowthInterval();
    public abstract void setGrowthInterval(long interval) throws GameException;
}
