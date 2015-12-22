package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;

/**
 * Created by gqp on 2015/12/1.
 */
public class VsTrainState {
    final PointF coordinate;
    final int angle;
    int color;
    public VsTrainState(){
        this.color = 0;
        coordinate = new PointF(0.0f,0.0f);
        angle = 0;
    }

    public VsTrainState(PointF coordinate, int angle, int color){
        this.coordinate=coordinate;
        this.angle=angle;
        this.color = color;
    }
}
