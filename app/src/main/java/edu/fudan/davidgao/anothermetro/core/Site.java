package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.tools.Point;

public class Site implements PassengerState {
    public Site(Point<Integer> pos, SiteType type) {
        this.pos = pos;
        this.type = type;
    }
    public final Point<Integer> pos;
    public final SiteType type;

    // TODO: getPassengers()
    public ArrayList<Passenger> getPassengers() {
        return null;
    }

    public double dist(Point<Integer> pos) {
        return dist(pos.x, pos.y);
    }

    public double dist(int x, int y) {
        final int deltaX = pos.x - x;
        final int deltaY = pos.y - y;
        return Math.sqrt((double)((deltaX * deltaX)+(deltaY * deltaY)));
    }
}
