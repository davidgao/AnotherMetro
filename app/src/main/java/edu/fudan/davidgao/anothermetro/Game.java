package edu.fudan.davidgao.anothermetro;

/*
 * A game of AnotherMetro
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
    /* Default parameters */
    private static final long defaultTickInterval = 1000; /* in ms */
    private static final long defaultGrowthInterval = 30; /* in ticks */

    /* Singleton and his constructor */
    private static Game instance = null;
    public static Game getInstance() {
        return instance;
    }
    private Game(MapDatum[][] map, int maxGrowth, int baseGrowth) throws GameException {
        /* Read size and copy map */
        size = new Point<>(map.length, map[0].length);
        this.map = new MapDatum[size.x][size.y];
        for (int i = 0; i < size.x; i += 1) {
            this.map[i] = map[i].clone();
        }
        /* Start an ROI Generator */
        roiGenerator = new RoiGenerator(size, maxGrowth, baseGrowth);
        try {
            roi = roiGenerator.nextRoi();
        }
        catch (AlgorithmException ex) {
            throw new GameException("Invalid baseGrowth", ex);
        }
    }

    /* Game creation */
    public static Game create(int maxGrowth, int baseGrowth) throws GameException {
        /* This is a default game */
        /* Create a all-land map */
        MapDatum[][] map = new MapDatum[400][300];
        for (MapDatum[] mapLine: map) {
            for (int i = 0; i < mapLine.length; i+= 1) {
                mapLine[i] = MapDatum.LAND;
            }
        }
        return create(map, maxGrowth, baseGrowth);

    }
    public static synchronized Game create(MapDatum[][] map, int maxGrowth, int baseGrowth)
            throws GameException {
        if (instance == null) {
            instance = new Game(map, maxGrowth, baseGrowth);
            return instance;
        } else throw new GameException("Cannot create game: Game already exists.");
    }

    /* Life cycle control */
    public synchronized void start() throws GameException {
        if (state == GameState.NEW) {
            state = GameState.PAUSED;
            initTimer();
            initGrowth();
            initSiteSpawn();
        } else throw new GameException("Cannot start game: Game is not new.");
    }
    public synchronized void run() throws GameException {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        } else throw new GameException("Cannot run game: Game is not paused.");
        tickTimer.schedule(tickTask, tickInterval, tickInterval);
    }
    public synchronized void pause() throws GameException {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else throw new GameException("Cannot pause game: Game is not running.");
        tickTimer.cancel();
    }
    public synchronized void kill() throws GameException {
        if (state == GameState.RUNNING) {
            pause();
        }
        if (state == GameState.NEW || state == GameState.PAUSED) {
            state = GameState.ZOMBIE;
        } else throw new GameException("Cannot kill game: Game is already a zombie.");
    }
    public static synchronized void destroy() throws GameException {
        if (instance.state == GameState.ZOMBIE) {
            Game.instance = null;
        } else throw new GameException("Cannot destroy game: Game is not a zombie.");
    }

    /* General information */
    private GameState state = GameState.NEW;
    public GameState getState() {
        return state;
    }

    private MapDatum[][] map;
    public MapDatum[][] getMap() {
        return map;
    }

    private Rectangle<Integer> roi;
    public Rectangle<Integer> getRoi() {
        return roi;
    }

    private Point<Integer> size;
    public Point<Integer> getSize() {
        return size;
    }


    /* Tick timer */
    private long tickInterval = defaultTickInterval;
    public long getTickInterval() {
        return tickInterval;
    }
    public void setTickInterval(long interval) throws GameException {
        if (state == GameState.NEW) {
            tickInterval = interval;
        } else throw new GameException("Game already started");
    }
    private Counter tickCounter = new Counter();
    public long getTickCounter() {
        return tickCounter.getCounter();
    }
    private Broadcaster tickBroadcaster = new Broadcaster();
    private TimerTask tickTask = new RunnableTimerTask(tickBroadcaster);
    private Timer tickTimer = new Timer();
    private synchronized void initTimer() {
        tickBroadcaster.addListener(tickCounter);
    }

    /* Growth */
    public long getGrowthInterval() {
        return growthIntervalRunnable.getInterval();
    }
    public void setGrowthInterval(long interval) throws GameException {
        if (state == GameState.NEW) {
            growthIntervalRunnable.setInterval(interval);
        } else throw new GameException("Game already started");
    }
    private RoiGenerator roiGenerator;
    private final Runnable growthRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (Game.this) {
                try {
                    roi = roiGenerator.nextRoi();
                }
                catch (AlgorithmException exception) {
                    // Fully grown, skipping
                }
            }
        }
    };
    private IntervalRunnable growthIntervalRunnable = new IntervalRunnable(growthRunnable);
    private void initGrowth() {
        growthIntervalRunnable.setInterval(defaultGrowthInterval);
        tickBroadcaster.addListener(growthIntervalRunnable);
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
    public void setSiteSpawnInterval(int interval) throws GameException {
        if (state == GameState.NEW) {
            siteSpawnInterval = interval;
        } else throw new GameException("Game is not new.");
    }
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
        while (r >= rate[tier]) {
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
                sites.add(new Site(new Point<>(x, y), type));
                return true;
            }
        }
        return false;
    }
    public ArrayList<Site> getSites() {
        return sites;
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

    /* Utilities */
    private Random rand = new Random();
}
