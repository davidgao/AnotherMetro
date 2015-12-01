package edu.fudan.davidgao.anothermetro.core;

import java.util.EnumMap;

import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

class GameLogic implements Runnable {
    final Broadcaster action;
    final LogicStage writeBack, internalCallback, externalCallback;
    private final EnumMap<GameEvent, Runnable> alarmMap = new EnumMap<>(GameEvent.class);

    GameLogic() {
        action = new Broadcaster();
        writeBack = new LogicStage();
        internalCallback = new LogicStage();
        externalCallback = new LogicStage();
        addEvent(GameEvent.TICK);
        addEvent(GameEvent.GROW);
        addEvent(GameEvent.SITE_SPAWN);
        addEvent(GameEvent.LINE_CHANGE);
        addEvent(GameEvent.TRAIN_STATE_CHANGE);
        action.addListener(getAlarm(GameEvent.TICK));
    }

    @Override
    public synchronized void run() {
        action.run();
        writeBack.run();
        internalCallback.run();
        externalCallback.run();
    }

    private synchronized void addEvent(GameEvent event) {
        Broadcaster bc = new Broadcaster();
        bc.addListener(writeBack.getAlarm(event));
        bc.addListener(internalCallback.getAlarm(event));
        bc.addListener(externalCallback.getAlarm(event));
        alarmMap.put(event, bc);
    }

    synchronized Runnable getAlarm(GameEvent event) {
        return alarmMap.get(event);
    }
}
