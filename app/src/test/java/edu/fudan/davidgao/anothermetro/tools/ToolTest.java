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
        assertEquals(runnable.getInterval(), 2);
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
    }

    @Test
    public void emptyBroadcaster() throws Exception {
        Broadcaster broadcaster = new Broadcaster();
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

    /* Matrix2d */
    @Test
    public void matrix2D1() throws Exception {
        Matrix2D<Integer> matrix = new Matrix2D<>(5, 5);
        matrix.set(0, 0, 42);
        assertTrue(matrix.get(0, 0) == 42);
    }

    @Test
    public void matrix2D2() throws Exception {
        Point<Integer> pos = new Point<>(0, 0);
        Matrix2D<Integer> matrix = new Matrix2D<>(new Point<>(5, 5));
        matrix.set(pos, 42);
        assertTrue(matrix.get(pos) == 42);
    }

    @Test
    public void matrix2DFill() throws Exception {
        Matrix2D<Integer> matrix = new Matrix2D<>(new Point<>(5, 5));
        matrix.fill(42);
        assertTrue(matrix.get(0, 0) == 42);
        assertTrue(matrix.get(4, 4) == 42);
    }

    @Test
    public void matrix2DCopy() throws Exception {
        Matrix2D<Integer> matrix1 = new Matrix2D<>(new Point<>(5, 5));
        Matrix2D<Integer> matrix2 = matrix1.copy();
        matrix1.set(0, 0, 42);
        matrix2.set(0, 0, 84);
        assertTrue(matrix1.get(0, 0) == 42);
        assertTrue(matrix2.get(0, 0) == 84);
    }
}