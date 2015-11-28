package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.Iterator;

import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.core.Game;

/**
 * Created by fs on 2015/11/26.
 */
public class UpdateLineListener implements OnTouchListener {

    private Game game;

    public PointF touchPos;

    private boolean updating, leftSite;
    private long lastDownTime;

    private class Site_FGpoint{
        public Site site;
        public PointF pos;
        Site_FGpoint(Site site){
            this.site=site;
            pos = Config.BG2FGpoint(site.pos);
        }
    }
    private ArrayList<Site_FGpoint> sites;
    private Site_FGpoint touchedSite;

    public UpdateLineListener(){
        super();
        game = Game.getInstance();
        touchPos = new PointF();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){

        touchPos = UI2FGpoint(event.getX(), event.getY());

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                if(event.getDownTime() <= lastDownTime + 500)return false;

                lastDownTime = event.getDownTime();
                reload();

                VsLineHead touchedLineHead;
                if ((touchedLineHead = DrawLineHead.getInstance().isLineHead(touchPos)) != null) {  //touching head
                    if(game.lineUpdateModify(touchedLineHead.color, touchedLineHead.site)) {
                        updating = true;
                        leftSite = true;
                    }
                } else if ((touchedSite = isSite(touchPos)) != null) {  //touching site
                    if(game.lineUpdateNew(touchedSite)) {
                        updating = true;
                        leftSite = false;
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (updating){
                    if((nextSite = isSite(touchPos))!=null){
                        if(leftSite) { //reaching a new site
                            if(game.lineUpdateCheck(touchedSite)) {
                                leftSite = false;
                            }
                        }
                    }
                    else leftSite=true;
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (updating) {
                    updating = false;
                    game.lineUpdateConfirm();
                }
                return true;
        }
        return false;
    }

    private void reload() {
        ArrayList<Site> temp_sites = game.getSites();
        Iterator<Site> iter = temp_sites.iterator();
        for(Site temp = iter.next();iter.hasNext();temp = iter.next()){
            sites.add(new Site_FGpoint(temp));
        }
    }
    private PointF UI2FGpoint(float x, float y) {
        //TO-DO
        return new PointF(x,y);
    }

    private Site_FGpoint isSite(PointF touchPos){
        Iterator<Site_FGpoint> iter = sites.iterator();
        for(Site_FGpoint temp = iter.next();iter.hasNext();temp = iter.next()){
            if(distance(temp.pos,touchPos)<Config.SITE_RADIUS){
                return temp;
            }
        }
        return null;
    }
    private float distance(PointF a, PointF b){
        return (float)Math.sqrt(Math.pow(b.x-a.x,2)+Math.pow(b.y-a.y,2));
    }
}
