package edu.fudan.davidgao.anothermetro;

/* T should be immutable so Point can be immutable */
final class Point<T> {
    public final T x, y;

    public Point(T x, T y) {
        this.x = x;
        this.y = y;
    }
}
