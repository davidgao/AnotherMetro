package edu.fudan.davidgao.anothermetro.Visual;
import android.graphics.PointF;

import edu.fudan.davidgao.anothermetro.tools.Point;

/**
 * Created by gqp on 2015/11/28.
 */
public class Config {
    public static int GRID_X=0;
    public static int GRID_Y=0;
    public static final int MAX_SEGMENTS=200;
    public static final double LATENT_SITE_RADIUS=0.05;
    public static final float MAX_INF=1e20f;
    public static final float color_list[][] ={{ 0.63671875f, 0.76953125f, 0.22265625f, 1.0f }, { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f }}; //TODO
    public Config(){
    }
    public static PointF BG2FGpoint(Point<Integer> point){
        PointF t=new PointF(0,0);
        t.x=(float)(point.x/GRID_X*2.0-1.0);
        t.y=(float)((GRID_Y-point.y)/GRID_Y*2.0-1.0);
        return t;
    }
    public static float BG2FGx(float x){
        return (float)(x/GRID_X*2.0-1.0);
    }
    public static float BG2FGy(float y){
        return (float)((GRID_Y-y)/GRID_Y*2.0-1.0);
    }
}
