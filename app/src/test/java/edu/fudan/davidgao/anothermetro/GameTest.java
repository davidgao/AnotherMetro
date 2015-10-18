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
    public void create() throws Exception {
        Game game1 = Game.create();
        assertNotNull(game1);
    }
}