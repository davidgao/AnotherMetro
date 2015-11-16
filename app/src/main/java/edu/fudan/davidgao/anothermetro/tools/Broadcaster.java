package edu.fudan.davidgao.anothermetro.tools;

import java.util.ArrayList;

public class Broadcaster implements Runnable {
    public Broadcaster() {
        listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        for (Runnable listener:listeners) {
            listener.run();
        }
    }

    public boolean addListener(Runnable listener) {
        if (listener == null) return false;
        else return listeners.add(listener);
    }

    public boolean removeListener(Runnable listener) {
        return listeners.remove(listener);
    }

    public void clearListener() {
        listeners.clear();
    }

    private final ArrayList<Runnable> listeners;
}
