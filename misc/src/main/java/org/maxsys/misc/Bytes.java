package org.maxsys.misc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    public static byte[] intsToBytes(int[] ints) {
        ByteBuffer bb = ByteBuffer.allocate(ints.length * 4);
        for (int i : ints) {
            bb.putInt(i);
        }
        return bb.array();
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length == 4) {
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            return -1;
        }
    }

    public static int[] bytesToInts(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        int[] ints = new int[bytes.length / 4];
        for (int ii = 0; ii < ints.length; ii++) {
            ints[ii] = bb.getInt();
        }
        return ints;
    }

    public static byte[] stringToBytes(String string) {
        try {
            byte[] bytesString = string.getBytes("UTF-8");
            byte[] bytesStringSize = Bytes.intToBytes(bytesString.length);
            ByteBuffer bb = ByteBuffer.allocate(bytesStringSize.length + bytesString.length);
            bb.put(bytesStringSize);
            bb.put(bytesString);
            return bb.array();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
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

    public static String bytesToString(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        byte[] bytesStringSize = new byte[4];
        bb.get(bytesStringSize);
        byte[] bytesString = new byte[Bytes.bytesToInt(bytesStringSize)];
        bb.get(bytesString);
        try {
            return new String(bytesString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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

    public static byte[] doubleToBytes(double d) {
        return ByteBuffer.allocate(8).putDouble(d).array();
    }

    public static byte[] doublesToBytes(double[] doubles) {
        ByteBuffer bb = ByteBuffer.allocate(doubles.length * 8);
        for (double d : doubles) {
            bb.putDouble(d);
        }
        return bb.array();
    }

    public static double bytesToDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static double[] bytesToDoubles(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        double[] doubles = new double[bytes.length / 8];
        for (int di = 0; di < doubles.length; di++) {
            doubles[di] = bb.getDouble();
        }
        return doubles;
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

    public static byte[] timestampToBytes(Timestamp timestamp) {
        return ByteBuffer.allocate(8).putLong(timestamp.getTime()).array();
    }

    public static byte[] timestampsToBytes(Timestamp[] timestamps) {
        ByteBuffer bb = ByteBuffer.allocate(timestamps.length * 8);
        for (Timestamp ts : timestamps) {
            bb.putLong(ts.getTime());
        }
        return bb.array();
    }

    public static Timestamp bytesToTimestamp(byte[] bytes) {
        return new Timestamp(ByteBuffer.wrap(bytes).getLong());
    }

    public static Timestamp[] bytesToTimestamps(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        Timestamp[] timestamps = new Timestamp[bytes.length / 8];
        for (int tsi = 0; tsi < timestamps.length; tsi++) {
            timestamps[tsi] = new Timestamp(bb.getLong());
        }
        return timestamps;
    }

    public static byte[] linkedHashMapTimestampDoubleToBytes(LinkedHashMap<Timestamp, Double> map) {
        ByteBuffer bb = ByteBuffer.allocate(map.size() * 16);
        map.keySet().stream().forEach((ts) -> {
            bb.putLong(ts.getTime());
            bb.putDouble(map.get(ts));
        });
        return bb.array();
    }

    public static LinkedHashMap<Timestamp, Double> bytesToLinkedHashMapTimestampDouble(byte[] bytes) {
        LinkedHashMap<Timestamp, Double> map = new LinkedHashMap<>();
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int tmi = 0; tmi < bytes.length / 16; tmi++) {
            Timestamp ts = new Timestamp(bb.getLong());
            Double val = bb.getDouble();
            map.put(ts, val);
        }
        return map;
    }

    static byte[] objectsToBytes(ArrayList<Object> objects) {
        //[[byte class (1,2,3,4...)][byte[] bytes]]...
        ArrayList<byte[]> bytesAL = new ArrayList<>();
        int bytesSize = 0;
        for (Object o : objects) {
            switch (o.getClass().getCanonicalName()) {
                case "java.lang.String": //class 1
                    byte[] stringBytes = stringToBytes((String) o);
                    bytesAL.add(new byte[]{1});
                    bytesAL.add(stringBytes);
                    bytesSize += stringBytes.length + 1;
                    break;
                case "java.lang.Integer": //class 2
                    bytesAL.add(new byte[]{2});
                    bytesAL.add(intToBytes((int) o));
                    bytesSize += 5;
                    break;
                case "java.lang.Double": //class 3
                    bytesAL.add(new byte[]{3});
                    bytesAL.add(doubleToBytes((double) o));
                    bytesSize += 9;
                    break;
                case "java.sql.Timestamp": //class 4
                    bytesAL.add(new byte[]{4});
                    bytesAL.add(timestampToBytes((Timestamp) o));
                    bytesSize += 9;
                    break;
                case "java.util.LinkedHashMap": //class 5
                    bytesAL.add(new byte[]{5});

                    ArrayList<Object> lhmObjests = new ArrayList<>();
                    for (Object lhmK : ((LinkedHashMap) o).keySet()) {
                        lhmObjests.add(lhmK);
                        lhmObjests.add(((LinkedHashMap) o).get(lhmK));
                    }

                    byte[] lhmObjectsBytes = objectsToBytes(lhmObjests);
                    byte[] lhmObjectsBytesSize = intToBytes(lhmObjectsBytes.length);

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

    static ArrayList<Object> bytesToObjects(byte[] bytes) {
        ArrayList<Object> objects = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        while (bb.hasRemaining()) {
            byte c = bb.get();
            switch (c) {
                case 1:
                    byte[] bytesStringSize = new byte[4];
                    bb.get(bytesStringSize);
                    byte[] bytesString = new byte[bytesToInt(bytesStringSize)];
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
                    byte[] bytesLhm = new byte[bytesToInt(bytesLhmSize)];
                    bb.get(bytesLhm);
                    ArrayList<Object> lhmObjects = bytesToObjects(bytesLhm);
                    LinkedHashMap lhm = new LinkedHashMap();
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
