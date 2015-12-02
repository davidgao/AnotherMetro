package edu.fudan.davidgao.anothermetro.Visual;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.fudan.davidgao.anothermetro.core.GameEvent;
import edu.fudan.davidgao.anothermetro.core.Line;
import edu.fudan.davidgao.anothermetro.core.Site;
import edu.fudan.davidgao.anothermetro.core.Game;
import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

/**
 * Created by fs on 2015/11/26.
 */
public class DrawLineHead {
    //Beginning of GLSL
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COLOR_PER_VERTEX = 4;
    static final float[] lineCoords = new float[Config.MAX_LINES*8*COORDS_PER_VERTEX];
    static float[] lineColors = new float[Config.MAX_LINES*8*COLOR_PER_VERTEX];

    private int vertexCount = 0;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int colorStride = COLOR_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" + "attribute vec4 vColor;" + "varying vec4 aColor;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" + "aColor = vColor;"+
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 aColor;" +
                    "void main() {" +
                    "  gl_FragColor = aColor;" +
                    "}";
    //End of GLSL

    //Beginning of GLSL
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }


    public void draw() {

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");

        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glVertexAttribPointer(mColorHandle, COLOR_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                colorStride, colorBuffer);

        synchronized (lineCoords) {
            // Draw the triangle
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);
        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    public DrawLineHead() {
        init();

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                lineCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(lineCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // initialize vertex byte buffer for color
        ByteBuffer cc = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                lineColors.length * 4);
        // use the device hardware's native byte order
        cc.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        colorBuffer = cc.asFloatBuffer();
        // add the color to the FloatBuffer
        colorBuffer.put(lineColors);
        // set the buffer to read the first coordinate
        colorBuffer.position(0);


        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,   //TODO move loadShader to rendering class
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);


        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }
    //End of GLSL

    private ArrayList<VsLineHead> lineHeads = new ArrayList<>();
    private HashMap<Site, Integer> occupiedDirs=new HashMap<>();
    private Game game;

    private class LineChangeListener implements Runnable{

        @Override
        public void run() { //fetch line stats from BG
            ArrayList<Line> temp_lines=game.getLines();
            occupiedDirs.clear();
            lineHeads.clear();

            for (int i=0;i<temp_lines.size();i++){
                Line line = temp_lines.get(i);
                ArrayList<Site> sites = line.getSites();
                Site start = sites.get(0), end = sites.get(sites.size()-1);
                lineHeads.add(new VsLineHead(line, start, i, getDir(start)));
                lineHeads.add(new VsLineHead(line, end, i, getDir(end)));
            }

            heads2GLline();
        }
        private int getDir(Site site){ //give an available direction of a site to draw a new head
            Integer occupied = occupiedDirs.get(site);
            if(occupied == null){
                occupiedDirs.put(site, 1);
                return 0;
            }
            else{
                int mask = 1;
                for(int i=0;i<8;i++, mask <<= 1){
                    if((occupied & mask)==0){
                        occupiedDirs.put(site, occupied|mask);
                        return i;
                    }
                }
                return 0;
            }

        }
        private void heads2GLline(){
            VsLineHead currentHead;

            synchronized (lineCoords) {
                for (int i = 0; i<lineHeads.size(); i++) {
                    currentHead = lineHeads.get(i);
                    for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 4; k++) {
                            lineColors[i * 16 + j * 4 + k] = Config.color_list[currentHead.color][k];
                        }
                        lineCoords[i * 12 + j * 3] = currentHead.pos[j].x;
                        lineCoords[i * 12 + j * 3 + 1] = currentHead.pos[j].y;
                        lineCoords[i * 12 + j * 3 + 2] = Config.Z_LINEHEAD;
                    }
                }
                vertexCount = lineHeads.size() * 4;
            }
            colorBuffer.put(lineColors);
            colorBuffer.position(0);
            vertexBuffer.put(lineCoords);
            vertexBuffer.position(0);
        }
    }
    private LineChangeListener LCListener = new LineChangeListener();

    private void init(){
        game=Game.getInstance();
        game.getCallbackBroadcaster(GameEvent.LINE_CHANGE).addListener(LCListener);
    }

}
