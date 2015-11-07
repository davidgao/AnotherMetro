package edu.fudan.davidgao.anothermetro.tools;

public class Counter implements Runnable {
    @Override
    public void run() {
        counter += 1;
    }
    public long getCounter() {
        return counter;
    }
    private long counter = 0;
}
