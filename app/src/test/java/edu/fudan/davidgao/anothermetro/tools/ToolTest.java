package edu.fudan.davidgao.anothermetro.tools;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ToolTest {
    /* Counter */
    @Test
    public void testRunnable() throws Exception {
        Counter counter = new Counter();
        assertEquals(counter.getCounter(), 0);
        counter.run();
        assertEquals(counter.getCounter(), 1);
        counter.clearCounter();
        assertEquals(counter.getCounter(), 0);
    }

    /* IntervalRunnable */
    @Test
    public void intervalRunnable() throws Exception {
        Counter counter = new Counter();
        IntervalRunnable runnable = new IntervalRunnable(counter);
        runnable.setInterval(2);
        runnable.run();
        assertEquals(counter.getCounter(), 0);
        runnable.run();
        assertEquals(counter.getCounter(), 1);
    }

    /* RunnableTimerTask */
    @Test
    public void newTask() throws Exception {
        Counter runnable = new Counter();
        RunnableTimerTask task = new RunnableTimerTask(runnable);
        task.run();
        assertEquals(runnable.getCounter(), 1);
    }

    @Test(expected=NullPointerException.class)
    public void badNewTask() throws Exception {
        new RunnableTimerTask(null);
    }

    /* Broadcaster */
    @Test
    public void broadcaster() throws Exception {
        Broadcaster broadcaster = new Broadcaster();
        Counter runnable = new Counter();
        broadcaster.addListener(runnable);
        broadcaster.run();
        assertEquals(runnable.getCounter(), 1);
        broadcaster.clearListener();
        broadcaster.run();
        assertEquals(runnable.getCounter(), 1);
        broadcaster.addListener(runnable);
        broadcaster.removeListener(runnable);
        broadcaster.run();
        assertEquals(runnable.getCounter(), 1);
    }

    @Test
    public void emptyBroadcaster() throws Exception {
        Broadcaster broadcaster = new Broadcaster();
        broadcaster.addListener(null);
        broadcaster.removeListener(null);
        broadcaster.run();
    }

    /* Point */
    @Test
    public void point() throws Exception {
        Point<Integer> point1 = new Point<>(1, 2);
        assertTrue(point1.x == 1);
        assertTrue(point1.y == 2);
    }

    /* Rectangle */
    @Test
    public void rectangle() throws Exception {
        Rectangle<Integer> rectangle1 = new Rectangle<>(1, 2, 3, 4);
        assertTrue(rectangle1.x1 == 1);
        assertTrue(rectangle1.x2 == 2);
        assertTrue(rectangle1.y1 == 3);
        assertTrue(rectangle1.y2 == 4);
    }
}