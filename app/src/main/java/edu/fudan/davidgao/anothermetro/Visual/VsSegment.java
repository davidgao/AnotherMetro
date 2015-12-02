package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.Site;

/**
 * Created by gqp on 2015/11/26.
 */
public class VsSegment {
    public VsLine line;
    public Site st, ed;
    public int st_a, ed_a; //int 8 direct
    public double st_angle, ed_angle; //graphic

    private static double distance(PointF a, PointF b){
        return Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
    }

    public VsSegment(Site st, Site ed, VsLine line){
        this.st=st;
        this.ed=ed;
        this.line=line;
        st_angle=0;ed_angle=0;
        st_a=0;ed_a=0;
    }

/*
 * [gq] (try to) add the following interfaces:
 * VsTrainState getTrainState(float fraction, int direction)
 * @fraction: (x - st) / (ed - st)
 * @direction: whether the train is moving in reverse.  1 if false, -1 if true.
 * Returns a VsTrainState instance, which contains
 * 1. final Point<Float> coordinate
 * 2. final int angle
 * skq and bob can directly use the instance to draw a train and passengers.
 */

    VsTrainState getTrainState(float fraction, int direction){
        ArrayList<PointF> line_dot = DrawLine.calcLine(DrawLine.getPosByAngle(st, st_angle), DrawLine.getPosByAngle(ed, ed_angle));
        // line_dot.get(0), line_dot.get(1), line_dot.get(2) to obtain three coordinates
        double d12=distance(line_dot.get(0), line_dot.get(1)), d23=distance(line_dot.get(1), line_dot.get(2));
        double d13 =d12+d23;
        double mid_frac = 0;
        if (direction==1){
            mid_frac = d12/d13;
            if (fraction>mid_frac){
                fraction = (float)((fraction- mid_frac)/(1.0 - mid_frac));
                return new VsTrainState(new PointF(fraction*(line_dot.get(1).x+line_dot.get(2).x), fraction*(line_dot.get(1).y+line_dot.get(2).y)), Config.C2CC(ed_a));
            }else{
                fraction = (float)((fraction)/(mid_frac));
                return new VsTrainState(new PointF(fraction*(line_dot.get(0).x+line_dot.get(1).x), fraction*(line_dot.get(0).y+line_dot.get(1).y)), st_a);
            }
        }else{
            mid_frac = d23/d13;
            if (fraction>mid_frac){
                fraction = (float)((fraction- mid_frac)/(1.0 - mid_frac));
                return new VsTrainState(new PointF((1.0f-fraction)*(line_dot.get(0).x+line_dot.get(1).x), (1.0f-fraction)*(line_dot.get(0).y+line_dot.get(1).y)), Config.C2CC(st_a));
            }else{
                fraction = (float)((fraction)/(mid_frac));
                return new VsTrainState(new PointF((1.0f-fraction)*(line_dot.get(1).x+line_dot.get(2).x), (1.0f-fraction)*(line_dot.get(1).y+line_dot.get(2).y)), ed_a);
            }
        }
    }
}
