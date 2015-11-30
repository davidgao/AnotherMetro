package edu.fudan.davidgao.anothermetro.core;

import edu.fudan.davidgao.anothermetro.tools.Point;

public abstract class SiteValidator {
    public SiteValidator(IGame2 game) {
        this.game = game;
    }

    public abstract boolean validate(int x, int y);
    public abstract boolean validate(Point<Integer> pos);

    protected final IGame2 game;
}
