package edu.fudan.davidgao.anothermetro.tools;

public class Matrix2D<T> {
    /* Allocation */
    @SuppressWarnings("unchecked")
    public Matrix2D(Point<Integer> size) {
        data = (T[][])(new Object[size.x][size.y]);
        this.size = size;
    }
    @SuppressWarnings("unchecked")
    public Matrix2D(int x, int y) {
        data = (T[][])(new Object[x][y]);
        this.size = new Point<>(x, y);
    }

    /* Get */
    public synchronized T get(Point<Integer> pos) {
        return data[pos.x][pos.y];
    }
    public synchronized T get(int x, int y) {
        return data[x][y];
    }

    /* Set */
    public synchronized void set(Point<Integer> pos, T object) {
        data[pos.x][pos.y] = object;
    }
    public synchronized void set(int x, int y, T object) {
        data[x][y] = object;
    }

    /* Fill */
    public synchronized void fill(T object) {
        for (T line[]:data) {
            for (int j = 0; j < size.y; j++) {
                line[j] = object;
            }
        }
    }

    /* Copy */
    public synchronized Matrix2D<T> copy() {
        Matrix2D<T> dest = new Matrix2D<>(size);
        for (int i = 0; i < size.x; i++) {
            System.arraycopy(this.data[i], 0, dest.data[i], 0, size.y);
        }
        return dest;
    }

    public final Point<Integer> size;
    private T[][] data;
}
