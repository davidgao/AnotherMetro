package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.Line;
import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.core.Game;

/**
 * Created by gqp on 2015/11/26.
 */
public class DrawLine {
    //Beginning of GLSL
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private final int mProgram;
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private int mPositionHandle;
    private int mColorHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COLOR_PER_VERTEX = 4;
    static float[] lineCoords;
    static float[] lineColors;

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
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");

        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glVertexAttribPointer(mColorHandle, COLOR_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                colorStride, colorBuffer);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
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

    private void init(){
        game=Game.getInstance();
        lineCoords=new float[Config.MAX_SEGMENTS*4*3];
        lineColors=new float[Config.MAX_SEGMENTS*4*4];
    }

    //find the VsSite with given site by position
    private VsSite findVsSite(Site site){
        for (int i=0;i<sites.size();i++){
            VsSite temp=sites.get(i);
            if (temp.pos==Config.BG2FGpoint(site.pos)&&temp.type==site.type)
                return temp;
        }
        return null;
    }

    //pass each line, separate each segment(part line between two sites)
    private void passLine(VsLine line){
        for (int i=0;i<line.sites.size()-1;i++){
            VsSegment temp_vssegment=new VsSegment(i, i+1, line);
            VsSite temp_vssite = findVsSite(line.sites.get(i));
            calcAngle(temp_vssegment);
            temp_vssite.add_out(temp_vssegment);
            temp_vssite = findVsSite(line.sites.get(i+1));
            temp_vssite.add_in(temp_vssegment);
            segments.add(temp_vssegment);
        }
    }

    //pass each site, dispatch the in angle and out angle for each segment
    private void passSite(ArrayList<VsSite> sites){
        for (int i=0;i<sites.size();i++){
            VsSite temp=sites.get(i);
            temp.dispatch_in();
            temp.dispatch_out();
        }
    }

    //return a point at given angle on a circle
    private PointF getPosByAngle(Site site, double angle){
        PointF result=new PointF(0,0);
        result.x=(float)(Config.BG2FGx(site.pos.x)+Math.cos(angle)*Config.LATENT_SITE_RADIUS);result.y=(float)(Config.BG2FGy(site.pos.y)+Math.sin(angle)*Config.LATENT_SITE_RADIUS);
        return result;
    }

    //calc Euler distance
    private static float distance(float x0, float y0, float x1, float y1){
        return (float)Math.sqrt((x0-x1)*(x0-x1)+(y0-y1)*(y0-y1));
    }

    //get index of minimum of array
    private static int minIndex(float[] a){
        int idx=-1;
        float minV=Config.MAX_INF;
        for (int i=0;i<a.length;i++){
            if (minV>a[i]){
                minV=a[i];
                idx=i;
            }
        }
        return idx;
    }

    //calc start angle and end angle of vsSegment
    public static void calcAngle(VsSegment vsSegment){
        PointF st=Config.BG2FGpoint(vsSegment.line.sites.get(vsSegment.st).pos);
        PointF ed=Config.BG2FGpoint(vsSegment.line.sites.get(vsSegment.ed).pos);
        if (st.x==ed.x){
            if (st.y<ed.y) {
                vsSegment.st_a = 6;
                vsSegment.ed_a = 2;
            }else
            {
                vsSegment.st_a = 2;
                vsSegment.ed_a = 6;
            }
            return;
        }
        if (st.y==ed.y){
            if (st.x<ed.x){
                vsSegment.st_a=0;
                vsSegment.ed_a=4;
            }else{
                vsSegment.st_a=4;
                vsSegment.ed_a=0;
            }
            return;
        }
        float [] cpointx=new float[8];
        float [] cpointy=new float[8];
        float s =1.0f;
        cpointx[0] = (float)((1.0*ed.y-st.y+s*st.x)*s); cpointy[0]=(float)(1.0*ed.y); //S1
        cpointx[1]=(float)(1.0*ed.x); cpointy[1]=(float)(s*ed.x+st.y-s*st.x); //S2
        s=-1.0f;
        cpointx[2] = (float)((1.0*ed.y-st.y+s*st.x)*s); cpointy[2]=(float)(1.0*ed.y); //S1
        cpointx[3]=(float)(1.0*ed.x); cpointy[3]=(float)(s*ed.x+st.y-s*st.x); //S2
        s=1.0f;
        cpointx[4]=(float)((1.0*st.y-ed.y+s*ed.x)*s); cpointy[2]=(float)(1.0*st.y); //E1
        cpointx[5]=(float)(1.0*st.x); cpointy[3]=(float)(s*st.x+ed.y-s*ed.x);  //E2
        s=-1.0f;
        cpointx[6]=(float)((1.0*st.y-ed.y+s*ed.x)*s); cpointy[6]=(float)(1.0*st.y); //E1
        cpointx[7]=(float)(1.0*st.x); cpointy[7]=(float)(s*st.x+ed.y-s*ed.x);  //E2
        float [] cresult=new float[8];
        for (int i=0;i<4;i++){
            cresult[i]=distance(st.x, st.y, cpointx[i], cpointy[i]);
        }
        for (int i=4;i<8;i++) {
            cresult[i] = distance(ed.x, ed.y, cpointx[i], cpointy[i]);
        }
        int idx = minIndex(cresult);
        switch (idx){
            case 0:
                if (ed.y<st.y) {
                    vsSegment.st_a = 3;
                    vsSegment.ed_a = 0;
                }else{
                    vsSegment.st_a = 7;
                    vsSegment.ed_a = 4;
                }
                break;
            case 1:
                if (ed.x<st.x) {
                    vsSegment.st_a = 3;
                    vsSegment.ed_a = 6;
                }else{
                    vsSegment.st_a = 7;
                    vsSegment.ed_a = 2;
                }
                break;
            case 2:
                if (ed.y<st.y) {
                    vsSegment.st_a = 1;
                    vsSegment.ed_a = 4;
                }else{
                    vsSegment.st_a = 5;
                    vsSegment.ed_a = 0;
                }
                break;
            case 3:
                if (ed.x<st.x) {
                    vsSegment.st_a = 5;
                    vsSegment.ed_a = 2;
                }else{
                    vsSegment.st_a = 1;
                    vsSegment.ed_a = 6;
                }
                break;
            case 4:
                if (st.y<ed.y) {
                    vsSegment.st_a = 0;
                    vsSegment.ed_a = 3;
                }else{
                    vsSegment.st_a = 4;
                    vsSegment.ed_a = 7;
                }
                break;
            case 5:
                if (st.x<ed.x) {
                    vsSegment.st_a = 6;
                    vsSegment.ed_a = 3;
                }else{
                    vsSegment.st_a = 2;
                    vsSegment.ed_a = 7;
                }
                break;
            case 6:
                if (st.y<ed.y) {
                    vsSegment.st_a = 4;
                    vsSegment.ed_a = 1;
                }else{
                    vsSegment.st_a = 0;
                    vsSegment.ed_a = 5;
                }
                break;
            case 7:
                if (st.x<ed.x) {
                    vsSegment.st_a = 2;
                    vsSegment.ed_a = 5;
                }else{
                    vsSegment.st_a = 6;
                    vsSegment.ed_a = 1;
                }
                break;
        }
    }

