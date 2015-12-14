package org.maxsys.misc.threads;

public abstract class RTh implements Runnable {

    private volatile boolean isRunning;
    private volatile Thread thread;

    public RTh() {
        thread = new Thread(this);
        String name = Thread.currentThread().getName() + " -> " + thread.getName();
        if (name.length() < 256) {
            thread.setName(name);
        }
    }

    public RTh(String name) {
        name = Thread.currentThread().getName() + " -> " + name;
        if (name.length() > 255) {
            name = "..." + name.substring(name.length() - 252);
        }
        thread = new Thread(this, name);
    }

    public abstract void WhileRunningDo();

    @Override
    public final void run() {
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
