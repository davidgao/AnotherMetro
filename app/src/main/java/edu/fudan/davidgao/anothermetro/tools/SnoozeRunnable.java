package edu.fudan.davidgao.anothermetro.tools;

public class SnoozeRunnable {
    /*
        When alarm.run() is called, enter snoozing state.
        When deadline.run() is called, run wrapped runnable if and only if in snoozing state.
     */
    public SnoozeRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public final Runnable alarm = new Runnable() {
        @Override
        public void run() {
            synchronized (SnoozeRunnable.this){
                snoozing = true;
            }
        }
    };

    public final Runnable deadline = new Runnable() {
        @Override
        public void run() {
            synchronized (SnoozeRunnable.this){
                if (snoozing) {
                    snoozing = false;
                    runnable.run();
                }
            }
        }
    };

    private boolean snoozing = false;
    private Runnable runnable;
}
