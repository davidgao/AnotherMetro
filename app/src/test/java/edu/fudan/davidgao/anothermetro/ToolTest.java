package edu.fudan.davidgao.anothermetro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
        assertEquals(point1.x.intValue(), 1);
        assertEquals(point1.y.intValue(), 2);
        point1.x = 3;
        point1.y = 4;
        assertEquals(point1.x.intValue(), 3);
        assertEquals(point1.y.intValue(), 4);
        Point<Integer> point2 = new Point<>(point1);
        assertNotEquals(point1, point2);
        assertEquals(point1.x, point2.x);
        assertEquals(point1.y, point2.y);
    }

    /* Rectangle */
    @Test
    public void rectangle() throws Exception {
        Rectangle<Integer> rectangle1 = new Rectangle<>(1, 2, 3, 4);
        assertEquals(rectangle1.x1.intValue(), 1);
        assertEquals(rectangle1.x2.intValue(), 2);
        assertEquals(rectangle1.y1.intValue(), 3);
        assertEquals(rectangle1.y2.intValue(), 4);
        rectangle1.x1 = 5;
        rectangle1.x2 = 6;
        rectangle1.y1 = 7;
        rectangle1.y2 = 8;
        assertEquals(rectangle1.x1.intValue(), 5);
        assertEquals(rectangle1.x2.intValue(), 6);
        assertEquals(rectangle1.y1.intValue(), 7);
        assertEquals(rectangle1.y2.intValue(), 8);
        Rectangle<Integer> rectangle2 = new Rectangle<>(rectangle1);
        assertNotEquals(rectangle1, rectangle2);
        assertEquals(rectangle1.x1, rectangle2.x1);
        assertEquals(rectangle1.x2, rectangle2.x2);
        assertEquals(rectangle1.y1, rectangle2.y1);
        assertEquals(rectangle1.y2, rectangle2.y2);
    }
}