package edu.fudan.davidgao.anothermetro;

/*
 * A game of AnotherMetro
 */

import java.util.ArrayList;
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
            } else throw new GameException("Cannot create game: Game already exists.");
        }
    }
    public static Game create(MapDatum[][] map) throws GameException {
        synchronized (Game.class) {
            if (instance == null) {
                instance = new Game(map);
                return instance;
            } else throw new GameException("Cannot create game: Game already exists.");
        }
    }

    public void start() throws GameException {
        synchronized (this) {
            if (state == GameState.NEW) {
                state = GameState.PAUSED;
                initGrowth();
                initSiteSpawn();
            } else throw new GameException("Cannot start game: Game is not new.");
        }
    }

    public void run() throws GameException {
        synchronized (this) {
            if (state == GameState.PAUSED) {
                state = GameState.RUNNING;
            } else throw new GameException("Cannot run game: Game is not paused.");
        }
        tickTimer.schedule(tickTask, tickInterval, tickInterval);
    }

    public void pause() throws GameException {
        synchronized (this) {
            if (state == GameState.RUNNING) {
                state = GameState.PAUSED;
            } else throw new GameException("Cannot pause game: Game is not running.");
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
            } else throw new GameException("Cannot kill game: Game is already a zombie.");
        }
    }

    public void destroy() throws GameException {
        synchronized (this) {
            if (state == GameState.ZOMBIE) {
                Game.instance = null;
            } else throw new GameException("Cannot destroy game: Game is not a zombie.");
        }
    }

    /* General */
    public GameState getState() {
        return state;
    }
    public MapDatum[][] getMap() {
        return map;
    }
    public Rectangle getRoi() { /* Game grid coordinate */
        return roi;
    }


    /* Getting and setting */
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
    public ArrayList<Site> getSites() {
        return sites;
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
        int x1 = (int)((double)sizeX * 0.4);
        int x2 = (int)((double)sizeX * 0.6);
        int y1 = (int)((double)sizeY * 0.4);
        int y2 = (int)((double)sizeY * 0.6);
        roi = roiBase = new Rectangle(x1, x2, y1, y2);
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
    private Rectangle roi, roiBase;
    private void initGrowth() {
        nextGrowth = growthInterval;
    }
    private void grow() {
        /* NOTE: Caller should always sync */
        nextGrowth += growthInterval;
        growth += 1;
        final double rate = (double)growth / (double)maxGrowth;
        final double delta = 1 - rate;
        int x1 = (int)((double)roiBase.x1 * delta);
        int x2 = (int)((double)sizeX * rate + (double)roiBase.x2 * delta);
        int y1 = (int)((double)roiBase.y1 * delta);
        int y2 = (int)((double)sizeY * rate + (double)roiBase.y2 * delta);
        roi = new Rectangle(x1, x2, y1, y2);
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
    private ArrayList<Site> sites = new ArrayList<>();
    private void initSiteSpawn() {
        nextSiteSpawn = siteSpawnInterval;
        spawnSite(SiteType.fromInt(0));
        spawnSite(SiteType.fromInt(1));
        spawnSite(SiteType.fromInt(2));
    }
    private boolean siteValid(int x, int y) {
        for (int i = 0; i < sites.size(); i += 1) {
            if (sites.get(i).dist(x, y) < siteDist) {
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
        boolean spawned = spawnSite(SiteType.fromInt(type));
        if (spawned && tier > 2) {
            uniqueSites += 1;
        }
    }
    private boolean spawnSite(SiteType type) {
        /* NOTE: Caller should always sync */
        for (int i = 0; i < siteSpawnTries; i += 1){
            final int x = rand.nextInt(roi.x2 - roi.x1) + roi.x1;
            final int y = rand.nextInt(roi.y2 - roi.y1) + roi.y1;
            if (siteValid(x, y)) {
                sites.add(new Site(x, y, type));
                return true;
            }
        }
        return false;
    }

    /* Line operation */
    private ArrayList<Line> lines = new ArrayList<>();
    private int maxLines = 3;
    public ArrayList<Line> getLines() {
        return lines;
    }
    public Line newLine(Site s1, Site s2) throws GameException{
        if (lines.size() >= maxLines) throw new GameException("Can't add more lines.");
        Line tmp = new Line(s1, s2);
        lines.add(tmp);
        return tmp;
    }
}
