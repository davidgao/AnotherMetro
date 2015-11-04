package edu.fudan.davidgao.anothermetro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ToolTest {
    private class TestRunnable implements Runnable {
        @Override
        public void run() {
            runCount += 1;
        }
        public int getRunCount() {
            return runCount;
        }
        public void clearRunCount() {
            runCount = 0;
        }
        private int runCount = 0;
    }

    /* TestRunnable */
    @Test
    public void testRunnable() throws Exception {
        TestRunnable runnable = new TestRunnable();
        assertEquals(runnable.getRunCount(), 0);
        runnable.run();
        assertEquals(runnable.getRunCount(), 1);
        runnable.clearRunCount();
        assertEquals(runnable.getRunCount(), 0);
    }

    /* RunnableTimerTask */
    @Test
    public void newTask() throws Exception {
        TestRunnable runnable = new TestRunnable();
        RunnableTimerTask task = new RunnableTimerTask(runnable);
        task.run();
        assertEquals(runnable.getRunCount(), 1);
    }

    @Test(expected=NullPointerException.class)
    public void badNewTask() throws Exception {
        new RunnableTimerTask(null);
    }

    /* Broadcaster */
    @Test
    public void broadcaster() throws Exception {
        Broadcaster broadcaster = new Broadcaster();
        TestRunnable runnable = new TestRunnable();
        broadcaster.addListener(runnable);
        broadcaster.run();
        assertEquals(runnable.getRunCount(), 1);
        broadcaster.clearListener();
        broadcaster.run();
        assertEquals(runnable.getRunCount(), 1);
        broadcaster.addListener(runnable);
        broadcaster.removeListener(runnable);
        broadcaster.run();
        assertEquals(runnable.getRunCount(), 1);
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