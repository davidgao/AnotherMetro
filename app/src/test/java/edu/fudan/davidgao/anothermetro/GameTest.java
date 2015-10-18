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

    @Test(expected=GameException.class)
    public void badCreate() throws Exception {
        Game.create();
        Game.create();
    }

    @Test
    public void start() throws Exception {
        game.start();
        assertEquals(game.getState(), GameState.PAUSED);
    }

    @Test(expected=GameException.class)
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

    @Test(expected=GameException.class)
    public void badRun() throws Exception {
        game.run();
    }
}