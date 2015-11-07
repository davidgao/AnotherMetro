package edu.fudan.davidgao.anothermetro.core;

import edu.fudan.davidgao.anothermetro.tools.Point;
import edu.fudan.davidgao.anothermetro.tools.Rectangle;

final class RoiGenerator {
    public RoiGenerator(Point<Integer> size, int maxGrowth, int baseGrowth) {
        this.size = new Point<>((double)size.x, (double)size.y);
        if (maxGrowth <= 0) {
            this.maxGrowth = 25;
        } else {
            this.maxGrowth = maxGrowth;
        }
        if (baseGrowth < 0) {
            nextGrowth = this.maxGrowth / 5;
        } else {
            nextGrowth = baseGrowth;
        }
        renewRate();
    }

    public final synchronized Rectangle<Integer> nextRoi() throws AlgorithmException {
        if (nextGrowth > maxGrowth) throw new AlgorithmException("ROI grows beyond size");
        int x1, x2, y1, y2;
        x1 = (int)(size.x * rate1);
        x2 = (int)(size.x * rate2);
        y1 = (int)(size.y * rate1);
        y2 = (int)(size.y * rate2);
        Rectangle<Integer> roi = new Rectangle<>(x1, x2, y1, y2);
        maxGrowth += 1;
        renewRate();
        return roi;
    }

    private void renewRate() {
        double tmp_rate = (double)nextGrowth / (double)maxGrowth * 0.5d;
        rate1 = 0.5 - tmp_rate;
        rate2 = 0.5 + tmp_rate;
    }

    private Point<Double> size;
    private int maxGrowth, nextGrowth;
    private double rate1, rate2;
}
