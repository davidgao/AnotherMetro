package edu.fudan.davidgao.anothermetro;

public class Site {
    public Site(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    public final int x, y, type;

    public double dist(int x, int y) {
        final int deltax = this.x - x;
        final int deltay = this.y - y;
        return Math.sqrt((double)((deltax * deltax)+(deltay * deltay)));
    }
}
