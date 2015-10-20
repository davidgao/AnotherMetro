package edu.fudan.davidgao.anothermetro;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {
    private Game game;

    /* Self test */
    @Test(expected=AssertionError.class)
    public void badAssertion() throws Exception {
        assertTrue(false);
    }

    @Test
    public void goodAssertion() throws Exception {
        assertTrue(true);
    }

    /* Tools */
    @Before
    public void buildup() {
        try{
            game = Game.create();
        } catch (GameException exception) {
            // Nothing
        }
    }

    @After
    public void teardown() {
        Game game = Game.getInstance();
        if (game != null) {
            try {
                game.kill();
            } catch (GameException exception) {
                // Nothing
            }
            try {
                game.destroy();
            } catch (GameException exception) {
                // Nothing
            }
        }
    }

    @Test
    public void getInstance() throws Exception {
        assertNotNull(game);
        game.kill();
        game.destroy();
        game = Game.getInstance();
        assertNull(game);
    }

    @Test
    public void create() throws Exception {
        assertNotNull(game);
        assertEquals(game.getState(), GameState.NEW);
    }

    @Test(expected = GameException.class)
    public void badCreate() throws Exception {
        Game.create();
    }

    @Test
    public void createWithMap() throws Exception {
        Game game = Game.getInstance();
        game.kill();
        game.destroy();
        game = Game.create(new MapDatum[20][20]);
        assertNotNull(game);
    }

    @Test(expected = GameException.class)
    public void badCreateWithMap() throws Exception {
        Game.create(new MapDatum[20][40]);
        MapDatum[][] map = Game.getInstance().getMap();
        assertEquals(map.length, 20);
        assertEquals(map[0].length, 40);
    }

    @Test
    public void start() throws Exception {
        game.start();
        assertEquals(game.getState(), GameState.PAUSED);
    }

    @Test(expected = GameException.class)
    public void badStart() throws Exception {
        game.start();
        game.start();
    }

    @Test
    public void run() throws Exception {
        game.start();
        game.run();
        assertEquals(game.getState(), GameState.RUNNING);
        Thread.sleep(game.getTickInterval() + 100, 0);
        assertTrue(game.getTickCounter() > 0);
    }

    @Test(expected = GameException.class)
    public void badRun() throws Exception {
        game.run();
    }

    @Test
    public void pause() throws Exception {
        game.start();
        game.run();
        game.pause();
        assertEquals(game.getState(), GameState.PAUSED);
        long counter = game.getTickCounter();
        Thread.sleep(game.getTickInterval() + 100, 0);
        assertEquals(game.getTickCounter(), counter);
    }

    @Test(expected = GameException.class)
    public void badPause() throws Exception {
        game.pause();
    }

    @Test
    public void killNew() throws Exception {
        game.kill();
    }

    @Test
    public void killRunning() throws Exception {
        game.start();
        game.run();
        game.kill();
    }

    @Test(expected = GameException.class)
    public void badKill() throws Exception {
        game.kill();
        game.kill();
    }

    @Test
    public void destroy() throws Exception {
        game.kill();
        game.destroy();
    }

    @Test(expected = GameException.class)
    public void badDestroy() throws Exception {
        game.destroy();
    }

    @Test
    public void setTickInterval() throws Exception {
        game.setTickInterval(20);
        assertEquals(game.getTickInterval(), 20);
    }

    @Test(expected = GameException.class)
    public void badSetTickInterval() throws Exception{
        game.start();
        game.setTickInterval(20);
    }
}