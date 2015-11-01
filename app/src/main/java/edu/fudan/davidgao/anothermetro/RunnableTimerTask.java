package edu.fudan.davidgao.anothermetro;

import java.util.TimerTask;

public class RunnableTimerTask extends TimerTask {
    @Override
    public void run (){
        runnable.run();
    }

    public RunnableTimerTask(Runnable runnable){
        this.runnable = runnable;
    }

    private Runnable runnable;
}
