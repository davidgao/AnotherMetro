package edu.fudan.davidgao.anothermetro;

import android.graphics.Color;

/**
 * Created by gq on 10/19/15.
 */
public class GameConfiguration {
    private static GameConfiguration ourInstance = new GameConfiguration();

    public static GameConfiguration getInstance() {
        return ourInstance;
    }

    /** Refresh rate (fps) */
    private static int mRefreshRate = 60;

    private static int[] mColorMap = {
            Color.LTGRAY,
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.GRAY,
            Color.YELLOW,
            Color.MAGENTA,
            Color.CYAN,
            Color.DKGRAY,
            Color.rgb(128, 0, 0),
            Color.rgb(0, 128, 0),
            Color.rgb(0, 0, 128)
    };

    private GameConfiguration() {
    }

    public static int getRefreshRate() {
        return mRefreshRate;
    }

    public static int getRefreshInterval() {
        return 1000 / mRefreshRate;
    }

    public static int getMaximumLines() {
        return mColorMap.length;
    }

    public static int getLineColor(int i) {
        return mColorMap[i];
    }
}
