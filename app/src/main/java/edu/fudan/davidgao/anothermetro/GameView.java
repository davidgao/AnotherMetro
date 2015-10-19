package edu.fudan.davidgao.anothermetro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gq on 10/19/15.
 */
public class GameView extends View {

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void renderGame(Canvas canvas) {
        Game game = Game.getInstance();
        // Lock game
        synchronized (game) {
            Site[] sites = game.getSites();
            Line[] lines = game.getLines();
            Drawer.drawMap(canvas, game.getMap());
            for (int i = 0; i < lines.length; ++i) {
                Drawer.drawLine(canvas, lines[i]);
            }
            for (int i = 0; i < sites.length; ++i) {
                Drawer.drawSite(canvas, sites[i]);
            }
        }
    }

    public void mainLoop() {
        while (true) {
            invalidate();
            try {
                Thread.sleep(GameConfiguration.getRefreshInterval(), 0);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        renderGame(canvas);
    }
}
