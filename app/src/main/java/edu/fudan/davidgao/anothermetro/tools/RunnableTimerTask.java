package edu.fudan.davidgao.anothermetro.tools;

import java.util.TimerTask;

public class RunnableTimerTask extends TimerTask {
    @Override
    public void run (){
        runnable.run();
    }

    public RunnableTimerTask(Runnable runnable) throws NullPointerException {
        if (runnable == null) throw new NullPointerException();
        this.runnable = runnable;
    }

    protected Runnable runnable;
}
