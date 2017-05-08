package org.maxsys.misc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bytes {

    public static byte[] toBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >> 24) & 0xFF);
        bytes[1] = (byte) ((i >> 16) & 0xFF);
        bytes[2] = (byte) ((i >> 8) & 0xFF);
        bytes[3] = (byte) (i & 0xFF);
        return bytes;
    }

    public static byte[] toBytes(int[] ints) {
        ByteBuffer bb = ByteBuffer.allocate(ints.length * 4);
        for (int i : ints) {
            bb.putInt(i);
        }
        return bb.array();
    }

    public static int toInt(byte[] bytes) {
        if (bytes.length == 4) {
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            return -1;
        }
    }

    public static int[] toInts(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        int[] ints = new int[bytes.length / 4];
        for (int ii = 0; ii < ints.length; ii++) {
            ints[ii] = bb.getInt();
        }
        return ints;
    }

    public static byte[] toBytes(String string) {
        try {
            byte[] bytesString = string.getBytes("UTF-8");
            byte[] bytesStringSize = Bytes.toBytes(bytesString.length);
            ByteBuffer bb = ByteBuffer.allocate(bytesStringSize.length + bytesString.length);
            bb.put(bytesStringSize);
            bb.put(bytesString);
            return bb.array();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static byte[] toBytes(String[] strings) {
        ArrayList<byte[]> bytesAL = new ArrayList<>();
        int bytesSize = 0;
        for (String string : strings) {
            try {
                byte[] bytesString = string.getBytes("UTF-8");
                byte[] bytesStringSize = Bytes.toBytes(bytesString.length);
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

    public static String toString(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        byte[] bytesStringSize = new byte[4];
        bb.get(bytesStringSize);
        byte[] bytesString = new byte[Bytes.toInt(bytesStringSize)];
        bb.get(bytesString);
        try {
            return new String(bytesString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String[] toStrings(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        ArrayList<String> stringsAL = new ArrayList<>();
        while (bb.hasRemaining()) {
            byte[] bytesStringSize = new byte[4];
            bb.get(bytesStringSize);
            byte[] bytesString = new byte[Bytes.toInt(bytesStringSize)];
            bb.get(bytesString);
            try {
                stringsAL.add(new String(bytesString, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return stringsAL.toArray(new String[0]);
    }

    public static byte[] toBytes(double d) {
        return ByteBuffer.allocate(8).putDouble(d).array();
    }

    public static byte[] toBytes(double[] doubles) {
        ByteBuffer bb = ByteBuffer.allocate(doubles.length * 8);
        for (double d : doubles) {
            bb.putDouble(d);
        }
        return bb.array();
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static double[] toDoubles(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        double[] doubles = new double[bytes.length / 8];
        for (int di = 0; di < doubles.length; di++) {
            doubles[di] = bb.getDouble();
        }
        return doubles;
    }

    public static byte[] toBytes(long[] longs) {
        ByteBuffer bb = ByteBuffer.allocate(longs.length * 8);
        for (long l : longs) {
            bb.putLong(l);
        }
        return bb.array();
    }

    public static long[] toLongs(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long[] longs = new long[bytes.length / 8];
        for (int li = 0; li < longs.length; li++) {
            longs[li] = bb.getLong();
        }
        return longs;
    }

    public static byte[] toBytes(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return ByteBuffer.allocate(8).putLong(timestamp.getTime()).array();
    }

    public static byte[] toBytes(Timestamp[] timestamps) {
        ByteBuffer bb = ByteBuffer.allocate(timestamps.length * 8);
        for (Timestamp ts : timestamps) {
            bb.putLong(ts.getTime());
        }
        return bb.array();
    }

    public static Timestamp toTimestamp(byte[] bytes) {
        return new Timestamp(ByteBuffer.wrap(bytes).getLong());
    }

    public static Timestamp[] toTimestamps(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        Timestamp[] timestamps = new Timestamp[bytes.length / 8];
        for (int tsi = 0; tsi < timestamps.length; tsi++) {
            timestamps[tsi] = new Timestamp(bb.getLong());
        }
        return timestamps;
    }

    public static byte[] toBytes(ArrayList objects) {
        //[[byte class (1,2,3,4...)][byte[] bytes]]...
        ArrayList<byte[]> bytesAL = new ArrayList<>();
        int bytesSize = 0;
        for (Object o : objects) {
            switch (o.getClass().getCanonicalName()) {
                case "java.lang.String": //class 1
                    byte[] stringBytes = toBytes((String) o);
                    bytesAL.add(new byte[]{1});
                    bytesAL.add(stringBytes);
                    bytesSize += stringBytes.length + 1;
                    break;
                case "java.lang.Integer": //class 2
                    bytesAL.add(new byte[]{2});
                    bytesAL.add(toBytes((int) o));
                    bytesSize += 5;
                    break;
                case "java.lang.Double": //class 3
                    bytesAL.add(new byte[]{3});
                    bytesAL.add(toBytes((double) o));
                    bytesSize += 9;
                    break;
                case "java.sql.Timestamp": //class 4
                    bytesAL.add(new byte[]{4});
                    bytesAL.add(toBytes((Timestamp) o));
                    bytesSize += 9;
                    break;
                case "java.util.LinkedHashMap": //class 5
                    bytesAL.add(new byte[]{5});

                    ArrayList<Object> lhmObjects = new ArrayList<>();
                    for (Object lhmK : ((LinkedHashMap) o).keySet()) {
                        lhmObjects.add(lhmK);
                        lhmObjects.add(((LinkedHashMap) o).get(lhmK));
                    }

                    byte[] lhmObjectsBytes = toBytes(lhmObjects);
                    byte[] lhmObjectsBytesSize = toBytes(lhmObjectsBytes.length);

                    bytesAL.add(lhmObjectsBytesSize);
                    bytesAL.add(lhmObjectsBytes);

                    bytesSize += lhmObjectsBytes.length + 5;

                    break;
            }
        }
        ByteBuffer bb = ByteBuffer.allocate(bytesSize);
        bytesAL.forEach(bb::put);
        return bb.array();
    }

    public static ArrayList<Object> toObjects(byte[] bytes) {
        ArrayList<Object> objects = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        while (bb.hasRemaining()) {
            byte c = bb.get();
            switch (c) {
                case 1:
                    byte[] bytesStringSize = new byte[4];
                    bb.get(bytesStringSize);
                    byte[] bytesString = new byte[toInt(bytesStringSize)];
                    bb.get(bytesString);
                     {
                        try {
                            String s = new String(bytesString, "UTF-8");
                            objects.add(s);
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 2:
                    objects.add(bb.getInt());
                    break;
                case 3:
                    objects.add(bb.getDouble());
                    break;
                case 4:
                    objects.add(new Timestamp(bb.getLong()));
                    break;
                case 5:
                    byte[] bytesLhmSize = new byte[4];
                    bb.get(bytesLhmSize);
                    byte[] bytesLhm = new byte[toInt(bytesLhmSize)];
                    bb.get(bytesLhm);
                    ArrayList<Object> lhmObjects = toObjects(bytesLhm);
                    LinkedHashMap<Object, Object> lhm = new LinkedHashMap<>();
                    for (int lhmi = 0; lhmi < lhmObjects.size(); lhmi++) {
                        lhm.put(lhmObjects.get(lhmi), lhmObjects.get(lhmi + 1));
                        lhmi++;
                    }
                    objects.add(lhm);
                    break;
            }

        }
        return objects;
    }
}
