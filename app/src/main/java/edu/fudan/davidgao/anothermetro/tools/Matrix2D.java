package edu.fudan.davidgao.anothermetro.tools;

import java.util.ArrayList;

public class Matrix2D<T> {
    /* Construction */
    public Matrix2D(Point<Integer> size, T object){
        refill(size.x, size.y, object);
    }
    public Matrix2D(int x, int y, T object){
        refill(x, y, object);
    }

    /* Get */
    public T get(Point<Integer> pos) {
        return get(pos.x, pos.y);
    }
    public T get(int x, int y) {
        return data.get(x).get(y);
    }

    /* Set */
    public void set(Point<Integer> pos, T object) {
        set(pos.x, pos.y, object);
    }
    public void set(int x, int y, T object) {
        data.get(x).set(y, object);
    }

    /* Fill up */
    private void refill(int x, int y, T object){
        /* Construct baseline */
        ArrayList<T> baseline = new ArrayList<>();
        for (int i = 0; i < y; i++) {
            baseline.add(object);
        }
        /* Construct data array */
        data = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            ArrayList<T> line = new ArrayList<>();
            line.addAll(baseline);
            data.add(line);
        }
    }

    private ArrayList<ArrayList<T>> data;
}
