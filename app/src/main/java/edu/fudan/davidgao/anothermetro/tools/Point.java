package edu.fudan.davidgao.anothermetro.tools;

/* T should be immutable so Point can be immutable */
public final class Point<T> {
    public final T x, y;

    public Point(T x, T y) {
        this.x = x;
        this.y = y;
    }
}
