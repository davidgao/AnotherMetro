package edu.fudan.davidgao.anothermetro;

import android.content.Context;
import android.opengl.GLSurfaceView;

import edu.fudan.davidgao.anothermetro.core.GameException;


public class GameView extends GLSurfaceView {

    private final GameRenderer renderer;

    public int view_width, view_height;

    private static GameView singleton = null;
    public static GameView getInstance(){
        return singleton;
    }

    public GameView(Context context){
        super(context);

        view_width = context.getResources().getDisplayMetrics().widthPixels;
        view_height = context.getResources().getDisplayMetrics().heightPixels;

        // Create OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Create and set renderer
        renderer = new GameRenderer();
        setRenderer(renderer);

        // Only render when data is dirty
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        singleton = this;
    }
}
