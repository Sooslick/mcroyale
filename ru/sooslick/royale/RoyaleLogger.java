package ru.sooslick.royale;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class RoyaleLogger {
    private static Logger LOG;

    private static final String PREFIX = "[debug] ";
    private static final Consumer<String> DEBUG_OFF = (s) -> LOG.fine(s);
    private static final Consumer<String> DEBUG_ON = (s) -> LOG.info(PREFIX + s);

    private static Consumer<String> DEBUG = DEBUG_OFF;

    public static void initWith(Logger logger) {
        LOG = logger;
    }

    public static void setDebugMode(boolean enable) {
        DEBUG = enable ? DEBUG_ON : DEBUG_OFF;
    }

    public static void warn(String warn) {
        LOG.warning(warn);
    }

    public static void info(String info) {
        LOG.info(info);
    }

    public static void debug(String debug) {
        DEBUG.accept(debug);
    }
}
