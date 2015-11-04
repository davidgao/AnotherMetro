package edu.fudan.davidgao.anothermetro;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AlgorithmTest {
    /* ROI Generator */
    @Test
    public void roiGenerator() throws Exception {
        int x = 400, y = 300;
        Point<Integer> size = new Point<>(x, y);
        RoiGenerator generator = new RoiGenerator(size, 25, 5);
        Rectangle<Integer> roi = generator.nextRoi();
        assertTrue(roi.x1 == x * 2 / 5);
        assertTrue(roi.x2 == x * 3 / 5);
        assertTrue(roi.y1 == y * 2 / 5);
        assertTrue(roi.y2 == y * 3 / 5);
        generator = new RoiGenerator(size, -1, -1);
        roi = generator.nextRoi();
        assertTrue(roi.x1 == x * 2 / 5);
        assertTrue(roi.x2 == x * 3 / 5);
        assertTrue(roi.y1 == y * 2 / 5);
        assertTrue(roi.y2 == y * 3 / 5);
        generator = new RoiGenerator(size, 25, -1);
        roi = generator.nextRoi();
        assertTrue(roi.x1 == x * 2 / 5);
        assertTrue(roi.x2 == x * 3 / 5);
        assertTrue(roi.y1 == y * 2 / 5);
        assertTrue(roi.y2 == y * 3 / 5);
    }

    @Test(expected = AlgorithmException.class)
    public void badRoiGenerator() throws Exception {
        int x = 400, y = 300;
        RoiGenerator generator = new RoiGenerator(new Point<>(x, y), 25, 26);
        generator.nextRoi();
    }
}