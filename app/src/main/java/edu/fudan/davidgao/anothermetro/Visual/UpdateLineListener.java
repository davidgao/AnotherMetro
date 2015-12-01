package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.fudan.davidgao.anothermetro.GameView;
import edu.fudan.davidgao.anothermetro.core.GameException;
import edu.fudan.davidgao.anothermetro.core.Line;
import edu.fudan.davidgao.anothermetro.core.Site;
import edu.fudan.davidgao.anothermetro.core.Game;

/**
 * Created by fs on 2015/11/26.
 */
public class UpdateLineListener implements OnTouchListener {

    private Game game;

    public PointF touchPos; //Current touch place which could be get by others

    private boolean extending, adding, leftSite;
    private long lastDownTime;

    private Site touchedSite, startSite, lastSite;
    private ArrayList<Site> planLine;
    private int planColor;
    private Line line_to_extend;

    private class Site_FGpoint{
        public Site site;
        public PointF pos;
        Site_FGpoint(Site site){
            this.site=site;
            pos = Config.BG2FGpoint(site.pos);
        }
    }
    private ArrayList<Site_FGpoint> sites = new ArrayList<>();
    private ArrayList<VsLineHead> lineHeads = new ArrayList<>();
    private HashMap<Site, Integer> occupiedDirs = new HashMap<>();

