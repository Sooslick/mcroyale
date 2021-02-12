package ru.sooslick.royale.config;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.sooslick.royale.RestrictedZoneTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ZoneConfig {
    private static final List<RestrictedZoneTemplate> defaultZones = ImmutableList.copyOf(Arrays.asList(
            new RestrictedZoneTemplate(1000, 300, 300, 0.01D),
            new RestrictedZoneTemplate(500, 180, 180, 0.05D),
            new RestrictedZoneTemplate(250, 90, 120, 0.25),
            new RestrictedZoneTemplate(125, 60, 120, 1)
    ));

    public static int zonePreStartSize = 1005;
    public static int zoneStartDelay = 60;
    public static boolean zoneCenterOffsetEnabled = true;
    public static int zoneLavaFlowSize = 16;
    public static int lavaFlowPeriod = 30;
    public static boolean outsideBreakingEnabled = true;
    public static int outsideBreakingMaxDistance = 3;
    public static List<RestrictedZoneTemplate> zones = defaultZones;

    public static void readConfig(FileConfiguration cfg) {
        zonePreStartSize = cfg.getInt("zonePreStartSize", 1005);
        zoneStartDelay = cfg.getInt("zoneStartDelay", 60);
        zoneCenterOffsetEnabled = cfg.getBoolean("zoneCenterOffsetEnabled", true);
        zoneLavaFlowSize = cfg.getInt("zoneLavaFlowSize", 16);
        lavaFlowPeriod = cfg.getInt("lavaFlowPeriod", 30);
        outsideBreakingEnabled = cfg.getBoolean("outsideBreakingEnabled", true);

        //validate
        if (zoneLavaFlowSize < 1) zoneLavaFlowSize = 1;
        if (zoneLavaFlowSize > 64) {}//todo lag alert}
        if (zonePreStartSize <= zoneLavaFlowSize) zonePreStartSize = zoneLavaFlowSize + 1;

        //break distance
        if (outsideBreakingEnabled) {
            outsideBreakingMaxDistance = cfg.getInt("outsideBreakingMaxDistance", 3);
            if (outsideBreakingMaxDistance > 16) {}//todo lag alert}
        }

        //read zones
        int zoneMax = zonePreStartSize;
        int zoneMin = zoneLavaFlowSize;
        List<RestrictedZoneTemplate> newZones = new LinkedList<>();
        try {
            for (Object obj : cfg.getList("zones")) {
                if (obj instanceof ConfigurationSection) {
                    ConfigurationSection cs = (ConfigurationSection) obj;
                    int newSize = cs.getInt("zoneSize");
                    if (newSize >= zoneMax || newSize <= zoneMin) {
                        throw new IllegalArgumentException();
                    }
                    newZones.add(new RestrictedZoneTemplate(
                            newSize,
                            cs.getInt("zoneDelay"),
                            cs.getInt("zoneShrinkTime"),
                            cs.getDouble("zoneDamage")
                    ));
                    zoneMax = newSize;
                } else {
                    throw new IllegalArgumentException();
                }
            }
            zones = newZones;
        } catch (Exception e) {
            //todo log
            zones = defaultZones;
        }
        //todo: log squares
    }

    private ZoneConfig() {}
}
