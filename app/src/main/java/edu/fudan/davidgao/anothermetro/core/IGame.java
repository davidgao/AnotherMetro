package edu.fudan.davidgao.anothermetro.core;

import java.util.Timer;
import java.util.TimerTask;

import edu.fudan.davidgao.anothermetro.tools.*;

class IGame extends Game {
    /* Default parameters */
    private static final long defaultTickInterval = 1000; /* in ms */
    private static final long defaultGrowthInterval = 30; /* in ticks */

    /* Singleton */
    protected static Game instance = null;
    public static Game getInstance() {
        return instance;
    }

    /* Private constructor */
    private IGame(MapDatum[][] map, int maxGrowth, int baseGrowth) throws GameException {
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
            instance = new IGame(map, maxGrowth, baseGrowth);
            return instance;
        } else throw new GameException("Cannot create game: Game already exists.");
    }

    /* Life cycle control */
    public synchronized void start() throws GameException {
        if (state == GameState.NEW) {
            state = GameState.PAUSED;
            initTimer();
            initGrowth();
            //initSiteSpawn();
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
    private IntervalRunnable growthIntervalRunnable = new IntervalRunnable(growthRunnable);
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
            synchronized (IGame.this) {
                try {
                    roi = roiGenerator.nextRoi();
                }
                catch (AlgorithmException exception) {
                    // Fully grown, skipping
                }
            }
        }
    };
    private void initGrowth() {
        growthIntervalRunnable.setInterval(defaultGrowthInterval);
        tickBroadcaster.addListener(growthIntervalRunnable);
    }
}
