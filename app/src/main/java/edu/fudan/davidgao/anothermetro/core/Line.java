package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.GameException;
import edu.fudan.davidgao.anothermetro.core.Site;

public class Line {
    private final ArrayList<Site> sites = new ArrayList<>();
    public final Train train;

    public Line(Site s1, Site s2) {
        sites.add(s1);
        sites.add(s2);
        train = new Train(this);
    }

    public void extend(Site source, Site target) throws GameException {
        if (sites.contains(target)) throw new GameException("Illegal line extension.");
        if (source == sites.get(0)) {
            sites.add(0, target);
        } else if (source == sites.get(sites.size() - 1)) {
            sites.add(target);
        } else throw new GameException("Illegal line extension.");
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Site> getSites() {
        return (ArrayList<Site>)sites.clone();
    }
}
