package edu.fudan.davidgao.anothermetro.Visual;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.Line;
import edu.fudan.davidgao.anothermetro.core.Site;

/**
 * Created by gqp on 2015/11/26.
 */
public class VsLine {
    public ArrayList<Site> sites = new ArrayList<>(2);
    public static int color_ptr=0;
    public int color;
    public Line line;
    public VsLine(Line line){
        sites=line.getSites();
        this.line = line;
        color = color_ptr;
        color_ptr++;
    }
    public VsLine(Line line, int color){
        sites=line.getSites();
        this.color=color;
    }
}
