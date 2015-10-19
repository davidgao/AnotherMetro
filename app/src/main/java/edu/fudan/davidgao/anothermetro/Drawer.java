package edu.fudan.davidgao.anothermetro;

import android.graphics.Canvas;

/**
 * Created by gq on 10/19/15.
 */
public class Drawer {
    private static Drawer ourInstance = new Drawer();

    public static Drawer getInstance() {
        return ourInstance;
    }

    private Drawer() {
    }

    public static void drawMap(Canvas canvas, MapDatum[][] map) {
    }

    public static void drawSite(Canvas canvas, Site site) {
    }

    // NOTE: should retrieve color from GameConfiguration.getColor()
    public static void drawLine(Canvas canvas, Line line) {
    }
}
