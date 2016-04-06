package org.maxsys.networking;

import java.net.Socket;

public abstract class NetServerCmdHandler implements Runnable {

    public final CmdArgs cmdArgs;
    public final Socket socket;

    public NetServerCmdHandler(Socket socket, CmdArgs cmdArgs) {
        this.socket = socket;
        this.cmdArgs = cmdArgs;
    }

}
