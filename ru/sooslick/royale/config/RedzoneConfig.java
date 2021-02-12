package ru.sooslick.royale.config;

import org.bukkit.configuration.file.FileConfiguration;

public class RedzoneConfig {
    public static boolean redzoneEnabled = false;
    public static int redzoneRadius = 25;
    public static int redzonePeriod = 10;
    public static int redzoneDuration = 20;
    public static int redzoneDensity = 5;
    public static int redzoneStartDelay = 350;
    public static int redzoneDelayMin = 60;
    public static int redzoneDelayMax = 120;
    public static int redzoneDisableSize = 250;

    public static void readConfig(FileConfiguration cfg) {
        redzoneEnabled = cfg.getBoolean("redzoneEnabled", false);
        if (redzoneEnabled) {
            redzoneRadius = cfg.getInt("redzoneRadius", 25);
            redzonePeriod = cfg.getInt("redzonePeriod", 10);
            redzoneDuration = cfg.getInt("redzoneDuration", 20);
            redzoneDensity = cfg.getInt("redzoneDensity", 5);
            redzoneStartDelay = cfg.getInt("redzoneStartDelay", 350);
            redzoneDelayMin = cfg.getInt("redzoneDelayMin", 60);
            redzoneDelayMax = cfg.getInt("redzoneDelayMax", 120);
            redzoneDisableSize = cfg.getInt("redzoneDisableSize", 250);

            //validate
            if (redzoneDensity < 1) redzoneDensity = 1;
            if (redzoneDelayMin > redzoneDelayMax) redzoneDelayMin = redzoneDelayMax;
            if (redzoneDisableSize < ZoneConfig.zoneLavaFlowSize) redzoneDisableSize = ZoneConfig.zoneLavaFlowSize + 1;
        }
    }

    private RedzoneConfig() {}
}
