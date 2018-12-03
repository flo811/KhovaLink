package khovalink;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Class about logging errors in a file.
 *
 * @author flo
 */
public final class KhovaLog {

    private static final String LOG_NAME = "KhovaLog.log";
    private static final Logger LOG = Logger.getLogger(KhovaLink.class.getName());

    private static Handler fileHandler;
    private static boolean isLog = false;

    /**
     * Non instanciable class.
     */
    private KhovaLog() {
    }

    /**
     * Allows to log an exception.
     *
     * @param <E> The exception type.
     * @param ex The exception.
     */
    public static <E extends Exception> void addLog(final E ex) {
        if (!isLog) {
            isLog = true;
            init();
        }

        LOG.warning(ex.getMessage());
        System.err.println(ex.getMessage());
    }

    /**
     * Closes log file.
     */
    public static void close() {
        if (isLog) {
            fileHandler.close();
        }
    }

    /**
     * Initializes log file.
     */
    private static void init() {
        try {
            fileHandler = new FileHandler(LOG_NAME);
            LOG.addHandler(fileHandler);
        } catch (final IOException | SecurityException ex) {
            addLog(ex);
        }
    }
}
