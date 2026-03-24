package eu.askadev.magic.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * Ensures only one instance of the application can run at a time.
 * Uses a lock file to track if another instance is running.
 */
public class SingleInstanceManager {

    private static final String LOCK_FILE = ".app-genia.lock";
    private static RandomAccessFile lockFile;
    private static FileLock lock;

    /**
     * Attempts to acquire a lock for single instance.
     * @return true if lock acquired (this is the first/only instance)
     * @return false if another instance is already running
     */
    public static boolean acquireLock() {
        try {
            File file = new File(LOCK_FILE);
            lockFile = new RandomAccessFile(file, "rw");
            lock = lockFile.getChannel().tryLock();

            if (lock == null) {
                // Could not acquire lock - another instance is running
                lockFile.close();
                return false;
            }

            // Write PID to lock file for reference
            lockFile.seek(0);
            lockFile.writeUTF("Instance lock - " + System.currentTimeMillis());

            return true;
        } catch (IOException e) {
            System.err.println("Error acquiring lock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Releases the lock when application is shutting down.
     */
    public static void releaseLock() {
        try {
            if (lock != null) {
                lock.release();
            }
            if (lockFile != null) {
                lockFile.close();
            }
            // Delete the lock file
            File file = new File(LOCK_FILE);
            file.delete();
        } catch (IOException e) {
            System.err.println("Error releasing lock: " + e.getMessage());
        }
    }
}

