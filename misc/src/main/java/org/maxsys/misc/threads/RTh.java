package org.maxsys.misc.threads;

public abstract class RTh implements Runnable {

    private volatile boolean isRunning;
    private volatile Thread thread;

    public RTh() {
        thread = new Thread(this);
    }

    public RTh(String name) {
        thread = new Thread(this, name);
    }

    public abstract void WhileRunningDo();

    @Override
    public void run() {
        while (isRunning) {
            WhileRunningDo();
        }
    }

    public void Start() {
        isRunning = true;
        thread.start();
    }

    public void Stop() {
        isRunning = false;
    }

    public boolean StopAndWait(long mills) {
        Stop();
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ex) {
        }
        return isEnded();
    }

    public void StopAndForget(long timeout) {
        new Thread(() -> {
            Stop();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
            }
            if (!isEnded()) {
                Kill();
            }
        }, thread.getName() + " - S&F").start();
    }

    public void Kill() {
        thread.stop();
    }

    public boolean isEnded() {
        return !thread.isAlive();
    }
}
