package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

public abstract class PassengerState {
    abstract public ArrayList<Passenger> getPassengers();
    abstract void addPassenger(Passenger passenger);
}
