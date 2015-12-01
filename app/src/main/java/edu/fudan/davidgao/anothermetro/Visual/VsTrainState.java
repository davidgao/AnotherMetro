package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;

/**
 * Created by gqp on 2015/12/1.
 */
public class VsTrainState {
    final PointF coordinate;
    final int angle;
    public VsTrainState(){
        coordinate = new PointF(0.0f,0.0f);
        angle = 0;
    }

    public VsTrainState(PointF coordinate, int angle){
        this.coordinate=coordinate;
        this.angle=angle;
    }
}
