package edu.fudan.davidgao.anothermetro.core;

public abstract class TrainState {
    public final Line line;
    public final int direction;

    TrainState(Line line, int direction) {
        this.line = line;
        this.direction = direction;
    }
}
