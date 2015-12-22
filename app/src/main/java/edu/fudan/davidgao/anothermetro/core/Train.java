package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

public class Train extends PassengerState {
    TrainState state;
    final ArrayList<Passenger> passengers = new ArrayList<>();

    public Train(Line l) {
        state = new StandbyTrainState(l, l.getSites().get(0), 1);
    }

    public TrainState getState() {
        return state;
    }

    void setState(TrainState state) {
        this.state = state;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Passenger> getPassengers() {
        return (ArrayList<Passenger>)passengers.clone();
    }

    void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }
}
