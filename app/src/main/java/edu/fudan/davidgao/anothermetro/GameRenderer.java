package edu.fudan.davidgao.anothermetro;

import android.opengl.*;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import edu.fudan.davidgao.anothermetro.Visual.DrawLine;
import edu.fudan.davidgao.anothermetro.Visual.DrawLineHead;
import edu.fudan.davidgao.anothermetro.Visual.DrawPassenger;
import edu.fudan.davidgao.anothermetro.Visual.DrawSite;
import edu.fudan.davidgao.anothermetro.Visual.DrawTrain;
import edu.fudan.davidgao.anothermetro.Visual.UpdateLineListener;

public class GameRenderer implements GLSurfaceView.Renderer {

    private DrawLine drawLine;
    //private DrawLineHead drawLineHead = new DrawLineHead();
    //private DrawPassenger drawPassenger = new DrawPassenger();
    private DrawSite drawSite;
    //private DrawTrain drawTrain = new DrawTrain();
    //private UpdateLineListener updateLineListener = new UpdateLineListener();

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawLine = new DrawLine();
        drawSite = new DrawSite();
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        drawLine.draw();
        //drawLineHead.draw();
        //drawPassenger.draw();
        drawSite.draw();
        //drawTrain.draw();
        //updateLineListener.draw();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
}