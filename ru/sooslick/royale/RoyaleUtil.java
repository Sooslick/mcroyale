package ru.sooslick.royale;

import java.util.logging.Logger;

public class RoyaleUtil {

    public static Logger LOG;

    public static void logInfo(String msg) {
        LOG.info(RoyaleMessages.prefix + msg);
    }

    public static void logWarning(String msg) {
        LOG.warning(RoyaleMessages.suffixYellow + RoyaleMessages.prefix + msg);
    }

}
