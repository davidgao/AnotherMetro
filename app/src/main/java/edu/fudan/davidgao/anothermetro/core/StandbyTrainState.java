package edu.fudan.davidgao.anothermetro.core;

public final class StandbyTrainState extends TrainState {
    public final Site site;
    long timeToStay;

    public StandbyTrainState(Line line, Site site, int direction) {
        super(line, direction);
        this.site = site;
        timeToStay = 0;
    }
}
