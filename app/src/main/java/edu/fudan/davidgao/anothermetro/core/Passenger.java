package edu.fudan.davidgao.anothermetro.core;

public class Passenger {
    public final int type;
    private PassengerState state;

    public Passenger(int type, PassengerState state) {
        this.type = type;
        this.state = state;
    }

    public PassengerState getState() {
        return state;
    }

    void setState(PassengerState state) {
        this.state = state;
    }
}
