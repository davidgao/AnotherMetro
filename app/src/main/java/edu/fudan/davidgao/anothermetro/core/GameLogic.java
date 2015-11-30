package edu.fudan.davidgao.anothermetro.core;

import java.util.EnumMap;

import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

class GameLogic implements Runnable {
    private final Broadcaster action;
    private final LogicStage writeBack, internalCallback, externalCallback;
    private final EnumMap<GameEvent, Runnable> alarmMap = new EnumMap<>(GameEvent.class);

    GameLogic() {
        action = new Broadcaster();
        writeBack = new LogicStage();
        internalCallback = new LogicStage();
        externalCallback = new LogicStage();
    }

    @Override
    public synchronized void run() {
        action.run();
        writeBack.run();
        internalCallback.run();
        externalCallback.run();
    }

    private synchronized void initEvent(GameEvent event) {
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
