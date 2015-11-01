package edu.fudan.davidgao.anothermetro;

import java.util.ArrayList;

class Broadcaster implements Runnable {
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
        return listeners.add(listener);
    }

    public boolean removeListener(Runnable listener) {
        return listeners.remove(listener);
    }

    public void clearListener() {
        listeners.clear();
    }

    private final ArrayList<Runnable> listeners;
}
