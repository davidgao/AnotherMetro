package edu.fudan.davidgao.anothermetro;

import android.content.Context;
import android.opengl.GLSurfaceView;


public class GameView extends GLSurfaceView {

    private final GameRenderer renderer;

    public GameView(Context context){
        super(context);

        // Create OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Create and set renderer
        renderer = new GameRenderer();
        setRenderer(renderer);

        // Only render when data is dirty
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
