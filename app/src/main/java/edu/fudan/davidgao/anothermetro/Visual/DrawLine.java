package edu.fudan.davidgao.anothermetro.Visual;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import edu.fudan.davidgao.anothermetro.Line;
import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.core.Game;

/**
 * Created by gqp on 2015/11/26.
 */
public class DrawLine {
    //Beginning of GLSL
    private FloatBuffer vertexBuffer;
    private final int mProgram;
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private int mPositionHandle;
    private int mColorHandle;

    private int vertexCount = 0;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    //End of GLSL

    private ArrayList<VsLine> lines;
    private ArrayList<VsSite> sites;
    private ArrayList<VsSegment> segments;
    private Game game;
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

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;
    static float lineCoords[Config.MAX_LINES*4];

    public void draw() {
        prepare();

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
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public DrawLine() {
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

    private void init(){
        game=Game.getInstance();
    }

    private VsSite findVsSite(Site site){
        for (int i=0;i<sites.size();i++){
            VsSite temp=sites.get(i);
            if (temp.pos==site.pos&&temp.type==site.type)
                return temp;
        }
        return null;
    }
    private void passLine(VsLine line){
        for (int i=0;i<line.sites.size()-1;i++){
            VsSegment temp_vssegment=new VsSegment(i, i+1, line);
            VsSite temp_vssite = findVsSite(line.sites.get(i));
            temp_vssite.add_out(temp_vssegment);
            temp_vssite = findVsSite(line.sites.get(i+1));
            temp_vssite.add_in(temp_vssegment);
            segments.add(temp_vssegment);
        }
    }

    private void passSite(ArrayList<VsSite> sites){
        //TODO dispatch angles for each site

        for (int i=0;i<sites.size();i++){
            VsSite temp=sites.get(i);
            temp.dispatch_in();
            temp.dispatch_out();
        }
    }

    private void segment2GLline(ArrayList<VsSegment> segments){
        for (int i=0;i<segments.size();i++){
            VsSegment temp=segments.get(i);
            ArrayList<Float> line_dot = calcLine(getPosByAngle(temp.line.sites.get(temp.st), temp.st_angle), getPosByAngle(temp.line.sites.get(temp.ed), temp.ed_angle));
            lineCoords[i*4]=line_dot.get(0);lineCoords[i*4+1]=line_dot.get(1);lineCoords[i*4+2]=line_dot.get(2);lineCoords[i*4+3]=line_dot.get(3);
            lineColors[i*2]=temp.line.color;lineColors[i*2+1]=temp.line.color;
        }
    }

    private void prepare(){
        ArrayList<Site> temp_sites=game.getSites();
        ArrayList<Line> temp_lines=game.getLines();
        segments.clear();
        sites.clear();
        lines.clear();
        for (int i=0;i<temp_sites.size();i++){
            VsSite temp_vssite=new VsSite(temp_sites.get(i));
            sites.add(temp_vssite);
        }
        VsLine.color_ptr=0;
        for (int i=0;i<temp_lines.size();i++){
            VsLine temp_vsline=new VsLine(temp_lines.get(i));
            lines.add(temp_vsline);
            passLine(temp_vsline);
        }
        passSite(sites);
        segment2GLline(segments);
    }



}
