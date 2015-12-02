package edu.fudan.davidgao.anothermetro.Visual;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.Line;
import edu.fudan.davidgao.anothermetro.core.RunningTrainState;
import edu.fudan.davidgao.anothermetro.core.Site;
import edu.fudan.davidgao.anothermetro.core.Game;
import edu.fudan.davidgao.anothermetro.core.GameEvent;
import edu.fudan.davidgao.anothermetro.core.StandbyTrainState;
import edu.fudan.davidgao.anothermetro.core.Train;
import edu.fudan.davidgao.anothermetro.core.TrainState;
import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

/**
 * Created by 凯强 on 2015/12/1.
 */
public class TrainRenderer {
    private static final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "attribute vec4 vPosition;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = vPosition;" +
            "}";
    // TODO: later change color according to line
    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private float[] vertexCoords;
    private int vertexCount = 0;
    private static TrainRenderer instance;

    public static TrainRenderer getInstance() {
        return instance;
    }

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private Game game;

    public TrainRenderer() {
        game = Game.getInstance();
        vertexCoords = new float[3 * 4 * Config.MAX_SEGMENTS];
        Broadcaster trainUpdateBroadcaster = game.getCallbackBroadcaster(GameEvent.TRAIN_STATE_CHANGE);
        trainUpdateBroadcaster.addListener(trainUpdateRunnable);

        ByteBuffer bb = ByteBuffer.allocateDirect(
                //(number of coordinate values * 4 byte per float)
                vertexCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        //create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(vertexCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shader and OpenGL program
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader); // add the vertex fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);

        // Forcefully update the lines, sites and trains member so that they are initialized
        trainUpdate();

        // Startup a train drawing thread which repeatedly updates train vertex buffer
        Thread thTrainDraw = new Thread(trainDrawingRunnable);
        thTrainDraw.start();
        instance = this;
    }


    public void render(){

        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // Get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the train
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private static  final double size =  0.02;


    private synchronized void addVertex(double x, double y, double high) {
        vertexCoords[vertexCount ++] = (float) x;
        vertexCoords[vertexCount ++] = (float) y;
        vertexCoords[vertexCount ++] = (float) high;
    }

    private synchronized void addVertexPlusVectorWithAngle(double cx, double cy, double dx, double dy, double angle, double high)
    {
        double ndx = dx * Math.cos(angle) - dy * Math.sin(angle);
        double ndy = dx * Math.sin(angle) + dy * Math.cos(angle);
        addVertex(cx + ndx, cy + ndy, high);
    }

    private synchronized void addTrain(double x, double y, double rad ,double high){
        // Lower Triangle
        addVertexPlusVectorWithAngle(x, y, 3 * size, 2 * size, rad, high);
        addVertexPlusVectorWithAngle(x, y, -3 * size, -2 * size, rad, high);
        addVertexPlusVectorWithAngle(x, y, 3 * size, -2 * size, rad, high);

        // Upper Triangle
        addVertexPlusVectorWithAngle(x, y, -3 * size, -2 * size, rad,high);
        addVertexPlusVectorWithAngle(x, y, -3 * size, 2 * size, rad, high);
        addVertexPlusVectorWithAngle(x, y, 3 * size, 2 * size, rad, high);
    }

    private void addTrain(double x, double y, int deg, double high){
        addTrain(x,y,Math.PI/4 * deg, high);
    }

    private ArrayList<Line> lines = null;
    private ArrayList<Site> sites = null;
    private ArrayList<Train> trains = null;
    private long tickCounter;
    private Runnable trainUpdateRunnable = new Runnable() {
        public void run() {
            trainUpdate();
        }
    };

    private synchronized void trainUpdate() {
        System.out.println("Train update");
        lines = game.getLines();
        sites = game.getSites();
        trains = new ArrayList<>();
        for (int i = 0; i < lines.size(); ++i) {
            trains.add(lines.get(i).train);
        }
    }

    /* This happens at every frame */
    /* davidgao: FIXME
     * Please do not use tight loops for drawing. Please use openGL's way to "always render".
     */
    private Runnable trainDrawingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                for (; ; ) {
                    tickCounter = game.getTickCounter();
                    synchronized (instance) {
                        refresh();
                    }
                    Thread.sleep(16);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /* davidgao: FIXME
     * This is a tool function, should be implemented as public VsTrainState(TrainState)
     * or something.
     */
    private VsTrainState transformTrainToVsTrain(Train train)
    {
        TrainState state = train.getState();
        VsTrainState vsTrainState;
        if (state instanceof StandbyTrainState)
        {
            StandbyTrainState standbyTrainState = (StandbyTrainState)state;
            Site site = standbyTrainState.site;
            VsSite vsSite = new VsSite(site);
            vsTrainState = new VsTrainState(vsSite.pos, 0);
        } else if (state instanceof RunningTrainState)
        {
            RunningTrainState runningTrainState = (RunningTrainState)state;
            VsSegment vsSegment;
            //if (state.direction==1) {
                vsSegment = DrawLine.getInstance().findSegment(runningTrainState.s1, runningTrainState.s2, state.line);
            //}else{
            //    vsSegment = DrawLine.getInstance().findSegment(runningTrainState.s2, runningTrainState.s1, state.line);
            //}
            long timePeriod = runningTrainState.arrival - runningTrainState.departure;
            long timePassed = tickCounter - runningTrainState.departure;
            System.out.printf("%d %d\n", timePeriod, timePassed);
            double fraction = (double)timePassed / (double) timePeriod;
            vsTrainState = vsSegment.getTrainState(fraction, state.direction);
        } else vsTrainState = null;
        return vsTrainState;
    }

    private ArrayList<VsTrainState> transformTrainToVsTrain(ArrayList<Train> trains) {
        int len = trains.size();
        ArrayList<VsTrainState> VsTrains = new ArrayList<VsTrainState>();
        for (int i = 0; i < len; ++i) {
            Train train = trains.get(i);
            VsTrains.add(transformTrainToVsTrain(train));
        }
        return VsTrains;
    }

    private void refresh(){
        vertexCount = 0;
        ArrayList<VsTrainState> VsTrains;
        synchronized (this) {
                VsTrains = transformTrainToVsTrain(trains);
        }
        int len = VsTrains.size();
        for (int i = 0; i < len; ++ i)
        {
            addTrain(VsTrains.get(i).coordinate.x,VsTrains.get(i).coordinate.y,VsTrains.get(i).angle,1.00f);
        }
        vertexBuffer.put(vertexCoords);
        vertexBuffer.position(0);
    }
}