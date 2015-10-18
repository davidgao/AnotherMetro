package edu.fudan.davidgao.anothermetro;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {
    @Test(expected=AssertionError.class)
    public void badAssertion() throws Exception {
        assertTrue(false);
    }

    @Test
    public void goodAssertion() throws Exception {
        assertTrue(true);
    }

    @Test
    public void getInstance() throws Exception {
        Game game = Game.getInstance();
        assertNull(game);
        Game.create();
        game = Game.getInstance();
        assertNotNull(game);
        game.kill();
        game.destroy();
        game = Game.getInstance();
        assertNull(game);
    }
}