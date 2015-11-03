package edu.fudan.davidgao.anothermetro;

/* T should be immutable so Rectangle can be immutable */
final class Rectangle<T> {
    public final T x1, x2, y1, y2;

    public Rectangle(T x1, T x2, T y1, T y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
}
