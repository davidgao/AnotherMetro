package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.fudan.davidgao.anothermetro.tools.*;

public class IGame2 extends Game { //TODO
    /* Default parameters */
    // TODO: let tick interval (much) smaller, as skq is drawing train per tick.
    private static final long defaultTickInterval = 200; /* in ms */
    private static final int defaultMaxGrowth = 25;
    private static final int defaultBaseGrowth = 10;
    private static final long defaultGrowthInterval = 30; /* in ticks */
    private static final long defaultSiteSpawnInterval = 20; /* in ticks */
    private static final long defaultPassengerSpawnInterval = 5; /* in ticks */

    /* Singleton */
    protected static Game instance = null;
    public static Game getInstance() {
        return instance;
    }

    /* Logic */
    private GameLogic logic;

    /* Private constructor */
    private IGame2(Matrix2D<MapDatum> map) {
        /* Copy map */
        this.map = map.copy();
        /* Init the game */
        initLogic();
        initTick();
        initGrowth();
        initSiteSpawn();
        initTrainMove();
        initPassengerSpawn();
    }

    /* Game creation */
    public static Game create() throws GameException {
        /* Create a all-land map */
        Matrix2D<MapDatum> map = new Matrix2D<>(40, 30);
        map.fill(MapDatum.LAND);
        /* and try to create */
        return create(map);
    }
    public static synchronized Game create(Matrix2D<MapDatum> map) throws GameException {
        if (instance == null) {
            instance = new IGame2(map);
            return instance;
        } else throw new GameException("Cannot create game: Game already exists.");
    }

    /* Initialization */
    private void initLogic() {
        logic = new GameLogic();
    }
    private void initTick() {
        tickTimer = new Timer();
        tickTask = new RunnableTimerTask(logic);
        tickCounter = new Counter();
        logic.action.addListener(tickCounter);
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
        growthNotifier = logic.getAlarm(GameEvent.GROW);
        logic.action.addListener(growthIntervalRunnable);
    }
    private void initSiteSpawn() {
        siteValidator = new BasicSiteValidator(this);
        siteSpawnIntervalRunnable = new IntervalRunnable(siteSpawnRunnable);
        siteSpawnIntervalRunnable.setInterval(defaultSiteSpawnInterval);
        siteSpawnNotifier = logic.getAlarm(GameEvent.SITE_SPAWN);
        logic.action.addListener(siteSpawnIntervalRunnable);
    }
    private void initTrainMove() {
        trainMoveNotifier = logic.getAlarm(GameEvent.TRAIN_STATE_CHANGE);
        logic.action.addListener(trainMoveRunnable);
    }
    private void initPassengerSpawn() {
        passengerSpawnIntervalRunnable = new IntervalRunnable(passengerSpawnRunnable);
        passengerSpawnIntervalRunnable.setInterval(defaultPassengerSpawnInterval);
        passengerChangeNotifier = logic.getAlarm(GameEvent.PASSENGER_CHANGE);
        logic.action.addListener(passengerSpawnIntervalRunnable);
    }

    /* Callback */
    public Broadcaster getCallbackBroadcaster(GameEvent event) {
        return logic.externalCallback.getBroadcaster(event);
    }

