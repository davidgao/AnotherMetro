package edu.fudan.davidgao.anothermetro.core;

public class Passenger {
    public final SiteType type;
    PassengerState state;

    public Passenger(SiteType type, PassengerState state) {
        this.type = type;
        this.state = state;
        state.addPassenger(this);
    }

    public PassengerState getState() {
        return state;
    }

    void setState(PassengerState state) {
        this.state = state;
    }
}
