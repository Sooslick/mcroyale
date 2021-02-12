package ru.sooslick.royale;

import java.util.logging.Logger;

public class RoyaleLogger {
    private static Logger LOG;

    public static void initWith(Logger logger) {
        LOG = logger;
    }

    public static void warn(String warn) {
        LOG.warning(warn);
    }

    public static void info(String info) {
        LOG.info(info);
    }
}
