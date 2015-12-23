package edu.fudan.davidgao.anothermetro;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

import edu.fudan.davidgao.anothermetro.Visual.UpdateLineListener;
import edu.fudan.davidgao.anothermetro.core.GameException;


public class GameView extends GLSurfaceView {

    private final GameRenderer renderer;

    public int view_width, view_height, view_size;
    public float view_offset;

    private static GameView singleton = null;
    public static GameView getInstance(){
        return singleton;
    }

    public GameView(Context context){
        super(context);
        view_width = GameMain.getWidth();
        view_height = GameMain.getHeight();
        view_size = Math.min(view_width, view_height);
        view_offset = (Math.max(view_width, view_height) - view_size) / 2f;

        // Create OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Create and set renderer
        renderer = new GameRenderer();
        setRenderer(renderer);

        // Only render when data is dirty
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        singleton = this;
    }


    public void addUpdateLineListener(UpdateLineListener listener){
        setOnTouchListener(listener);
    }
}
