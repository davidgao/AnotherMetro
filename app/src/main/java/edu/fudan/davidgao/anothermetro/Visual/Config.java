package edu.fudan.davidgao.anothermetro.Visual;
import android.graphics.PointF;

import edu.fudan.davidgao.anothermetro.tools.Point;

/**
 * Created by gqp on 2015/11/28.
 */
public class Config {
    public static int GRID_X=400;
    public static int GRID_Y=300;
    public static final float SITE_RADIUS=0.05f;

    public static final int MAX_LINES=8;
    public static final int MAX_SEGMENTS=200;
    public static final double LATENT_SITE_RADIUS=0.05;

    public static final float MAX_INF=1e20f;
    public static final float Z_SEGMENT=0.0f;
    public static final float Z_LINEHEAD=0.1e-4f;
    public static final float Z_BLUEPRINT=0.2e-4f;
    public static final double EPSI=1e-6;
    public static final int C2CC_list[]={0,7,6,5,4,3,2,1};

    public static final float color_list[][] ={
            { 0.9f, 0.1f, 0.1f, 1.0f },
            { 0.8f, 0.8f, 0.1f, 1.0f },
            { 0.1f, 0.9f, 0.1f, 1.0f },
            { 0.1f, 0.8f, 0.8f, 1.0f },
            { 0.1f, 0.1f, 0.9f, 1.0f },
            { 0.8f, 0.1f, 0.8f, 1.0f },
            { 0.9f, 0.7f, 0.7f, 1.0f },
            { 0.7f, 0.7f, 0.9f, 1.0f }}; //TODO
    public static final float color_extra_line[] = {0.2f, 0.2f, 0.2f, 0.5f};

    public Config(){
    }
    public static int C2CC(int t){
        return C2CC_list[t];
    }
    public static PointF BG2FGpoint(Point<Integer> point){
        PointF t=new PointF(0,0);
        t.x=(float)(1.0*point.x/GRID_X*2.0-1.0);
        t.y=(float)((1.0*GRID_Y-point.y)/GRID_Y*2.0-1.0);
        return t;
    }
    public static float BG2FGx(Integer x){
        return (float)(1.0f*x/GRID_X*2.0-1.0);
    }
    public static float BG2FGy(Integer y){
        return (float)((1.0f*GRID_Y-y)/GRID_Y*2.0-1.0);
    }
}
