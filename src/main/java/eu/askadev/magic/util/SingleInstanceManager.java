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

    private static final String LOCK_FILE = ".magic.lock";
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
            // Only try to create parent directory if it exists and is not null
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            lockFile = new RandomAccessFile(file, "rw");
            lock = lockFile.getChannel().tryLock();

            if (lock == null) {
                // Could not acquire lock - another instance is running
                try {
                    lockFile.close();
                } catch (Exception e) {
                    // Ignore errors during close
                }
                lockFile = null;
                return false;
            }

            // Write timestamp to lock file for reference
            lockFile.seek(0);
            lockFile.writeUTF("M.A.G.I.C Instance lock - " + System.currentTimeMillis());
            lockFile.getChannel().force(false);

            System.out.println("INFO: Lock acquired successfully");
            return true;
        } catch (IOException e) {
            System.err.println("Error acquiring lock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Releases the lock when application is shutting down.
     * This method does not block and handles errors gracefully.
     */
    public static void releaseLock() {
        System.out.println("INFO: Attempting to release lock...");

        try {
            // Release lock first
            if (lock != null) {
                try {
                    lock.release();
                    System.out.println("INFO: Lock released");
                } catch (Exception e) {
                    System.err.println("Warning: Error releasing file lock: " + e.getMessage());
                }
                lock = null;
            }
        } catch (Exception e) {
            System.err.println("Warning: Error in lock release: " + e.getMessage());
        }

        try {
            // Close file
            if (lockFile != null) {
                try {
                    lockFile.close();
                    System.out.println("INFO: Lock file closed");
                } catch (Exception e) {
                    System.err.println("Warning: Error closing lock file: " + e.getMessage());
                }
                lockFile = null;
            }
        } catch (Exception e) {
            System.err.println("Warning: Error in file close: " + e.getMessage());
        }

        // Try to delete the lock file (non-blocking)
        try {
            File file = new File(LOCK_FILE);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("INFO: Lock file deleted");
                } else {
                    System.out.println("INFO: Could not delete lock file (may be in use)");
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not delete lock file: " + e.getMessage());
        }

        System.out.println("INFO: Lock release complete");
    }
}

