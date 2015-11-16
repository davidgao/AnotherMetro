package edu.fudan.davidgao.anothermetro.tools;

public class Counter implements Runnable {
    @Override
    public void run() {
        counter += 1;
    }
    public long getCounter() {
        return counter;
    }
    public void clearCounter() {
        counter = 0;
    }
    private long counter = 0;
}
