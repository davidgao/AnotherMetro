package edu.fudan.davidgao.anothermetro.Visual;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.Line;
import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.core.Game;
import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

/**
 * Created by 凯强 on 2015/12/1.
 */
public class DrawTrain {
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    // number of coordinates per vertex in this array
    static final int  COORDS_PRE_VERTEX = 4;
    private float[] vertexCoords;
    private int vertexCount = 0;

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

    public DrawTrain() {
        game = Game.getInstance();
        vertexCoords = new float[3 * 4 * Config.MAX_SEGMENTS];
        Broadcaster broadcaster = game.getCallbackBroadcaster(GameEvent.TICK);
        broadcaster.addListener(TrainRefresh_RUNABLE());

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
        vertexBuffer.position();

        // prepare shader and OpenGL program
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader); // add the vertex fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
    }


    public void draw(){

        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // Get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PRE_VERTEX, GLES20.GL_FLOAT, flase, vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.gluniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        checkGlError("glUniformMatrix4fv");

        // Draw the train
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private static  final double size =  0.05;


    private void addVertex(double x, double y, double high) {
        vertexCoords[vertexCount ++] = (float) x;
        vertexCoords[vertexCount ++] = (float) y;
        vertexCoords[vertexCount ++] = (float) high;
    }

    private void addVertexPlusVectorWithAngle(double cx, double cy, double dx, double dy, double angle, double high)
    {
        double ndx = dx * Math.cos(angle) - dy * Math.sin(angle);
        double ndy = dx * Math.sin(angle) + dy * Math.cos(angle);
        addVertex(cx + ndx, cy + ndy, high);
    }

    private void addTrain(double x, double y, double angle ,double high){
        //Down Triangle
        addVertexPlusVectorWithAngle(x,y,3*size,2*size,angle, high);
        addVertexPlusVectorWithAngle(x,y,-3*size,-2*size,angle, high);
        addVertexPlusVectorWithAngle(x,y,3*size,-2*size,angle, high);

        //Up Triangle
        addVertexPlusVectorWithAngle(x, y, -3*size, -2*size, angle,high);
        addVertexPlusVectorWithAngle(x, y, -3*size, 2*size, angle, high);
        addVertexPlusVectorWithAngle(x, y, 3 * size, 2 * size, angle, high);
    }

    private void addTrain(double x, double y, int angle, double high){
        addTrain(x,y,Math.PI/4 * angle, high);
    }

    private ArrayList<Line> lines;
    private ArrayList<Site> sites;
    private ArrayList<Train> trains;
    private ArrayList<TrainState> trainStates;
    private long tickCounter;
    private Runnable TrainRefresh_RUNABLE = new Runnable() {
        public void run() {
            synchronized (this) {
                lines = game.getLines();
                sites = game.getSites();
                trains = game.getTrains();
                trainStates = game.getTrainStates();
                tickCounter = game.getTickCounter();
            }
        }
    };

    private VsTrainState transformTrainToVsTrain(Train train)
    {
        TrainState state = train.getTrainState();
        VsTrainState vsTrainState;
        if (state instanceof StandbyTrainState)
        {
            Site site = state.site;
            VsSite vsSite = new VsSite(site);
            vsTrainState = new VsTrainState(vsSite.pos, 0);
        } else if (state instanceof RunningTrainState)
        {
            VsSegment vsSegment = new VsSegment(state.s1, state.s2, new VsLine(state.line));
            long timePeriod = state.arrival - state.departure;
            long timePassed = tickCounter - state.departure;
            fraction = (double)timePassed / (double) timePeriod;
            vsTrainState = vsSegment.getTrainState(fraction, state.direction);
        } else vsTrainState = null;
        return vsTrainState;
    }

    private ArrayList<VsTrainState> transformTrainToVsTrain(ArrayList<Train> trains) {
        int len = trains.size();
        ArrayList<VsTrainState> VsTrains = new ArrayList<VsTrainState>();
        for (int i = 0; i < len; ++i) {
            VsTrains.add(transformTrainToVsTrain(train));
        }
        return VsTrains;
    }

    private void drawer(){
        synchronized (this) {
                ArrayList<VsTrainState> VsTrains = transformTrainToVsTrain(trains);
        }
        int len = VsTrains.size();
        for (int i = 0; i < len; ++ i)
        {
            addTrain(VsTrains.get(i).x,VsTrains[i].y,VsTrain[i].angle,1.00f);
        }
    }
}
