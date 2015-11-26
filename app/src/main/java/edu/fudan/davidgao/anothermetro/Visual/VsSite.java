package edu.fudan.davidgao.anothermetro.Visual;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.Line;
import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.SiteType;
import edu.fudan.davidgao.anothermetro.tools.Point;

/**
 * Created by gqp on 2015/11/26.
 */
public class VsSite {
    public final Point<Integer> pos;
    public final SiteType type;
    public ArrayList<VsSegment> inList;
    public ArrayList<VsSegment> outList;

    public VsSite(Site site){
        pos=site.pos;
        type=site.type;
        inList=new ArrayList<>();
        outList=new ArrayList<>();
    }

    public void add_in(VsSegment vsSegment){
        inList.add(vsSegment);
    }

    public void add_out(VsSegment vsSegment){
        outList.add(vsSegment);
    }

    public void dispatch_in(){
        //TODO
    }

    public void dispatch_out(){
        //TODO
    }

}
