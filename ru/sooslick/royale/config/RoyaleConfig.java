package ru.sooslick.royale.config;

import org.bukkit.configuration.file.FileConfiguration;
import ru.sooslick.royale.Royale;

public class RoyaleConfig {

    public static void readConfig() {
        FileConfiguration cfg = Royale.R.getConfig();
        LobbyConfig.readConfig(cfg);
        ZoneConfig.readConfig(cfg);
        RedzoneConfig.readConfig(cfg);
        GameplayConfig.readConfig(cfg);
        AirdropConfig.readConfig(cfg);
    }
}
