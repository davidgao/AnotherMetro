package edu.fudan.davidgao.anothermetro;

/**
 * Created by gq on 10/19/15.
 */
public class Game {
    private static Game ourInstance = new Game();

    public static Game getInstance() {
        return ourInstance;
    }

    private Game() {
    }

    public MapDatum[][] getMap() {
        return null;
    }

    public Site[] getSites() {
        return null;
    }

    public Line[] getLines() {
        return null;
    }
}