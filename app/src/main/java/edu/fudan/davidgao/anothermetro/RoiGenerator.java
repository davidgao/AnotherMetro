package edu.fudan.davidgao.anothermetro;

final class RoiGenerator {
    public RoiGenerator(Point<Integer> size, int levels, int startLevel) {
        this.size = new Point<>((double)size.x, (double)size.y);
        this.levels = levels;
        if (startLevel < 0) {
            nextLevel = levels / 5;
        } else {
            nextLevel = startLevel;
        }
        renewRate();
    }

    public final synchronized Rectangle<Integer> nextRoi() throws AlgorithmException {
        if (nextLevel > levels) throw new AlgorithmException("ROI beyond size");
        int x1, x2, y1, y2;
        x1 = (int)(size.x * rate1);
        x2 = (int)(size.x * rate2);
        y1 = (int)(size.y * rate1);
        y2 = (int)(size.y * rate2);
        Rectangle<Integer> roi = new Rectangle<>(x1, x2, y1, y2);
        levels += 1;
        renewRate();
        return roi;
    }

    private void renewRate() {
        double tmp_rate = (double)nextLevel / (double)levels * 0.5d;
        rate1 = 0.5 - tmp_rate;
        rate2 = 0.5 + tmp_rate;
    }

    private Point<Double> size;
    private int levels, nextLevel;
    private double rate1, rate2;
}
