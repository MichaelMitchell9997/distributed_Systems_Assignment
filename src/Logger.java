import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

// Defines a Logger class for logging time stamps of the start and finish of critical section and buffer size to a file.
public class Logger {
    private final String filePath;
    private static Logger instance;

    private Logger(String filePath) {
        this.filePath = filePath;
    }

    public static synchronized Logger getInstance(String filePath) {
        // If the instance is null, create a new Logger instance with the provided file path.
        if (instance == null) {
            instance = new Logger(filePath);
        }
        // Return the existing/newly created Logger instance.
        return instance;
    }

    // Synchronized method to log a message to the file. Synchronization ensures thread safety.
    public synchronized void log(String message) {
        try (RandomAccessFile log = new RandomAccessFile(filePath, "rw")) {
            // Move to the end of the file to append the new message.
            log.seek(log.length());
            // Write the message along with the current timestamp to the file.
            log.writeBytes(System.lineSeparator() + message + " at " + new Date().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method used to clear the log file content when the Coordinator is started
    public static void resetLogFile() {
        try (RandomAccessFile log = new RandomAccessFile("log.txt", "rw")) {
            // Set the length of the file to 0, clearing it.
            log.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
