package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.GameException;
import edu.fudan.davidgao.anothermetro.core.Site;

public class Line {
    public ArrayList<Site> sites = new ArrayList<>(2);

    public Line(Site s1, Site s2) {
        sites.add(s1);
        sites.add(s2);
    }

    public void extend(Site source, Site target) throws GameException {
        if (source == sites.get(0)) {
            sites.add(0, target);
        } else if (source == sites.get(sites.size() - 1)) {
            sites.add(target);
        } else throw new GameException("Illegal line extension.");
    }
}
