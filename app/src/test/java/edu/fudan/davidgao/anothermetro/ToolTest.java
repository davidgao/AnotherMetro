package edu.fudan.davidgao.anothermetro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}