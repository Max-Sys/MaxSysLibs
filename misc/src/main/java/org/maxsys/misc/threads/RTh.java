package org.maxsys.misc.threads;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class RTh implements Runnable {

    private volatile boolean isRunning;
    private volatile Thread thread;
    private final int THREAD_NAME_MAX_LENGTH = 255;

    public RTh() {
        thread = new Thread(this);
        String name = Thread.currentThread().getName() + " -> " + thread.getName();
        if (name.length() <= THREAD_NAME_MAX_LENGTH) {
            thread.setName(name);
        }
    }

    public RTh(String name) {
        name = Thread.currentThread().getName() + " -> " + name;
        if (name.length() > THREAD_NAME_MAX_LENGTH) {
            name = "..." + name.substring(name.length() - THREAD_NAME_MAX_LENGTH - 3);
        }
        thread = new Thread(this, name);
    }

    public abstract int THREAD_SLEEP_MILLS();

    public abstract void WhileRunningDo();

    @Override
    public final void run() {
        onStart();
        while (isRunning) {
            try {
                Thread.sleep(THREAD_SLEEP_MILLS());
            } catch (InterruptedException ex) {
                Logger.getLogger(RTh.class.getName()).log(Level.SEVERE, null, ex);
            }
            WhileRunningDo();
        }
        onStop();
    }

    public final void Start() {
        isRunning = true;
        thread.start();
    }

    public final void Stop() {
        isRunning = false;
    }

    public final boolean StopAndWait(long mills) {
        Stop();
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ex) {
        }
        return isEnded();
    }

    public final void StopAndForget(long timeout) {
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

    public final void Kill() {
        thread.stop();
    }

    public void onStop() {
    }

    public void onStart() {
    }

    public final boolean isEnded() {
        return !thread.isAlive();
    }
}
