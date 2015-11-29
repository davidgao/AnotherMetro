package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.fudan.davidgao.anothermetro.GameView;
import edu.fudan.davidgao.anothermetro.Line;
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
    private ArrayList<Site_FGpoint> sites = new ArrayList<>();
    private ArrayList<VsLineHead> lineHeads = new ArrayList<>();
    private HashMap<Site, Integer> occupiedDirs = new HashMap<>();
    private Site_FGpoint touchedSite;

    public UpdateLineListener(){
        super();
        game = Game.getInstance();
        touchPos = new PointF();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){

        GameView view = (GameView)v;
        touchPos = UI2FGpoint(event.getX(), event.getY(), view.view_width, view.view_height);

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                if(event.getDownTime() <= lastDownTime + 500)return false;

                lastDownTime = event.getDownTime();
                reload();

                VsLineHead touchedLineHead;
                if ((touchedLineHead = isLineHead(touchPos)) != null) {  //touching head
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
                    if((touchedSite = isSite(touchPos))!=null){
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
        sites.clear();
        Iterator<Site> iter = temp_sites.iterator();
        for(Site temp = iter.next();iter.hasNext();temp = iter.next()){
            sites.add(new Site_FGpoint(temp));
        }

        ArrayList<Line> temp_lines=game.getLines();
        occupiedDirs.clear();
        lineHeads.clear();
        for (int i=0;i<temp_lines.size();i++){
            ArrayList<Site> line = temp_lines.get(i).sites;
            Site start = line.get(0), end = line.get(line.size()-1);
            lineHeads.add(new VsLineHead(start, i, getDir(start)));
            lineHeads.add(new VsLineHead(end, i, getDir(end)));
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

    private Site_FGpoint isSite(PointF touchPos){
        Iterator<Site_FGpoint> iter = sites.iterator();
        for(Site_FGpoint temp = iter.next();iter.hasNext();temp = iter.next()){
            if(distance(temp.pos,touchPos)<Config.SITE_RADIUS){
                return temp;
            }
        }
        return null;
    }
    public VsLineHead isLineHead(PointF onTouch){
        Iterator<VsLineHead> iter = lineHeads.iterator();

        for(VsLineHead currentHead = iter.next(); iter.hasNext(); iter.next()){
            if(distance(onTouch, currentHead.pos[1]) <= Config.SITE_RADIUS){
                return currentHead;
            }
        }

        return null;
    }
    private float distance(PointF a, PointF b){
        return (float)Math.sqrt(Math.pow(b.x-a.x,2)+Math.pow(b.y-a.y,2));
    }
}
