package edu.fudan.davidgao.anothermetro.core;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.HashMap;

class BasicPathFinder implements PathFinder {
    IGame2 game;
    public BasicPathFinder() {
        game = IGame2.getInstance();
    }

    public boolean getOnTrain(Passenger p, Site s, Train t) {
        int siteCount1, siteCount2, pos;
        Site nextSite;
        Line l = t.state.line;
        pos = l.sites.indexOf(s);
        pos += t.state.direction;
        nextSite = l.sites.get(pos);

        siteCount1 = getSiteCount(s, p.type);
        siteCount2 = getSiteCount(nextSite, p.type);
System.out.printf("Counts: %d %d\n", siteCount1, siteCount2);
        return (siteCount2 < siteCount1);
    }

    private int getSiteCount(Site start, SiteType type) {
        HashMap<Site, Boolean> visited = new HashMap<>();
        ArrayList<Site> queue = new ArrayList<>();
        int stepCount = 0, maxStepCount = game.sites.size();

        queue.add(start);
        visited.put(start, true);
        while (stepCount < maxStepCount) {
            ArrayList<Site> nextQueue = new ArrayList<>();
            for (Site s : queue) {
                for (Line l : s.lines) {
                    int pos = l.sites.indexOf(s);
                    if (pos > 0) {
                        Site nextSite = l.sites.get(pos - 1);
                        if (!visited.containsKey(nextSite)) {
                            if (nextSite.type == type) {
                                return stepCount + 1;
                            }
                            nextQueue.add(nextSite);
                            visited.put(nextSite, true);
                        }
                    }
                    if (pos < l.sites.size() - 1) {
                        Site nextSite = l.sites.get(pos + 1);
                        if (!visited.containsKey(nextSite)) {
                            if (nextSite.type == type) {
                                return stepCount + 1;
                            }
                            nextQueue.add(nextSite);
                            visited.put(nextSite, true);
                        }
                    }
                }
            }
            queue = nextQueue;
            stepCount += 1;
        }
        return 10000;
    }
}
