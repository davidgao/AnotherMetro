package edu.fudan.davidgao.anothermetro;

import java.util.TimerTask;

class RunnableTimerTask extends TimerTask {
    @Override
    public void run (){
        runnable.run();
    }

    public RunnableTimerTask(Runnable runnable) throws NullPointerException{
        if (runnable == null) throw new NullPointerException();
        this.runnable = runnable;
    }

    private Runnable runnable;
}
