package edu.fudan.davidgao.anothermetro;

/*
 * A game of AnotherMetro
 */

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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
                initSiteSpawn();
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
    public void setTickInterval(long interval) throws GameException {
        if (state == GameState.NEW) {
            tickInterval = interval;
        } else throw new GameException("Game already started");
    }
    public int[] getRoi() {
        final int[] tmp = {roiX1, roiX2, roiY1, roiY2};
        return tmp;
    }
    public MapDatum[][] getMap() {
        return map;
    }
    public Site[] getSites() {
        Site[] tmp = new Site[sites.size()];
        sites.copyInto(tmp);
        return tmp;
    }
    public long getGrowthInterval() {
        return growthInterval;
    }
    public void setGrowthInterval(long interval) throws GameException {
        if (state == GameState.NEW) {
            growthInterval = interval;
        } else throw new GameException("Game already started");
    }

    /* Private */
    private Random rand = new Random();
    private GameState state = GameState.NEW;
    private Game(MapDatum[][] map) {
        this.map = map;
        sizeX = map.length;
        sizeY = map[0].length;
        roiX1 = roiX1Base = (int)((double)sizeX * 0.4);
        roiX2 = roiX2Base = (int)((double)sizeX * 0.6);
        roiY1 = roiY1Base = (int)((double)sizeY * 0.4);
        roiY2 = roiY2Base = (int)((double)sizeY * 0.6);
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
            if (tickCounter >= nextSiteSpawn && sites.size() < maxSites) grow();
        }
    }

    /* Growth and map */
    private long growthInterval = 30; /* in ticks */
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
        growth += 1;
        final double rate = (double)growth / (double)maxGrowth;
        final double delta = 1 - rate;
        roiX1 = (int)((double)roiX1Base * delta);
        roiX2 = (int)((double)sizeX * rate + (double)roiX2Base * delta);
        roiY1 = (int)((double)roiY1Base * delta);
        roiY2 = (int)((double)sizeY * rate + (double)roiY2Base * delta);
    }

    /* Sites */
    private long siteSpawnInterval = 10; /* in ticks */
    private long nextSiteSpawn;
    private int maxSites = 40;
    private int siteSpawnTries = 100;
    private int uniqueSites = 0;
    private int maxUniqueSites = 5;
    private double siteDist = 10.0;
    private double siteRate1[] = {0.4, 0.7, 0.8, 1.0};
    private double siteRate2[] = {0.5, 0.875, 1.0, 1.0};
    private Vector<ActiveSite> sites = new Vector<>();
    private void initSiteSpawn() {
        nextSiteSpawn = siteSpawnInterval;
        spawnSite(0);
        spawnSite(1);
        spawnSite(2);
    }
    private boolean siteValid(int x, int y) {
        for (int i = 0; i < sites.size(); i += 1) {
            if (sites.elementAt(i).dist(x, y) < siteDist) {
                return false;
            }
        }
        return true;
    }
    private void spawnSite() {
        /* NOTE: Caller should always sync */
        double[] rate;
        if (uniqueSites == maxUniqueSites) {
            rate = siteRate2;
        } else {
            rate = siteRate1;
        }
        double r = rand.nextDouble();
        int tier = 0, type;
        while (r < rate[tier]) {
            tier += 1;
        }
        if (tier > 2) {
            type = 3 + uniqueSites;
        } else {
            type = tier;
        }
        boolean spawned = spawnSite(type);
        if (spawned && tier > 2) {
            uniqueSites += 1;
        }
    }
    private boolean spawnSite(int type) {
        /* NOTE: Caller should always sync */
        for (int i = 0; i < siteSpawnTries; i += 1){
            final int x = rand.nextInt(roiX2 - roiX1) + roiX1;
            final int y = rand.nextInt(roiY2 - roiY1) + roiY1;
            if (siteValid(x, y)) {
                sites.add(new ActiveSite(x, y, type));
                return true;
            }
        }
        return false;
    }
}
