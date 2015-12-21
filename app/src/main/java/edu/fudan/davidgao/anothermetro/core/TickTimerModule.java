package edu.fudan.davidgao.anothermetro.core;

import java.util.Timer;

import edu.fudan.davidgao.anothermetro.tools.*;

class TickTimerModule implements GameModule {
    long interval;

    protected Timer timer;
    protected RunnableTimerTask task;
    protected Counter counter;

    public void init(GameLogic logic) {
        interval = IGame2.defaultTickInterval;
        task = new RunnableTimerTask(logic);
        timer = new Timer();
        counter = new Counter();
        logic.action.addListener(counter);
    }

    public void start() {

    }

    public void run() {
        timer.schedule(task, interval, interval);
    }

    public void pause() {
        timer.cancel();
    }

    public long getCounter() {
        return counter.getCounter();
    }

    public void setCounter(long count) {
        counter.setCounter(count);
    }

    public void clearCounter() {
        counter.clearCounter();
    }
}
