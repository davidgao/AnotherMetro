package edu.fudan.davidgao.anothermetro;

/* T should be immutable */
class Point<T> {
    public T x, y;

    public Point(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point<T> base) {
        x = base.x;
        y = base.y;
    }
}
