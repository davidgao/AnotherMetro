package edu.fudan.davidgao.anothermetro.core;

import edu.fudan.davidgao.anothermetro.tools.*;

public class EventDispatcher extends Broadcaster {
    public EventDispatcher() {
        super();
        addListener(internalBroadcaster);
        addListener(externalBroadcaster);
    }

    public synchronized boolean addInternalListener(Runnable listener) {
        return internalBroadcaster.addListener(listener);
    }

    public synchronized boolean addExternalListener(Runnable listener) {
        return externalBroadcaster.addListener(listener);
    }

    public synchronized Runnable addSnoozeCallback(Runnable callback) {
        SnoozeRunnable snooze = new SnoozeRunnable(callback);
        externalBroadcaster.addListener(snooze.deadline);
        return snooze.alarm;
    }

    Broadcaster internalBroadcaster = new Broadcaster();
    Broadcaster externalBroadcaster = new Broadcaster();
}
