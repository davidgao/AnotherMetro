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
    private ArrayList<PointF> line_dot;

    static double distance(PointF a, PointF b){
        return Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
    }

    //TODO remove this
    public ArrayList<PointF> getLine_dot(){
        return line_dot;
    }
    public void force_update_LineDot(){
        line_dot = DrawLine.calcLine(DrawLine.getPosByAngle(st, st_angle), DrawLine.getPosByAngle(ed, ed_angle));
    }
    public void update_LineDot(){
        if (line_dot==null)
            line_dot = DrawLine.calcLine(DrawLine.getPosByAngle(st, st_angle), DrawLine.getPosByAngle(ed, ed_angle));
    }
    public VsSegment(Site st, Site ed, VsLine line){
        this.st=st;
        this.ed=ed;
        this.line=line;
        st_angle=0;ed_angle=0;
        st_a=0;ed_a=0;
        line_dot=null;
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
        // line_dot.get(0), line_dot.get(1), line_dot.get(2) to obtain three coordinates
        double d12=distance(line_dot.get(0), line_dot.get(1)), d23=distance(line_dot.get(1), line_dot.get(2));
        double d13 =d12+d23;
        double mid_frac = 0;
        System.out.printf("SSSSS st=%d ed=%d\n", st_a, ed_a);
        if (direction==1){
            mid_frac = d12/d13;
            if (fraction>mid_frac){
                fraction = (float)((fraction- mid_frac)/(1.0 - mid_frac));
                return new VsTrainState(new PointF(line_dot.get(1).x+fraction*(line_dot.get(2).x-line_dot.get(1).x), line_dot.get(1).y+fraction*(line_dot.get(2).y-line_dot.get(1).y)),  Config.FLIP(ed_a));
            }else{
                fraction = (float)((fraction)/(mid_frac));
                return new VsTrainState(new PointF(line_dot.get(0).x+fraction*(line_dot.get(1).x-line_dot.get(0).x), line_dot.get(0).y+fraction*(line_dot.get(1).y-line_dot.get(0).y)), st_a);
            }
        }else{
            mid_frac = d23/d13;
            if (fraction>mid_frac){
                fraction = (float)((fraction- mid_frac)/(1.0 - mid_frac));
                return new VsTrainState(new PointF(line_dot.get(0).x+(1.0f-fraction)*(line_dot.get(1).x-line_dot.get(0).x), line_dot.get(0).y+(1.0f-fraction)*(line_dot.get(1).y-line_dot.get(0).y)), Config.FLIP(st_a));
            }else{
                fraction = (float)((fraction)/(mid_frac));
                return new VsTrainState(new PointF(line_dot.get(1).x+(1.0f-fraction)*(line_dot.get(2).x-line_dot.get(1).x), line_dot.get(1).y+(1.0f-fraction)*(line_dot.get(2).y-line_dot.get(1).y)), ed_a);
            }
        }
    }
    /*
    public VsTrainState getTrainState(double fraction, int direction)
    {
        ArrayList<PointF> line = line_dot;
        VsTrainState vsTrainState = null;
        PointF A,B,C,AB,BC;
        int angle1,angle2;
        if (direction == 1) {
            A = line.get(0);
            B = line.get(1);
            C = line.get(2);
            angle1 = Config.C2CC(ed_a);
            angle2 = st_a;
        } else {
            A = line.get(2);
            B = line.get(1);
            C = line.get(1);
            angle2 = Config.C2CC(ed_a);
            angle1 = st_a;
        }
        AB = new PointF(B.x-A.x,B.y-A.y);
        BC = new PointF(C.x-B.x,C.y-B.y);

        double dAB = VsSegment.distance(A, B);
        double dBC = VsSegment.distance(B, C);
        double distance = dAB + dBC;

        double fractionMiddle = dAB / distance;
        double fractionDelta = fraction - fractionMiddle;
        if ( fractionDelta > 0) {
            vsTrainState = new VsTrainState(new PointF(B.x+(float)fractionDelta * BC.x,B.y+ (float)fractionDelta * BC.y),angle1);
        } else {
            vsTrainState = new VsTrainState(new PointF(B.x+(float)fractionDelta * AB.x,B.y+ (float)fractionDelta * AB.y),angle2);
        }
        return vsTrainState;
    }*/
}
