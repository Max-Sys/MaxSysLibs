package org.maxsys.misc;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunLocks {

    public static boolean isLock_Or_If_Not_CreateLockAndHook(String lockID) {
        File lockHomeDir = new File(System.getProperty("user.home") + "/.RunLocks");
        if (!lockHomeDir.exists()) {
            lockHomeDir.mkdirs();
        }

        String lockFileName = System.getProperty("user.home") + "/.RunLocks/" + lockID + ".lock";
        File lockFile = new File(lockFileName);
        if (lockFile.exists()) {
            return true;
        }

        try {
            lockFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(RunLocks.class.getName()).log(Level.SEVERE, null, ex);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            lockFile.delete();
        }, "RunLocksHook"));

        return false;
    }

    public static void deleteLockFile(String lockID) {
        String lockFileName = System.getProperty("user.home") + "/.RunLocks/" + lockID + ".lock";
        File lockFile = new File(lockFileName);
        lockFile.delete();
    }

    public static String getLockFileName(String lockID) {
        return System.getProperty("user.home") + "/.RunLocks/" + lockID + ".lock";
    }

}
