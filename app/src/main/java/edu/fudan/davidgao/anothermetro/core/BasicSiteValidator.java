package edu.fudan.davidgao.anothermetro.core;

import java.util.ArrayList;

import edu.fudan.davidgao.anothermetro.tools.Point;

public class BasicSiteValidator extends SiteValidator {
    private ArrayList<Site> sites;
    final Runnable onSiteSpawn = new Runnable() {
        @Override
        public void run() {
            maintain();
        }
    };
    private double minDist = 10.0;

    public BasicSiteValidator(IGame game) {
        super(game);
    }

    public double getMinDist() {
        return minDist;
    }

    public synchronized void setMinDist(double minDist) throws GameException {
        synchronized (game) {
            game.assertState(GameState.NEW);
            this.minDist = minDist;
        }
    }

    @Override
    public synchronized boolean validate(int x, int y) {
        synchronized (game) {
            for (Site site:sites) {
                if (site.dist(x, y) < minDist) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public boolean validate(Point<Integer> pos) {
        return validate(pos.x, pos.y);
    }

    private synchronized void maintain() {
        sites = game.getSites();
    }
}
