package edu.fudan.davidgao.anothermetro.core;

import java.util.EnumMap;

import edu.fudan.davidgao.anothermetro.tools.*;

class LogicStage implements Runnable {
    private final EnumMap<GameEvent, Broadcaster> broadcasterMap = new EnumMap<>(GameEvent.class);
    private final EnumMap<GameEvent, Runnable> alarmMap = new EnumMap<>(GameEvent.class);
    private final Broadcaster mainBroadcaster = new Broadcaster();

    LogicStage() {
        addEvent(GameEvent.TICK);
        addEvent(GameEvent.GROW);
        addEvent(GameEvent.SITE_SPAWN);
        addEvent(GameEvent.LINE_CHANGE);
        addEvent(GameEvent.TRAIN_STATE_CHANGE);
    }

    @Override
    public synchronized void run() {
        mainBroadcaster.run();
    }

    synchronized Broadcaster getBroadcaster(GameEvent event) {
        return broadcasterMap.get(event);
    }

    synchronized Runnable getAlarm(GameEvent event) {
        return alarmMap.get(event);
    }

    private synchronized void addEvent(GameEvent event) {
        Broadcaster bc = new Broadcaster();
        SnoozeRunnable snooze = new SnoozeRunnable(bc);
        broadcasterMap.put(event, bc);
        alarmMap.put(event, snooze.alarm);
        mainBroadcaster.addListener(snooze.deadline);
    }
}
