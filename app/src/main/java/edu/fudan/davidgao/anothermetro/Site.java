package edu.fudan.davidgao.anothermetro;

public class Site {
    public Site(int x, int y, SiteType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    public final int x, y;
    public final SiteType type;

    public double dist(int x, int y) {
        final int deltaX = this.x - x;
        final int deltaY = this.y - y;
        return Math.sqrt((double)((deltaX * deltaX)+(deltaY * deltaY)));
    }
}
