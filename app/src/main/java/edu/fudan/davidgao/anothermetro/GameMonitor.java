package edu.fudan.davidgao.anothermetro;

import android.app.AlertDialog;
import android.content.DialogInterface;

import edu.fudan.davidgao.anothermetro.core.Game;
import edu.fudan.davidgao.anothermetro.core.GameEvent;
import edu.fudan.davidgao.anothermetro.tools.Broadcaster;

/**
 * Created by gq on 12/22/15.
 */
public class GameMonitor {
    private static GameMonitor gameMonitor = null;
    private GameMain gameMain;

    private Broadcaster b;
    public static GameMonitor getInstance() {
        if (gameMonitor == null) {
            gameMonitor = new GameMonitor();
        }
        return gameMonitor;
    }
    private GameMonitor() {
        b = Game.getInstance().getCallbackBroadcaster(GameEvent.GAME_OVER);
        gameMain = (GameMain)GameView.getInstance().getContext();
        /* Too dirty but I don't want to think... */
        b.addListener(new Runnable() {
            @Override
            public void run() {
                gameMain.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doGameOver();
                    }
                });
            }
        });
    }

    private void doGameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(gameMain);
        builder.setTitle("Game over");
        builder.setMessage("Your score:" + Game.getInstance().getScore());
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameMain.finish();
            }
        });
        builder.create().show();
    }
}
