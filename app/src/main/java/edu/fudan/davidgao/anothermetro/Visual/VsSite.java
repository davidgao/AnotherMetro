package edu.fudan.davidgao.anothermetro.Visual;

import android.graphics.PointF;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.Site;
import edu.fudan.davidgao.anothermetro.SiteType;

/**
 * Created by gqp on 2015/11/26.
 */
public class VsSite {
    public final PointF pos;
    public final SiteType type;
    public ArrayList<VsSegment> inList;
    public ArrayList<VsSegment> outList;

    public VsSite(Site site){
        pos=Config.BG2FGpoint(site.pos);
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
        ArrayList<Integer>[] dlist=new ArrayList[8];
        for (int i=0;i<8;i++)
            dlist[i]=new ArrayList<>();
        for (int i=0;i<inList.size();i++){
            dlist[inList.get(i).ed_a].add(i);
        }
        for (int i=0;i<8;i++){
            double delta=2.0*Math.PI/8.0/dlist[i].size();
            for (int j=0;j<dlist[i].size();j++){
                inList.get(dlist[i].get(j)).ed_angle=i*2.0*Math.PI/8.0+delta*j;
            }
        }
    }

    public void dispatch_out(){
        //TODO
        ArrayList<Integer>[] dlist=new ArrayList[8];
        for (int i=0;i<8;i++)
            dlist[i]=new ArrayList<>();
        for (int i=0;i<outList.size();i++){
            dlist[outList.get(i).st_a].add(i);
        }
        for (int i=0;i<8;i++){
            double delta=2.0*Math.PI/8.0/dlist[i].size();
            for (int j=0;j<dlist[i].size();j++){
                outList.get(dlist[i].get(j)).st_angle=i*2.0*Math.PI/8.0+delta*j;
            }
        }
    }

}
