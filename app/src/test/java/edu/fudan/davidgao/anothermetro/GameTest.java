package edu.fudan.davidgao.anothermetro;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.GameException;
import edu.fudan.davidgao.anothermetro.core.GameState;
import edu.fudan.davidgao.anothermetro.core.MapDatum;

import static org.junit.Assert.*;

public class GameTest {
    private Game game;

    /* Tools */
    @Before
    public void buildup() {
        try{
            game = Game.create(-1, -1);
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
                Game.destroy();
            } catch (GameException exception) {
                // Nothing
            }
        }
    }

    @Test
    public void getInstance() throws Exception {
        assertNotNull(game);
        game.kill();
        Game.destroy();
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
        Game.create(-1, -1);
    }

    @Test
    public void createWithMap() throws Exception {
        Game game = Game.getInstance();
        game.kill();
        Game.destroy();
        game = Game.create(new MapDatum[20][20], -1, -1);
        assertNotNull(game);
    }

    @Test(expected = GameException.class)
    public void badCreateWithMap() throws Exception {
        Game.create(new MapDatum[20][40], -1, -1);
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
        Game.destroy();
    }

    @Test(expected = GameException.class)
    public void badDestroy() throws Exception {
        Game.destroy();
    }

    @Test
    public void setTickInterval() throws Exception {
        game.setTickInterval(20);
        assertEquals(game.getTickInterval(), 20);
    }

    @Test(expected = GameException.class)
    public void badSetTickInterval() throws Exception {
        game.start();
        game.setTickInterval(20);
    }

    @Test
    public void initSiteSpawn() throws Exception {
        game.start();
        ArrayList<Site> sites = game.getSites();
        assertEquals(sites.size(), 3);
    }

    @Test
    public void spawnSite() throws Exception {
        game.setTickInterval(10);
        game.setSiteSpawnInterval(10);
        game.start();
        game.run();
        Thread.sleep(1000, 0);
        game.pause();
        ArrayList<Site> sites = game.getSites();
        assertTrue(sites.size() > 3);
    }
}