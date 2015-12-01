package edu.fudan.davidgao.anothermetro.tools;

public class IntervalRunnable implements Runnable {
    public IntervalRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public synchronized long getInterval() {
        return interval;
    }
    public synchronized void setInterval(long interval) {
        next = this.interval = interval;
        counter = 0;
    }

    @Override
    public synchronized void run() {
        counter += 1;
        if (counter >= next) {
            counter -= interval;
            runnable.run();
        }
    }

    private Runnable runnable;
    private long interval = 1, next = 1, counter = 0;
}
