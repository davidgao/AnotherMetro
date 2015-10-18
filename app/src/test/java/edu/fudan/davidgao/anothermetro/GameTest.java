package edu.fudan.davidgao.anothermetro;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {
    private Game game;

    @Test(expected=AssertionError.class)
    public void badAssertion() throws Exception {
        assertTrue(false);
    }

    @Test
    public void getWithNoInstance() throws Exception {
        game = Game.getInstance();
        assertNull(game);
    }
}