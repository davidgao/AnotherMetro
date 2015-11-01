package edu.fudan.davidgao.anothermetro;

public class Rectangle<T> {
    public T x1, x2, y1, y2;

    public Rectangle(T x1, T x2, T y1, T y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public Rectangle(Rectangle<T> base) {
        this.x1 = base.x1;
        this.x2 = base.x2;
        this.y1 = base.y1;
        this.y2 = base.y2;
    }
}
