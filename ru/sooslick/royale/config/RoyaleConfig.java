package ru.sooslick.royale.config;

import org.bukkit.configuration.file.FileConfiguration;
import ru.sooslick.royale.Royale;
import ru.sooslick.royale.RoyaleLogger;

public class RoyaleConfig {
    public static boolean debugMode;

    public static void readConfig() {
        FileConfiguration cfg = Royale.R.getConfig();
        LobbyConfig.readConfig(cfg);
        ZoneConfig.readConfig(cfg);
        RedzoneConfig.readConfig(cfg);
        GameplayConfig.readConfig(cfg);
        AirdropConfig.readConfig(cfg);

        debugMode = cfg.getBoolean("debugMode", false);
        RoyaleLogger.setDebugMode(debugMode);
    }
}
