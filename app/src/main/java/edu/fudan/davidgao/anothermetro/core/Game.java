package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.tools.*;

public abstract class Game {
    /* Singleton */
    public static Game getInstance() {
        return IGame2.getInstance();
    }

    /* Game creation */
    public static Game create() throws GameException {
        return IGame2.create();
    }
    public static Game create(Matrix2D<MapDatum> map) throws GameException {
        return IGame2.create(map);
    }

    /* Callback */
    public abstract Broadcaster getCallbackBroadcaster(GameEvent event);

    /* Life cycle control */
    public abstract void start() throws GameException;
    public abstract void run() throws GameException;
    public abstract void pause() throws GameException;
    public abstract void kill() throws GameException;
    public abstract void destroy() throws GameException;

    /* Game Data */
    public abstract GameState getState();
    public abstract Matrix2D<MapDatum> getMap();
    public abstract Rectangle<Integer> getRoi();
    public abstract Point<Integer> getSize();
    public abstract ArrayList<Site> getSites();

    /* Tick timer */
    public abstract long getTickInterval();
    public abstract void setTickInterval(long interval) throws GameException;
    public abstract long getTickCounter();

    /* Growth */
    public abstract long getGrowthInterval();
    public abstract void setGrowthInterval(long interval) throws GameException;
    public abstract void setGrowth(int maxGrowth, int baseGrowth) throws GameException;

    /* Site Spawn */
    public abstract long getSiteSpawnInterval();
    public abstract void setSiteSpawnInterval(long interval) throws GameException;

    /* Lines */
    public abstract ArrayList<Line> getLines();
    public abstract void addLine(Site s1, Site s2) throws GameException;
    public abstract void extendLine(Line l, Site src, Site dest) throws GameException;
}
