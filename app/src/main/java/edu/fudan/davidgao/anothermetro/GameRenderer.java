package edu.fudan.davidgao.anothermetro;

import android.opengl.*;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import edu.fudan.davidgao.anothermetro.Visual.DrawLine;
import edu.fudan.davidgao.anothermetro.Visual.DrawLineHead;
import edu.fudan.davidgao.anothermetro.Visual.DrawPassenger;
import edu.fudan.davidgao.anothermetro.Visual.DrawSite;
import edu.fudan.davidgao.anothermetro.Visual.TextManager;
import edu.fudan.davidgao.anothermetro.Visual.TextObject;
import edu.fudan.davidgao.anothermetro.Visual.TrainRenderer;
import edu.fudan.davidgao.anothermetro.Visual.UpdateLineListener;
import edu.fudan.davidgao.anothermetro.core.Game;
import edu.fudan.davidgao.anothermetro.core.GameException;
import edu.fudan.davidgao.anothermetro.core.Site;

public class GameRenderer implements GLSurfaceView.Renderer {

    private DrawLine drawLine;
    private DrawLineHead drawLineHead;
    private DrawPassenger drawPassenger;
    private DrawSite drawSite;
    private TrainRenderer drawTrain;
    private UpdateLineListener updateLineListener;
    private TextManager textManager;

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        try {
            Game.getInstance().start();
            Game.getInstance().run();
        } catch(GameException e) {
            e.printStackTrace();
        }
        drawLine = new DrawLine();
        drawSite = new DrawSite();
        drawLineHead= new DrawLineHead();
        drawTrain = new TrainRenderer();
        drawPassenger = new DrawPassenger();
        updateLineListener = new UpdateLineListener(drawLineHead);
        textManager = new TextManager(new TextObject("Touched", -0.2f, 0.2f), 1f);
        try {
            ArrayList<Site> temp_sites =  Game.getInstance().getSites();
            Game.getInstance().addLine(temp_sites.get(0), temp_sites.get(1));
            GameView.getInstance().addUpdateLineListener(updateLineListener);
        } catch (GameException e){
            e.printStackTrace();
        }
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        updateLineListener.setMatrix(mMVPMatrix);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        drawLine.draw(mMVPMatrix);
        drawLineHead.draw(mMVPMatrix);
        drawTrain.render(mMVPMatrix);
        drawPassenger.draw(mMVPMatrix);
        updateLineListener.draw(mMVPMatrix);
        drawSite.draw(mMVPMatrix);
        textManager.Draw(mMVPMatrix);
    }

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) height / width;
        Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);
    }
}