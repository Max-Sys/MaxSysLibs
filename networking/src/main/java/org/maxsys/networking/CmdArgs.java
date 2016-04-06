package org.maxsys.networking;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.maxsys.misc.Bytes;

public class CmdArgs {

    private final String cmd;
    private final byte[] args;

    public CmdArgs(String cmd, byte[] args) {
        this.cmd = cmd == null ? "" : cmd;
        this.args = args == null ? new byte[0] : args;
    }

    public CmdArgs(byte[] data) {
        if (data.length < 8) {
            this.cmd = "";
            this.args = new byte[0];
            return;
        }

        ByteBuffer bb = ByteBuffer.wrap(data);

        // Getting cmd
        byte[] cmdBytesSize = new byte[4];
        bb.get(cmdBytesSize);
        byte[] cmdBytes = new byte[Bytes.bytesToInt(cmdBytesSize)];
        bb.get(cmdBytes);
        String cmdString = "";
        try {
            cmdString = new String(cmdBytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CmdArgs.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.cmd = cmdString;

        // Getting args
        byte[] argsBytesSize = new byte[4];
        bb.get(argsBytesSize);
        this.args = new byte[Bytes.bytesToInt(argsBytesSize)];
        bb.get(this.args);
    }

    public String getCmd() {
        return cmd;
    }

    public byte[] getArgs() {
        return args;
    }

    public byte[] getCmdArgsData() {
        byte[] cmdBytes; // cmd in byte[]
        try {
            cmdBytes = this.cmd.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CmdArgs.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        byte[] cmdBytesSize = Bytes.intToBytes(cmdBytes.length); // Size of cmdBytes

        byte[] argsBytesSize = Bytes.intToBytes(this.args.length); // Size of args

        ByteBuffer bytes = ByteBuffer.allocate(cmdBytesSize.length + cmdBytes.length + argsBytesSize.length + this.args.length); // All bytes

        bytes.put(cmdBytesSize);
        bytes.put(cmdBytes);
        bytes.put(argsBytesSize);
        bytes.put(this.args);

        return bytes.array();
    }

}
