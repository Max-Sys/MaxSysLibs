package org.maxsys.networking;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sockets {

    public static Socket newClientSocket(String serverAddress, int port) {
        try {
            Socket newsocket = new Socket(serverAddress, port);
            return newsocket;
        } catch (IOException ex) {
            Logger.getLogger(Sockets.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(Sockets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendData(Socket socket, byte[] data) {
        byte[] dataSize = new byte[4];
        dataSize[0] = (byte) ((data.length >> 24) & 0xFF);
        dataSize[1] = (byte) ((data.length >> 16) & 0xFF);
        dataSize[2] = (byte) ((data.length >> 8) & 0xFF);
        dataSize[3] = (byte) (data.length & 0xFF);
        try {
            socket.getOutputStream().write(dataSize);
            socket.getOutputStream().flush();
            socket.getOutputStream().write(data);
            socket.getOutputStream().flush();
        } catch (IOException ex) {
            Logger.getLogger(Sockets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static byte[] receiveData(Socket socket) {
        byte[] sizeBytes = new byte[4];
        try {
            if (socket.getInputStream().read(sizeBytes) <= 0) {
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(Sockets.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        int size = ((sizeBytes[0] & 0xFF) << 24) | ((sizeBytes[1] & 0xFF) << 16) | ((sizeBytes[2] & 0xFF) << 8) | (sizeBytes[3] & 0xFF);

        byte[] data = new byte[size];
        try {
            int bytesRead;
            int offset = 0;
            while (data.length > offset) {
                bytesRead = socket.getInputStream().read(data, offset, data.length - offset);
                offset += bytesRead;
            }
        } catch (IOException ex) {
            Logger.getLogger(Sockets.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return data;
    }

    public static void sendCmd(Socket socket, CmdArgs cmdArgs) {
        Sockets.sendData(socket, cmdArgs.getCmdArgsData());
    }

    public static void sendCmd(String serverAddress, int port, CmdArgs cmdArgs) {
        Socket socket = Sockets.newClientSocket(serverAddress, port);
        Sockets.sendCmd(socket, cmdArgs);
        Sockets.closeSocket(socket);
    }

    public static CmdArgs receiveCmd(Socket socket) {
        return new CmdArgs(Sockets.receiveData(socket));
    }
}
