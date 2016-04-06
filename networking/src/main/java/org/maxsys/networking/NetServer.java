package org.maxsys.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NetServer implements Runnable {

    private final int PORT;
    private volatile boolean isCancelled = false;
    private final Thread thread;

    public NetServer(int port) {
        this.PORT = port;
        this.thread = new Thread(this, "NetServer on " + port);
    }

    public void startServer() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public void stopServer() {
        isCancelled = true;
    }

    @Override
    public final void run() {
        ServerSocket ssocket = null;
        try {
            ssocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            Logger.getLogger(NetServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ssocket == null) {
            return;
        }

        while (!isCancelled) {
            Socket socket = null;
            try {
                socket = ssocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(NetServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (socket == null) {
                continue;
            }

            CmdArgs cmdArgs = Sockets.receiveCmd(socket);

            if (cmdArgs.getCmd().equals("StopServer")) {
                stopServer();
            } else {
                new Thread(cmdHandler(socket, cmdArgs), "cmdHandler - " + cmdArgs.getCmd()).start();
            }
        }

    }

    public abstract Runnable cmdHandler(Socket socket, CmdArgs cmdArgs);
}
