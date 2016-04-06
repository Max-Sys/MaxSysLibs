package org.maxsys.misc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bytes {

    public static byte[] intToBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >> 24) & 0xFF);
        bytes[1] = (byte) ((i >> 16) & 0xFF);
        bytes[2] = (byte) ((i >> 8) & 0xFF);
        bytes[3] = (byte) (i & 0xFF);
        return bytes;
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length == 4) {
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            return -1;
        }
    }

    public static byte[] stringsToBytes(String[] strings) {
        ArrayList<byte[]> bytesAL = new ArrayList<>();
        int bytesSize = 0;
        for (String string : strings) {
            try {
                byte[] bytesString = string.getBytes("UTF-8");
                byte[] bytesStringSize = Bytes.intToBytes(bytesString.length);
                bytesSize += bytesStringSize.length + bytesString.length;
                bytesAL.add(bytesStringSize);
                bytesAL.add(bytesString);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ByteBuffer bb = ByteBuffer.allocate(bytesSize);
        bytesAL.forEach(bb::put);
        return bb.array();
    }

    public static String[] bytesToStrings(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        ArrayList<String> stringsAL = new ArrayList<>();
        while (bb.hasRemaining()) {
            byte[] bytesStringSize = new byte[4];
            bb.get(bytesStringSize);
            byte[] bytesString = new byte[Bytes.bytesToInt(bytesStringSize)];
            bb.get(bytesString);
            try {
                stringsAL.add(new String(bytesString, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return stringsAL.toArray(new String[0]);
    }

    public static byte[] doublesToBytes(double[] doubles) {
        ByteBuffer bb = ByteBuffer.allocate(doubles.length * 8);
        for (double d : doubles) {
            bb.putDouble(d);
        }
        return bb.array();
    }

    public static double[] bytesToDoubles(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        double[] doubles = new double[bytes.length / 8];
        for (int di = 0; di < doubles.length; di++) {
            doubles[di] = bb.getDouble();
        }
        return doubles;
    }

    public static byte[] intsToBytes(int[] ints) {
        ByteBuffer bb = ByteBuffer.allocate(ints.length * 4);
        for (int i : ints) {
            bb.putInt(i);
        }
        return bb.array();
    }

    public static int[] bytesToInts(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        int[] ints = new int[bytes.length / 4];
        for (int ii = 0; ii < ints.length; ii++) {
            ints[ii] = bb.getInt();
        }
        return ints;
    }

    public static byte[] longsToBytes(long[] longs) {
        ByteBuffer bb = ByteBuffer.allocate(longs.length * 8);
        for (long l : longs) {
            bb.putLong(l);
        }
        return bb.array();
    }

    public static long[] bytesToLongs(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long[] longs = new long[bytes.length / 8];
        for (int li = 0; li < longs.length; li++) {
            longs[li] = bb.getLong();
        }
        return longs;
    }

    public static byte[] timestampsToBytes(Timestamp[] timestamps) {
        ByteBuffer bb = ByteBuffer.allocate(timestamps.length * 8);
        for (Timestamp ts : timestamps) {
            bb.putLong(ts.getTime());
        }
        return bb.array();
    }

    public static Timestamp[] bytesToTimestamps(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        Timestamp[] timestamps = new Timestamp[bytes.length / 8];
        for (int tsi = 0; tsi < timestamps.length; tsi++) {
            timestamps[tsi] = new Timestamp(bb.getLong());
        }
        return timestamps;
    }

    public static byte[] treeMapTimestampDoubleToBytes(TreeMap<Timestamp, Double> treemap) {
        ByteBuffer bb = ByteBuffer.allocate(treemap.size() * 16);
        treemap.keySet().stream().forEach((ts) -> {
            bb.putLong(ts.getTime());
            bb.putDouble(treemap.get(ts));
        });
        return bb.array();
    }

    public static TreeMap<Timestamp, Double> bytesTotreeMapTimestampDouble(byte[] bytes) {
        TreeMap<Timestamp, Double> treemap = new TreeMap<>();
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int tmi = 0; tmi < bytes.length / 16; tmi++) {
            Timestamp ts = new Timestamp(bb.getLong());
            Double val = bb.getDouble();
            treemap.put(ts, val);
        }
        return treemap;
    }
}
