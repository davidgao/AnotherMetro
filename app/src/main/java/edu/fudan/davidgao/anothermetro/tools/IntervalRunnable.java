package edu.fudan.davidgao.anothermetro.tools;

public class IntervalRunnable implements Runnable {
    public IntervalRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public synchronized void setInterval(long interval) {
        next = this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }

    @Override
    public synchronized void run() {
        counter += 1;
        if (counter >= next) {
            next += interval;
            runnable.run();
        }
    }

    private Runnable runnable;
    private long interval = 1, next = 1, counter = 0;
}