    public UpdateLineListener(){
        super();
        game = Game.getInstance();
        extending = adding = false;
        planLine = new ArrayList<>();

        lineCoords=new float[Config.MAX_SEGMENTS*12];
        lineColors=new float[Config.MAX_SEGMENTS*16];

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

    @Override
    public boolean onTouch(View v, MotionEvent event){

        /* convert v into GameView which contains its scale information, convert pixel coordinates into (-1, 1) range */
        GameView view = (GameView)v;
        touchPos = UI2FGpoint(event.getX(), event.getY(), view.view_width, view.view_height);

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                if(event.getDownTime() <= lastDownTime + 500)return false; //avoid too frequent reaction
                lastDownTime = event.getDownTime();
                reload(); //get line and site information from BG and compute necessary values

                VsLineHead touchedLineHead;
                if ((touchedLineHead = isLineHead(touchPos)) != null) {
                    /* user is touching a line head
                     * inform BG that user is trying to modify an existed line
                     * pass line color(index) and site where change starts as arguments
                     */
                    if(game.checkExtendLine(touchedLineHead.line, touchedLineHead.site)) {
                        line_to_extend = touchedLineHead.line;
                        planColor = touchedLineHead.color;
                        lastSite = startSite = touchedLineHead.site;
                        vertexCount = 4;
                        extending = true;
                        leftSite = true;
                    }
                } else if ((touchedSite = isSite(touchPos)) != null) {
                    /* user is touching a site
                     * inform BG that user is trying to add a new line
                     * pass the start site as argument
                     */
                    if((planColor = game.checkNewLine(touchedSite)) >= 0) {
                        line_to_extend = null;
                        planColor = Config.color_new_line;
                        lastSite = startSite = touchedSite;
                        vertexCount = 4;
                        adding = true;
                        leftSite = false;
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (extending || adding){
                    if((touchedSite = isSite(touchPos))!=null){
                        if(leftSite) {
                            /* user has dragged to a site
                             * inform BG that user dragged through a site when updating a line,
                             * pass the site as argument
                             */
                            if(lastSite==touchedSite){
                                planLine.remove(lastSite);
                                lastSite = planLine.size()>0? planLine.get(planLine.size()-1):startSite;
                                vertexCount -= 4;
                            }
                            else{
                                if( isExtendValid(line_to_extend, startSite, planLine) ) {
                                    planLine.add(touchedSite);
                                    ArrayList<PointF> temp = DrawLine.calcLine(Config.BG2FGpoint(lastSite.pos), Config.BG2FGpoint(touchedSite.pos));
                                    temp.add(temp.get(1));
                                    lastSite = touchedSite;
                                    for(int i=0;i<4;i++) {
                                        lineColors[(vertexCount-i)*4 - 1] = Config.color_list[planColor][3];
                                        lineColors[(vertexCount-i)*4 - 2] = Config.color_list[planColor][2];
                                        lineColors[(vertexCount-i)*4 - 3] = Config.color_list[planColor][1];
                                        lineColors[(vertexCount-i)*4 - 4] = Config.color_list[planColor][0];
                                        lineCoords[(vertexCount-i)*3 - 1] = 0;
                                        lineCoords[(vertexCount-i)*3 - 2] = temp.get(i).y;
                                        lineCoords[(vertexCount-i)*3 - 3] = temp.get(i).x;
                                    }
                                    vertexCount += 4;
                                }
                            }

                            leftSite=false;
                        }
                    }
                    else leftSite=true;
                }
                return true;

            case MotionEvent.ACTION_UP:
                if(adding && planLine.size()>0) {
                    /* user has ended current drag action
                     * inform BG that current line update event is end
                     */
                    try {
                        line_to_extend = game.addLine(startSite, planLine.get(0));
                        startSite = planLine.get(0);
                        planLine.remove(0);
                    }
                    catch (GameException e){

                    }
                }
                if(adding || extending){
                    adding = extending = false;

                    try {
                        while (planLine.size() > 0) {
                            game.extendLine(line_to_extend, startSite, planLine.get(0));
                            startSite = planLine.get(0);
                            planLine.remove(0);
                        }
                    }
                    catch (GameException ex){

                    }

                    planLine.clear();
                }
                return true;
        }
        return false;
    }

    private void reload() {
        ArrayList<Site> temp_sites = game.getSites();
        sites.clear();
        Iterator<Site> iter = temp_sites.iterator();
        for(Site temp = iter.next();iter.hasNext();temp = iter.next()){
            sites.add(new Site_FGpoint(temp));
        }

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
    }
    private int getDir(Site site){
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
    private PointF UI2FGpoint(float x, float y, int width, int height) {
        return new PointF(x/width*2-1,1-y/height*2);
    }

    private Site isSite(PointF touchPos){
        Iterator<Site_FGpoint> iter = sites.iterator();
        for(Site_FGpoint temp = iter.next();iter.hasNext();temp = iter.next()){
            if(distance(temp.pos,touchPos)<Config.SITE_RADIUS){
                return temp.site;
            }
        }
        return null;
    }
    public VsLineHead isLineHead(PointF touchPos){
        Iterator<VsLineHead> iter = lineHeads.iterator();

        for(VsLineHead currentHead = iter.next(); iter.hasNext(); iter.next()){
            if(distance(touchPos, currentHead.pos[1]) <= Config.SITE_RADIUS){
                return currentHead;
            }
        }

        return null;
    }
    private float distance(PointF a, PointF b){
        return (float)Math.sqrt(Math.pow(b.x-a.x,2)+Math.pow(b.y-a.y,2));
    }


    //Beginning of GLSL
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private final int mProgram;

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
        if(!extending && !adding) return;

        ArrayList<PointF> temp = DrawLine.calcLine(Config.BG2FGpoint(lastSite.pos), touchPos);
        temp.add(temp.get(1));
        for(int i=0;i<4;i++) {
            lineColors[(vertexCount-i)*4 - 1] = Config.color_list[Config.color_extra_line][3];
            lineColors[(vertexCount-i)*4 - 2] = Config.color_list[Config.color_extra_line][2];
            lineColors[(vertexCount-i)*4 - 3] = Config.color_list[Config.color_extra_line][1];
            lineColors[(vertexCount-i)*4 - 4] = Config.color_list[Config.color_extra_line][0];
            lineCoords[(vertexCount-i)*3 - 1] = 0;
            lineCoords[(vertexCount-i)*3 - 2] = temp.get(i).y;
            lineCoords[(vertexCount-i)*3 - 3] = temp.get(i).x;
        }

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

    //End of GLSL

}
