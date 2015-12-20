package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.tools.Point;

public class Site extends PassengerState {
    final ArrayList<Passenger> passengers = new ArrayList<>();
    final ArrayList<Line> lines = new ArrayList<>();
    public Site(Point<Integer> pos, SiteType type) {
        this.pos = pos;
        this.type = type;
    }
    public final Point<Integer> pos;
    public final SiteType type;

    public double dist(Point<Integer> pos) {
        return dist(pos.x, pos.y);
    }

    public double dist(int x, int y) {
        final int deltaX = pos.x - x;
        final int deltaY = pos.y - y;
        return Math.sqrt((double)((deltaX * deltaX)+(deltaY * deltaY)));
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Passenger> getPassengers() {
        return (ArrayList<Passenger>)passengers.clone();
    }

    void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }
}
