package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;
import java.math.*;

import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.Line;
import edu.fudan.davidgao.anothermetro.tools.Point;


/**
 * Created by fs on 15/11/28.
 */
public class VsLineHead {
    public Site site;
    private int direction;
    public int color;
    public PointF[] pos = new PointF[4]; //root, middle, left, right
    private float rotateSin, rotateCos;

    VsLineHead(Site site, int color, int dir){
        this.site = site;
        this.color = color;
        direction = dir;

        setRotate();

        pos[0] = rotate(Config.SITE_RADIUS, 0f, Config.BG2FGpoint(site.pos));
        pos[1] = rotate(Config.SITE_RADIUS * 2, 0f, pos[0]);
        pos[2] = rotate(0f, Config.SITE_RADIUS, pos[1]);
        pos[3] = rotate(0f, -Config.SITE_RADIUS, pos[1]);
    }

    private void setRotate(){
        float sin45 = (float)Math.sqrt(2)/2;
        switch(direction){
            case 0: rotateCos = 1; rotateSin = 0; break;
            case 1: rotateCos = sin45; rotateSin = sin45; break;
            case 2: rotateCos = 0; rotateSin = 1; break;
            case 3: rotateCos = -sin45; rotateSin = sin45; break;
            case 4: rotateCos = -1; rotateSin = 0; break;
            case 5: rotateCos = -sin45; rotateSin = -sin45; break;
            case 6: rotateCos = 0; rotateSin = -1; break;
            case 7: rotateCos = sin45; rotateSin = -sin45; break;
        }
    }
    private PointF rotate(float x, float y, PointF offset){
        PointF target = new PointF(x*rotateCos - y*rotateSin, x*rotateSin + y*rotateSin);
        target.offset(offset.x, offset.y);
        return target;
    }
};
