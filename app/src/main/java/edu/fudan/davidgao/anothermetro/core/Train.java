package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

public class Train implements PassengerState {
    private TrainState state;
    private ArrayList<Passenger> passengers;

    //TODO: getPassengers()
    public ArrayList<Passenger> getPassengers(){
        return null;
    }

    public Train(Line l) {
        state = new StandbyTrainState(l, l.getSites().get(0), 1);
    }

    public TrainState getState() {
        return state;
    }

    void setState(TrainState state) {
        this.state = state;
    }
}
