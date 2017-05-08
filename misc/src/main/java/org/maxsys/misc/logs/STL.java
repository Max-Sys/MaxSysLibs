package org.maxsys.misc.logs;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class STL {

    private static String logFileNamePrefix = "";
    private static final String FILE_TIME_STAMP = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Calendar.getInstance().getTime());
    private static volatile int LogFileCounter = 0;
    private static volatile int LineCounter = 0;
    private static volatile int LineCounterMax = 4096;

    public synchronized static void Log(String logText) {
        if (LineCounter >= LineCounterMax) {
            LogFileCounter += 1;
            LineCounter = 0;
        }

        BufferedWriter bw = null;
        try {
            String filen = logFileNamePrefix + FILE_TIME_STAMP + " - " + String.format("%04d", LogFileCounter) + ".log";
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filen, true), "UTF-8"));
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(STL.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (bw != null) {
            String nowTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
            try {
                bw.write(nowTimeStamp + ": " + logText);
                bw.newLine();
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(STL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        LineCounter++;
    }

    public synchronized static void LogSo(String logText) {
        System.out.println(logText);
        Log(logText);
    }

    public synchronized static String getLog(String filter) {
        StringBuilder log = new StringBuilder();
        try {
            String filen = logFileNamePrefix + FILE_TIME_STAMP + " - " + String.format("%04d", LogFileCounter) + ".log";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filen), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (filter.startsWith("-")) {
                        if (!line.toLowerCase().contains(filter.substring(1).toLowerCase())) {
                            log.append(line);
                            log.append("\n");
                        }
                    } else if (line.toLowerCase().contains(filter.toLowerCase())) {
                        log.append(line);
                        log.append("\n");
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(STL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(STL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(STL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return log.toString();
    }

    public static void setLineCounterMax(int LineCounterMax) {
        STL.LineCounterMax = LineCounterMax;
    }

    public static void setLogFileNamePrefix(String logFileNamePrefix) {
        STL.logFileNamePrefix = logFileNamePrefix;
        File logsfolder = new File(logFileNamePrefix);
        if (!logsfolder.exists()) {
            logsfolder.mkdirs();
        }
    }

}
