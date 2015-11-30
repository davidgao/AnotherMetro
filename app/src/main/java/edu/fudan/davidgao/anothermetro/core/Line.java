package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.core.GameException;
import edu.fudan.davidgao.anothermetro.core.Site;

public class Line {
    private final ArrayList<Site> sites = new ArrayList<>();
    private TrainState trainState;

    public Line(Site s1, Site s2) {
        sites.add(s1);
        sites.add(s2);
        trainState = new StandbyTrainState(this, s1, 1);
    }

    public void extend(Site source, Site target) throws GameException {
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

    public TrainState getTrainState() {
        return trainState;
    }
}
