package edu.fudan.davidgao.anothermetro.core;

public class RunningTrainState extends TrainState {
    public final Site s1, s2;
    public final long departure, arrival;

    public RunningTrainState(Line line, Site s1, Site s2, int direction, long departure, long arrival) {
        super(line, direction);
        this.s1 = s1;
        this.s2 = s2;
        this.departure = departure;
        this.arrival = arrival;
    }
}