    /* Life cycle */
    private GameState state = GameState.NEW;
    public GameState getState() {
        return state;
    }
    public void assertState(GameState state) throws GameException {
        if (this.state != state) throw new GameException("Not in proper game state");
    }
    public synchronized void start() throws GameException {
        if (state == GameState.NEW) {
            state = GameState.PAUSED;
            startGrowth();
            startSiteSpawn();
            startTrainMove();
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
        synchronized (IGame2.class){
            if (state == GameState.ZOMBIE) {
                instance = null;
            } else throw new GameException("Cannot destroy game: Game is not a zombie.");
        }
    }

    /* Map */
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

    /* Tick */
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
    private TimerTask tickTask;
    private Timer tickTimer;

    /* Growth */
    private final Runnable growthRunnable = new Runnable() {
        @Override
        public void run() {
            grow();
        }
    };
    private synchronized void grow() {
        try {
            roi = roiGenerator.nextRoi();
            growthNotifier.run();
        }
        catch (AlgorithmException exception) {
            /* Skipping */
        }
    }
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
    private Runnable growthNotifier;
    private RoiGenerator roiGenerator;
    private void startGrowth() {

    }

    /* Sites */
    private ArrayList<Site> sites = new ArrayList<>();
    public ArrayList<Site> getSites() {
        return sites;
    }
    private final Runnable siteSpawnRunnable = new Runnable() {
        @Override
        public void run() {
            trySpawnSite();
        }
    };
    private synchronized void trySpawnSite() {
        try {
            spawnSite();
            siteSpawnNotifier.run();
            }
        catch (GameException ex) {
            /* Skipping */
        }
    }
    private IntervalRunnable siteSpawnIntervalRunnable;
    public long getSiteSpawnInterval() {
        return siteSpawnIntervalRunnable.getInterval();
    }
    public void setSiteSpawnInterval(long interval) throws GameException {
        assertState(GameState.NEW);
        siteSpawnIntervalRunnable.setInterval(interval);
    }
    private Runnable siteSpawnNotifier;
    private SiteValidator siteValidator;
    private void startSiteSpawn() {
        spawnSite(SiteType.fromInt(0));
        spawnSite(SiteType.fromInt(1));
        spawnSite(SiteType.fromInt(2));
        forceNotify(GameEvent.SITE_SPAWN);
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
    private boolean siteValid(int x, int y) {
        return siteValidator.validate(x, y);
    }
    private void spawnSite() throws GameException {
        System.out.println("spawnSite");
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
        if (spawned) siteSpawnNotifier.run();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    private boolean spawnSite(SiteType type) {
        /* NOTE: Caller should always sync */
        for (int i = 0; i < siteSpawnTries; i += 1){
            final int x = rand.nextInt(roi.x2 - roi.x1) + roi.x1;
            final int y = rand.nextInt(roi.y2 - roi.y1) + roi.y1;
            if (siteValid(x, y)) {
                sites.add(new Site(new Point<>(x, y), type));
                System.out.printf("New site at %d %d\n", x, y);
                return true;
            }
        }
        System.out.println("spawnSite failed");
        return false;
    }

    /* Lines */
    ArrayList<Line> lines = new ArrayList<>();
    @SuppressWarnings("unchecked")
    public ArrayList<Line> getLines() {
        return (ArrayList<Line>)lines.clone();
    }
    public void addLine(Site s1, Site s2) throws GameException {
        if (s1 == s2) throw new GameException("Station conflict");
        lines.add(new Line(s1, s2));
        forceNotify(GameEvent.LINE_CHANGE);
        forceNotify(GameEvent.TRAIN_STATE_CHANGE);
    }
    public void extendLine(Line l, Site src, Site dest) throws GameException {
        l.extend(src, dest);
        forceNotify(GameEvent.LINE_CHANGE);
    }
    public boolean canAddLine(Site s1, Site s2) {
        return (s1 != s2);
    }
    public boolean canExtendLine(Line l, Site src, Site dest) {
        ArrayList<Site> s = l.getSites();
        int index = s.indexOf(src);
        return !(index > 0 && index < (s.size() - 1)) && !(s.contains(dest));
    }

    /* Train Moving */
    private Runnable trainMoveRunnable = new Runnable() {
        @Override
        public void run() {
            trainMove();
        }
    };
    private synchronized void trainMove() {
        boolean have_moved = false;
        long now = getTickCounter();
        for (Line line : lines){
            TrainState ts = line.train.getState();
            ArrayList<Site> s = line.getSites();
            if (ts instanceof StandbyTrainState) {
                Site curr = ((StandbyTrainState) ts).site;
                Site next = s.get(s.indexOf(curr) + ts.direction);
                double dist = curr.dist(next.pos);
                if (ts.direction == 1) {
                    ts = new RunningTrainState(line, curr, next, 1,
                            now, now + (long) dist * 5);
                } else {
                    ts = new RunningTrainState(line, next, curr, -1,
                            now, now + (long) dist * 5);
                }
                line.train.setState(ts);
                have_moved = true;
            } else if (ts instanceof RunningTrainState) {
                if (now < ((RunningTrainState) ts).arrival) {
                    continue;
                }
                int dir = ts.direction;
                Site curr;
                if (dir == 1) {
                    curr = ((RunningTrainState) ts).s2;
                } else {
                    curr = ((RunningTrainState) ts).s1;
                }
                if (dir == 1 && curr == s.get(s.size()-1)) {
                    dir = -1;
                }
                if (dir == -1 && curr == s.get(0)) {
                    dir = 1;
                }
                ts = new StandbyTrainState(line, curr, dir);
                line.train.setState(ts);
                have_moved = true;
            }
        }
        if (have_moved) {
            trainMoveNotifier.run();
        }
    }
    private Runnable trainMoveNotifier;
    private void startTrainMove() {

    }

    /* Passenger */
    ArrayList<Passenger> passengers = new ArrayList<>();
    @SuppressWarnings("unchecked")
    public ArrayList<Passenger> getPassengers() {
        return (ArrayList<Passenger>)passengers.clone();
    }
    private IntervalRunnable passengerSpawnIntervalRunnable;
    public long getPassengerSpawnInterval() {
        return passengerSpawnIntervalRunnable.getInterval();
    }
    public void setPassengerSpawnInterval(long interval) throws GameException {
        assertState(GameState.NEW);
        passengerSpawnIntervalRunnable.setInterval(interval);
    }
    private Runnable passengerSpawnRunnable = new Runnable() {
        @Override
        public void run() {
            spawnPassenger();
        }
    };
    private synchronized void spawnPassenger() {
        System.out.println("Passenger spawn");
        final double uniqueRate = (double)uniqueSites / (double)sites.size();
        final int type;
        if (rand.nextDouble() < uniqueRate) {
            type = rand.nextInt(uniqueSites) + 3;
        } else {
            double tmp = rand.nextDouble();
            if (tmp < 0.5) {
                type = 0;
            } else if (tmp < 0.875) {
                type = 1;
            } else {
                type = 2;
            }
        }
        int index = rand.nextInt(sites.size());
        Site s = sites.get(index);
        Passenger p = new Passenger(SiteType.fromInt(type), s);
        passengers.add(p);
        passengerChangeNotifier.run();
    }
    private Runnable passengerChangeNotifier;


    private void forceNotify(GameEvent event) {
        logic.writeBack.getBroadcaster(event).run();
        logic.internalCallback.getBroadcaster(event).run();
        logic.externalCallback.getBroadcaster(event).run();
    }

    private Random rand = new Random();
}
