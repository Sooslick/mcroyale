package ru.sooslick.royale.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import ru.sooslick.royale.RoyaleMessages;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameplayConfig {
    private static final Map<EntityType, Double> defaultChances = ImmutableMap.of(
            EntityType.ZOMBIE, 0.5D,
            EntityType.SKELETON, 0.5D,
            EntityType.CREEPER, 0.5D,
            EntityType.WITCH, 0.25D
    );

    public static boolean monstersEnabled = true;
    public static int monstersStartDelay = 310;
    public static Map<EntityType, Double> monstersSpawnChances = defaultChances;
    public static boolean elytraStartEnabled = false;
    public static int elytraFallHeight = 1600;
    public static boolean safeZoneMapEnabled = true;
    public static boolean safeZoneAlertEnabled = true;
    public static boolean safeZoneCompassEnabled = true;
    public static boolean safeZoneCommandEnabled = true;
    public static boolean lootTrackingEnabled = true;

    public static void readConfig(FileConfiguration cfg) {
        monstersEnabled = cfg.getBoolean("monstersEnabled", true);
        elytraStartEnabled = cfg.getBoolean("elytraStartEnabled", false);
        safeZoneMapEnabled = cfg.getBoolean("safeZoneMapEnabled", true);
        safeZoneAlertEnabled = cfg.getBoolean("safeZoneAlertEnabled", true);
        safeZoneCompassEnabled = cfg.getBoolean("safeZoneCompassEnabled", true);
        safeZoneCommandEnabled = cfg.getBoolean("safeZoneComandEnabled", true);
        lootTrackingEnabled = cfg.getBoolean("lootTrackingEnabled", true);

        if (monstersEnabled) {
            monstersStartDelay = cfg.getInt("monstersStartDelay", 310);
            Map<EntityType, Double> newMonsters = new HashMap<>();
            ConfigurationSection cs = cfg.getConfigurationSection("monstersSpawnChances");
            try {
                for (String type : cs.getKeys(false))
                    newMonsters.put(EntityType.valueOf(type), cs.getDouble(type));
            } catch (IllegalArgumentException e) {
                //todo log
                newMonsters = defaultChances;
            }
            monstersSpawnChances = newMonsters;
            //todo log reduced monsters
        }

        if (elytraStartEnabled) {
            elytraFallHeight = cfg.getInt("elytraFallHeight", 1600);
            if (elytraFallHeight < 256) elytraFallHeight = 256;
        }
    }

    private GameplayConfig() {
    }
}
