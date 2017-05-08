package org.maxsys.misc.threads;

public abstract class RTh implements Runnable {

    private volatile boolean isRunning;
    private volatile boolean isPaused;
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
            if (!isPaused) {
                WhileRunningDo();
            }
            try {
                Thread.sleep(THREAD_SLEEP_MILLS());
            } catch (InterruptedException ex) {
            }
        }
        onStop();
    }

    public final void Start() {
        isRunning = true;
        isPaused = false;
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public final void Pause() {
        isPaused = true;
    }

    public final void Go() {
        isPaused = false;
    }

    public final void Stop() {
        thread.interrupt();
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
            int to = (int) (timeout / 1000);
            while (to > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                if (isEnded()) {
                    return;
                }
            }
            if (!isEnded()) {
                Kill();
            }
        }, thread.getName() + " - S&F").start();
    }

    @SuppressWarnings("deprecation")
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
