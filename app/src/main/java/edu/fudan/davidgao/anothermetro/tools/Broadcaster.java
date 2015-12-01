package edu.fudan.davidgao.anothermetro.tools;

import java.util.ArrayList;

public class Broadcaster implements Runnable {
    private final ArrayList<Runnable> listeners;

    public Broadcaster() {
        listeners = new ArrayList<>();
    }

    @Override
    public synchronized void run() {
        for (Runnable listener:listeners) {
            listener.run();
        }
    }

    public synchronized boolean addListener(Runnable listener) {
        return listener != null && listeners.add(listener);
    }
/*
    public synchronized boolean removeListener(Runnable listener) {
        return listeners.remove(listener);
    }

    public synchronized void clearListener() {
        listeners.clear();
    }
*/
}
