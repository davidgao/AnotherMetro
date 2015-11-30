package edu.fudan.davidgao.anothermetro.core;

import java.util.Timer;
import java.util.TimerTask;

import edu.fudan.davidgao.anothermetro.tools.*;

public class IGame2 extends Game { //TODO
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

    /* Logic */
    private GameLogic logic;

    /* Private constructor */
    private IGame2(Matrix2D<MapDatum> map) {
        /* Copy map */
        this.map = map.copy();
        /* Init the game */
        initLogic();
        initTick();
        //initGrowth();
        //initSiteSpawn();
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
            //startGrowth();
            //startSiteSpawn();
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
}
