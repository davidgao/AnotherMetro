package edu.fudan.davidgao.anothermetro.Visual;

import edu.fudan.davidgao.anothermetro.Line;

/**
 * Created by gqp on 2015/11/26.
 */
public class VsSegment {
    public VsLine line;
    public int st, ed;
    public int st_a, ed_a;
    public double st_angle, ed_angle;
    public VsSegment(int st, int ed, VsLine line){
        this.st=st;
        this.ed=ed;
        this.line=line;
        st_angle=0;ed_angle=0;
        st_a=0;ed_a=0;
    }
}