    //return three point of line from st to ed
    private ArrayList<PointF> calcLine(PointF st, PointF ed){
        if (st.x==ed.x){
            ArrayList<PointF> result=new ArrayList<>();
            result.add(st);result.add(new PointF(st.x, st.y));result.add(ed);
            return result;
        }
        if (st.y==ed.y){
            ArrayList<PointF> result=new ArrayList<>();
            result.add(st);result.add(new PointF(st.x,st.y));result.add(ed);
            return result;
        }
        float [] cpointx=new float[8];
        float [] cpointy=new float[8];
        float s =1.0f;
        cpointx[0] = (float)((1.0*ed.y-st.y+s*st.x)*s); cpointy[0]=(float)(1.0*ed.y); //S1
        cpointx[1]=(float)(1.0*ed.x); cpointy[1]=(float)(s*ed.x+st.y-s*st.x); //S2
        s=-1.0f;
        cpointx[2] = (float)((1.0*ed.y-st.y+s*st.x)*s); cpointy[2]=(float)(1.0*ed.y); //S1
        cpointx[3]=(float)(1.0*ed.x); cpointy[3]=(float)(s*ed.x+st.y-s*st.x); //S2
        s=1.0f;
        cpointx[4]=(float)((1.0*st.y-ed.y+s*ed.x)*s); cpointy[2]=(float)(1.0*st.y); //E1
        cpointx[5]=(float)(1.0*st.x); cpointy[3]=(float)(s*st.x+ed.y-s*ed.x);  //E2
        s=-1.0f;
        cpointx[6]=(float)((1.0*st.y-ed.y+s*ed.x)*s); cpointy[6]=(float)(1.0*st.y); //E1
        cpointx[7]=(float)(1.0*st.x); cpointy[7]=(float)(s*st.x+ed.y-s*ed.x);  //E2
        float [] cresult=new float[8];
        for (int i=0;i<4;i++){
            cresult[i]=distance(st.x, st.y, cpointx[i], cpointy[i]);
        }
        for (int i=4;i<8;i++) {
            cresult[i] = distance(ed.x, ed.y, cpointx[i], cpointy[i]);
        }
        int idx = minIndex(cresult);
        ArrayList<PointF> result=new ArrayList<>();
        result.add(st);result.add(new PointF(cpointx[idx],cpointy[idx]));result.add(ed);
        return result;
    }

    //send data to GL's buffer
    private void segment2GLline(ArrayList<VsSegment> segments){
        for (int i=0;i<segments.size();i++){
            VsSegment temp=segments.get(i);
            ArrayList<PointF> line_dot = calcLine(getPosByAngle(temp.line.sites.get(temp.st), temp.st_angle), getPosByAngle(temp.line.sites.get(temp.ed), temp.ed_angle));
            lineCoords[i*12]=line_dot.get(0).x;lineCoords[i*12+1]=line_dot.get(0).y;lineCoords[i*12+2]=0.0f;
            lineCoords[i*12+3]=line_dot.get(1).x;lineCoords[i*12+4]=line_dot.get(1).y;lineCoords[i*12+5]=0.0f;
            lineCoords[i*12+6]=line_dot.get(1).x;lineCoords[i*12+7]=line_dot.get(1).y;lineCoords[i*12+8]=0.0f;
            lineCoords[i*12+9]=line_dot.get(2).x;lineCoords[i*12+10]=line_dot.get(2).y;lineCoords[i*12+11]=0.0f;
            for (int j=0;j<4;j++)
                for (int k=0;k<4;k++)
                    lineColors[i*16+j*4+k]=Config.color_list[temp.line.color][k];
        }
    }

    //prepare every thing, get Sites and lines, convert site, line to VsSite, VsLine. pass Line , pass Site, send to GL
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
