package edu.fudan.davidgao.anothermetro;

class IntervalRunnable implements Runnable {
    public IntervalRunnable(Runnable runnable, long interval) {
        this.runnable = runnable;
        this.interval = interval;
        this.next = interval;
    }

    @Override
    public void run() {
        counter += 1;
        if (counter >= next) {
            next += interval;
            runnable.run();
        }
    }

    private Runnable runnable;
    private long interval, next, counter = 0;
}
