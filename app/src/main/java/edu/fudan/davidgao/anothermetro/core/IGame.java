package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.fudan.davidgao.anothermetro.tools.*;

class IGame extends Game {
    /* Default parameters */
    private static final long defaultTickInterval = 1000; /* in ms */
    private static final int defaultMaxGrowth = 25;
    private static final int defaultBaseGrowth = 10;
    private static final long defaultGrowthInterval = 30; /* in ticks */
    private static final long defaultSiteSpawnInterval = 20; /* in ticks */

    /* Singleton */
    protected static Game instance = null;

    public static Game getInstance() {
        return instance;
    }

    /* Private constructor */
    private IGame(Matrix2D<MapDatum> map) {
        /* Copy map */
        this.map = map.copy();
        /* Init the game */
        initDispatcher();
        initTick();
        initGrowth();
        initSiteSpawn();
    }

    /* Internal initialization */
    private void initDispatcher() {
        dispatcher = new EventDispatcher();
    }
    private void initTick() {
        tickTimer = new Timer();
        tickTask = new RunnableTimerTask(dispatcher);
        tickCounter = new Counter();
        dispatcher.addInternalListener(tickCounter);
    }
    private void initGrowth() {
        try {
            setGrowth(defaultMaxGrowth, defaultBaseGrowth);
        }
        catch (GameException ex) {
            /* default values won't cause exceptions */
        }
        growthIntervalRunnable = new IntervalRunnable(growthRunnable);
        growthIntervalRunnable.setInterval(defaultGrowthInterval);
        growthBroadcaster = new Broadcaster();
        growthNotifier = dispatcher.addSnoozeCallback(growthBroadcaster);
        dispatcher.addInternalListener(growthIntervalRunnable);
    }
    private void initSiteSpawn() {
        BasicSiteValidator tmp = new BasicSiteValidator(this);
        //tmp.setMinDist(5.0);
        siteValidator = tmp;

        siteSpawnIntervalRunnable = new IntervalRunnable(siteSpawnRunnable);
        siteSpawnIntervalRunnable.setInterval(defaultSiteSpawnInterval);
        siteSpawnBroadcaster = new Broadcaster();
        siteSpawnNotifier = dispatcher.addSnoozeCallback(siteSpawnBroadcaster);
        dispatcher.addInternalListener(siteSpawnIntervalRunnable);
    }

    /* Game creation */
    public static Game create() throws GameException {
        /* Create a all-land map */
        Matrix2D<MapDatum> map = new Matrix2D<>(400, 300);
        map.fill(MapDatum.LAND);
        /* and try to create */
        return create(map);
    }
    public static synchronized Game create(Matrix2D<MapDatum> map) throws GameException {
        if (instance == null) {
            instance = new IGame(map);
            return instance;
        } else throw new GameException("Cannot create game: Game already exists.");
    }

    /* Life cycle control */
    public synchronized void start() throws GameException {
        if (state == GameState.NEW) {
            state = GameState.PAUSED;
            initTick();
            startGrowth();
            startSiteSpawn();
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
    public void destroy() throws GameException {
        synchronized (IGame.class){
            if (state == GameState.ZOMBIE) {
                instance = null;
            } else throw new GameException("Cannot destroy game: Game is not a zombie.");
        }
    }

    /* Game Data */
    private GameState state = GameState.NEW;
    public GameState getState() {
        return state;
    }
    public void assertState(GameState state) throws GameException {
        if (this.state != state) throw new GameException("Not in proper game state");
    }
    private Matrix2D<MapDatum> map;
    public Matrix2D<MapDatum> getMap() {
        return map.copy();
    }
    private Rectangle<Integer> roi;
    public Rectangle<Integer> getRoi() {
        return roi;
    }
    public Point<Integer> getSize() {
        return map.size;
    }
    public ArrayList<Site> getSites() {
        return sites;
    }

    /* Tick timer */
    private long tickInterval = defaultTickInterval;
    public long getTickInterval() {
        return tickInterval;
    }
    public void setTickInterval(long interval) throws GameException {
        assertState(GameState.NEW);
        tickInterval = interval;
    }
    private Counter tickCounter;
    public long getTickCounter() {
        return tickCounter.getCounter();
    }
    private Broadcaster tickBroadcaster = new Broadcaster();
    private TimerTask tickTask;
    private Timer tickTimer;

    /* Growth */
    private final Runnable growthRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (IGame.this) {
                try {
                    roi = roiGenerator.nextRoi();
                    growthNotifier.run();
                }
                catch (AlgorithmException exception) {
                    /* Skipping */
                }
            }
        }
    };
    private IntervalRunnable growthIntervalRunnable;
    public long getGrowthInterval() {
        return growthIntervalRunnable.getInterval();
    }
    public void setGrowthInterval(long interval) throws GameException {
        assertState(GameState.NEW);
        growthIntervalRunnable.setInterval(interval);
    }
    public synchronized void setGrowth(int maxGrowth, int baseGrowth) throws GameException {
        assertState(GameState.NEW);
        roiGenerator = new RoiGenerator(this.map.size, maxGrowth, baseGrowth);
        try {
            roi = roiGenerator.nextRoi();
        }
        catch (AlgorithmException ex) {
            throw new GameException("Invalid Growth", ex);
        }
    }
    private Broadcaster growthBroadcaster;
    public synchronized boolean addGrowthListener(Runnable listener) throws GameException {
        assertState(GameState.NEW);
        return growthBroadcaster.addListener(listener);
    }
    private Runnable growthNotifier;
    private RoiGenerator roiGenerator;
    private void startGrowth() {

    }

    /* Sites */
    private final Runnable siteSpawnRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (IGame.this) {
                try {
                    spawnSite();
                    siteSpawnNotifier.run();
                }
                catch (GameException ex) {
                    /* Skipping */
                }
            }
        }
    };
    private IntervalRunnable siteSpawnIntervalRunnable;
    public long getSiteSpawnInterval() {
        return siteSpawnIntervalRunnable.getInterval();
    }
    public void setSiteSpawnInterval(long interval) throws GameException {
        assertState(GameState.NEW);
        siteSpawnIntervalRunnable.setInterval(interval);
    }
    private Broadcaster siteSpawnBroadcaster;
    public boolean addSiteSpawnListener(Runnable listener) {
        return siteSpawnBroadcaster.addListener(listener);
    }
    private Runnable siteSpawnNotifier;
    private SiteValidator siteValidator;
    private void startSiteSpawn() {
        spawnSite(SiteType.fromInt(0));
        spawnSite(SiteType.fromInt(1));
        spawnSite(SiteType.fromInt(2));

    }

    /* TODO */
    private long nextSiteSpawn;
    private int maxSites = 40;
    private int siteSpawnTries = 100;
    private int uniqueSites = 0;
    private int maxUniqueSites = 5;
    private double siteDist = 10.0;
    private double siteRate1[] = {0.4, 0.7, 0.8, 1.0};
    private double siteRate2[] = {0.5, 0.875, 1.0, 1.0};
    private ArrayList<Site> sites = new ArrayList<>();
    private boolean siteValid(int x, int y) {
        return siteValidator.validate(x, y);
    }
    private void spawnSite() throws GameException {
        /* NOTE: Caller should always sync */
        double[] rate;
        if (sites.size() >= maxSites) {
            throw new GameException("Sites fully spawn");
        }
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
    private EventDispatcher dispatcher;
    private Random rand = new Random();
}
