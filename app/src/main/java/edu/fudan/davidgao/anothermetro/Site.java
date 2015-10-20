package edu.fudan.davidgao.anothermetro;

public class Site {
    public Site(Point<Integer> pos, SiteType type) {
        this.pos = pos;
        this.type = type;
    }
    public final Point<Integer> pos;
    public final SiteType type;

    public double dist(int x, int y) {
        final int deltaX = pos.x - x;
        final int deltaY = pos.y - y;
        return Math.sqrt((double)((deltaX * deltaX)+(deltaY * deltaY)));
    }
}
