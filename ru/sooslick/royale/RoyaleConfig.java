package ru.sooslick.royale;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import static ru.sooslick.royale.RoyaleMessages.suffixNone;
import static ru.sooslick.royale.RoyaleMessages.suffixRed;

public class RoyaleConfig {

    private static Logger LOG;
    private static String YML;

    public static int zoneStartSize;
    public static int zonePreStartSize;
    public static int zoneStartTimer;
    public static int zoneStartDelay;
    public static int zoneEndSize;
    public static double zoneEndSpeed;
    public static double zoneNewSizeMultiplier;
    public static int zoneProcessorPeriod;
    public static double zoneWaitTimerMultiplier;
    public static double zoneShrinkTimerMultiplier;
    public static boolean zoneCenterOffsetEnabled;
    public static double zoneStartDamage;
    public static double zoneDamageMultiplier;
    public static int zoneLavaFlowSize;
    public static int lavaFlowPeriod;
    public static boolean redzoneEnabled;
    public static int redzoneRadius;
    public static int redzonePeriod;
    public static int redzoneDuration;
    public static int redzoneDensity;
    public static int redzoneStartDelay;
    public static int redzoneDelayMin;
    public static int redzoneDelayMax;
    public static int redzoneDisableSize;
    public static boolean monstersEnabled;
    public static int monstersStartDelay;
    public static HashMap<EntityType, Double> monstersSpawnChances;
    public static boolean elytraStartEnabled;
    public static int elytraFallHeight;
    public static int lobbyMinVotestarters;
    public static double lobbyMinVotestartersPercent;
    public static boolean lobbyPostGameCommandEnabled;
    public static String lobbyPostGameCommand;
    public static int lobbyPostGameCommandDelay;
    public static int squadMaxMembers;
    public static Team.OptionStatus squadNametagVisiblity;
    public static boolean squadFriendlyFireEnabled;
    public static boolean squadAutoBalancingEnabled;
    public static boolean gameOutsideBreakingEnabled;
    public static double gameOutsideBreakingMaxDistance;
    public static int gameOutsideBreakingPeriod;
    public static boolean gameGiveZoneMap;
    public static boolean gameContainerTrackingEnabled;
    public static Material gameContainerReplacmentMaterial;
    public static boolean airdropEnabled;
    public static boolean airdropAlertEnabled;
    public static int airdropStartDelay;
    public static int airdropDelayMin;
    public static int airdropDelayMax;
    public static int airdropDisableSize;
    public static HashMap<Material, Integer> airdropItems;
    public static double airdropEnchantedItemChance;
    public static HashMap<Material, HashMap<Enchantment, Integer>> airdropEnchantments;
    public static HashMap<PotionType, Integer> airdropPotions;
    public static HashMap<Material, Integer> airdropStackableItems;

    public RoyaleConfig() {
        setDefaults();
    }

    public static void setDefaults() {
        zoneStartSize = 2048;
        zonePreStartSize = 2055;
        zoneStartTimer = 300;
        zoneStartDelay = 60;
        zoneEndSize = 100;
        zoneEndSpeed = 0.5;
        zoneNewSizeMultiplier = 0.5;
        zoneProcessorPeriod = 10;
        zoneWaitTimerMultiplier = 0.75;
        zoneShrinkTimerMultiplier = 0.66;
        zoneCenterOffsetEnabled = true;
        zoneStartDamage = 0.01;
        zoneDamageMultiplier = 2;
        zoneLavaFlowSize = 16;
        lavaFlowPeriod = 30;
        redzoneEnabled = true;
        redzoneRadius = 25;
        redzonePeriod = 10;
        redzoneDuration = 20;
        redzoneDensity = 5;
        redzoneStartDelay = 350;
        redzoneDelayMin = 60;
        redzoneDelayMax = 120;
        redzoneDisableSize = 250;
        monstersEnabled = true;
        monstersStartDelay = 310;
        monstersSpawnChances = getDefaultMonsters();
        elytraStartEnabled = true;
        elytraFallHeight = 1600;
        lobbyMinVotestarters = 3;
        lobbyMinVotestartersPercent = 0.51;
        lobbyPostGameCommandEnabled = true;
        lobbyPostGameCommand = "say post-game event triggered";
        lobbyPostGameCommandDelay = 60;
        squadMaxMembers = 4;
        squadNametagVisiblity = Team.OptionStatus.FOR_OWN_TEAM;
        squadFriendlyFireEnabled = true;
        squadAutoBalancingEnabled = true;
        gameOutsideBreakingEnabled = true;
        gameOutsideBreakingMaxDistance = 3;
        gameOutsideBreakingPeriod = 10;
        gameGiveZoneMap = true;
        gameContainerTrackingEnabled = true;
        gameContainerReplacmentMaterial = Material.MOSSY_COBBLESTONE;
        airdropEnabled = true;
        airdropAlertEnabled = true;
        airdropStartDelay = 250;
        airdropDelayMin = 120;
        airdropDelayMax = 240;
        airdropDisableSize = 300;
        airdropItems = getDefaultAirdropItems();
        airdropEnchantedItemChance = 0.1;
        airdropEnchantments = getDefaultEnchantments();
        airdropPotions = getDefaultPotions();
        airdropStackableItems = getDefaultStackables();
    }

    public static HashMap<EntityType, Double> getDefaultMonsters() {
        HashMap<EntityType, Double> hm = new HashMap<>();
        hm.put(EntityType.ZOMBIE, 0.5);
        hm.put(EntityType.SKELETON, 0.5);
        hm.put(EntityType.CREEPER, 0.5);
        hm.put(EntityType.WITCH, 0.25);
        return hm;
    }

    public static HashMap<Material, Integer> getDefaultAirdropItems() {
        HashMap<Material, Integer> hm = new HashMap<>();
        hm.put(Material.AIR, 200);
        hm.put(Material.COBBLESTONE, 100);
        hm.put(Material.ARROW, 20);
        hm.put(Material.BOW, 10);
        hm.put(Material.IRON_SWORD, 2);
        hm.put(Material.SPLASH_POTION, 1);
        return hm;
    }

    public static HashMap<Material, HashMap<Enchantment, Integer>> getDefaultEnchantments() {
        HashMap<Material, HashMap<Enchantment, Integer>> hmm = new HashMap<>();
        HashMap<Enchantment, Integer> hme = new HashMap<>();
        hme.put(Enchantment.ARROW_DAMAGE, 3);
        hme.put(Enchantment.ARROW_KNOCKBACK, 1);
        hmm.put(Material.BOW, hme);
        hme = new HashMap<>();
        hme.put(Enchantment.DAMAGE_ALL, 4);
        hme.put(Enchantment.KNOCKBACK, 1);
        hmm.put(Material.IRON_SWORD, hme);
        return hmm;
    }

    public static HashMap<PotionType, Integer> getDefaultPotions() {
        HashMap<PotionType, Integer> hm = new HashMap<>();
        hm.put(PotionType.INSTANT_HEAL, 10);
        hm.put(PotionType.SPEED, 8);
        hm.put(PotionType.STRENGTH, 5);
        return hm;
    }

    public static HashMap<Material, Integer> getDefaultStackables() {
        HashMap<Material, Integer> hm = new HashMap<>();
        hm.put(Material.COBBLESTONE, 64);
        hm.put(Material.ARROW, 16);
        return hm;
    }

    public static void readConfig(FileConfiguration cfg) {
        cfgGetInt(cfg, "zoneStartSize", 2048, (val, def) -> {
            if (val < 1 || val > 4096) return def;
            else return val;
        });
        cfgGetInt(cfg, "zonePreStartSize", zoneStartSize + 5, (val, def) -> {
            if (val < zoneStartSize || val > 4200) return def;
            else return val;
        });
        cfgGetInt(cfg, "zoneStartDelay", 60, (val, def) -> {
            if (val < 0 || val > 300) return def;
            else return val;
        });
        cfgGetInt(cfg, "zoneStartTimer", 300, (val, def) -> {
            if (val < 60 || val > 1000) return def;
            else return val;
        });
        cfgGetInt(cfg, "zoneEndSize", 100, (val, def) -> {
            if (val < 1 || val > zoneStartSize) return def;
            else return val;
        });
        cfgGetDouble(cfg, "zoneEndSpeed", 0.5, (val, def) -> {
            if (val <= 0 || val > 5) return def;
            else return val;
        });
        cfgGetDouble(cfg, "zoneNewSizeMultiplier", 0.5, (val, def) -> {
            if (val <= 0 || val >= 1) return def;
            else return val;
        });
        cfgGetInt(cfg, "zoneProcessorPeriod", 10, (val, def) -> {
            if (val < 1 || val > 20) return def;
            else return val;
        });
        cfgGetDouble(cfg, "zoneWaitTimerMultiplier", 0.75, (val, def) -> {
            if (val <= 0 || val > 2) return def;
            else return val;
        });
        cfgGetDouble(cfg, "zoneShrinkTimerMultiplier", 0.666, (val, def) -> {
            if (val <= 0 || val > 2) return def;
            else return val;
        });
        cfgGetBoolean(cfg, "zoneCenterOffsetEnabled", true);
        cfgGetDouble(cfg, "zoneStartDamage", 0.01, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetDouble(cfg, "zoneDamageMultiplier", 2, (val, def) -> {
            if (val < 1) return def;
            else return val;
        });
        cfgGetInt(cfg, "zoneLavaFlowSize", 16, (val, def) -> {
            if (val < 1 || val > 32) return def;
            else return val;
        });
        cfgGetInt(cfg, "lavaFlowPeriod", 20, (val, def) -> {
            if (val < 10) return def;
            else return val;
        });
        cfgGetBoolean(cfg, "redzoneEnabled", true);
        cfgGetInt(cfg, "redzoneRadius", 25, (val, def) -> {
            if (val < 1 || val > zoneStartSize / 2) return def;
            else return val;
        });
        cfgGetInt(cfg, "redzonePeriod", 10, (val, def) -> {
            if (val < 1 || val > 100) return def;
            else return val;
        });
        cfgGetInt(cfg, "redzoneDuration", 10, (val, def) -> {
            if (val < 1 || val > 60) return def;
            else return val;
        });
        cfgGetInt(cfg, "redzoneDensity", 5, (val, def) -> {
            if (val < 1 || val > redzoneRadius) return def;
            else return val;
        });
        cfgGetInt(cfg, "redzoneStartDelay", 350, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetInt(cfg, "redzoneDelayMin", 60, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetInt(cfg, "redzoneDelayMax", 120, (val, def) -> {
            if (val < redzoneDelayMin) return def;
            else return val;
        });
        cfgGetInt(cfg, "redzoneDisableSize", 250, (val, def) -> {
            if (val < zoneLavaFlowSize) return def;
            else return val;
        });
        cfgGetBoolean(cfg, "monstersEnabled", true);
        cfgGetInt(cfg, "monstersStartDelay", 310, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetBoolean(cfg, "elytraStartEnabled", true);
        cfgGetInt(cfg, "elytraFallHeight", 1600, (val, def) -> {
            if (val < 256) return def;
            else return val;
        });
        cfgGetInt(cfg, "lobbyMinVotestarters", 3, (val, def) -> {
            if (val < 1) return def;
            else return val;
        });
        cfgGetDouble(cfg, "lobbyMinVotestartersPercent", 0.51, (val, def) -> {
            if (val <= 0 || val > 1) return def;
            else return val;
        });
        cfgGetBoolean(cfg, "lobbyPostGameCommandEnabled", true);
        cfgGetInt(cfg, "lobbyPostGameCommandDelay", 60, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetInt(cfg, "squadMaxMembers", 4, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetBoolean(cfg, "squadFriendlyFireEnabled", true);
        cfgGetBoolean(cfg, "squadAutoBalancingEnabled", true);
        cfgGetBoolean(cfg, "gameOutsideBreakingEnabled", true);
        cfgGetDouble(cfg, "gameOutsideBreakingMaxDistance", 3, (val, def) -> {
            if (val < 2 || val > 10) return def;
            else return val;
        });
        cfgGetInt(cfg, "gameOutsideBreakingPeriod", 5, (val, def) -> {
            if (val < 1) return def;
            else return val;
        });
        cfgGetBoolean(cfg, "gameGiveZoneMap", true);
        cfgGetBoolean(cfg, "gameContainerTrackingEnabled", true);
        cfgGetBoolean(cfg, "airdropEnabled", true);
        cfgGetBoolean(cfg, "airdropAlertEnabled", true);
        cfgGetInt(cfg, "airdropStartDelay", 250, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetInt(cfg, "airdropDelayMin", 150, (val, def) -> {
            if (val < 0) return def;
            else return val;
        });
        cfgGetInt(cfg, "airdropDelayMax", 250, (val, def) -> {
            if (val < airdropDelayMin) return def;
            else return val;
        });
        cfgGetInt(cfg, "airdropDisableSize", zoneEndSize, (val, def) -> {
            if (val < zoneLavaFlowSize) return def;
            else return val;
        });
        cfgGetDouble(cfg, "airdropEnchantedItemChance", 0.1, (val, def) -> {
            if (val < 0 || val > 1) return def;
            else return val;
        });

        //todo: refactor sections

        //monsterSpawnChances configurationSection
        ConfigurationSection cs = cfg.getConfigurationSection("monstersSpawnChances");
        Set<String> monsters = cs.getKeys(false);
        monstersSpawnChances = new HashMap<>();
        for (String s : monsters) {
            try {
                monstersSpawnChances.put(EntityType.valueOf(s), cs.getDouble(s));
            } catch (IllegalArgumentException e) {
                LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
            }
        }
        LOG.info(RoyaleMessages.prefix + RoyaleMessages.monstersSpawnChances);

        //todo: cfgGetString method
        String suffix = suffixNone;
        lobbyPostGameCommand = cfg.getString("PostGameCommand", "say post-game event triggered!");
        if (lobbyPostGameCommand.length() == 0) {
            lobbyPostGameCommand = "say post-game event triggered!";
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.lobbyPostGameCommand, lobbyPostGameCommand));
        suffix = suffixNone;

        try {
            squadNametagVisiblity = Team.OptionStatus.valueOf(cfg.getString("NametagVisiblity", "FOR_OWN_TEAM"));
        } catch (IllegalArgumentException e) {
            squadNametagVisiblity = Team.OptionStatus.FOR_OWN_TEAM;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.squadNametagVisiblity, squadNametagVisiblity));
        suffix = suffixNone;

        try {
            gameContainerReplacmentMaterial = Material.valueOf(cfg.getString("RestoreChestBlock", "MOSSY_COBBLESTONE"));
        } catch (IllegalArgumentException e) {
            gameContainerReplacmentMaterial = Material.MOSSY_COBBLESTONE;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.gameContainerReplacmentMaterial, gameContainerReplacmentMaterial));
        suffix = suffixNone;

        //airdropItems configurationSection
        cs = cfg.getConfigurationSection("airdropItems");
        Set<String> items = cs.getKeys(false);
        airdropItems = new HashMap<>();
        for (String s : items) {
            try {
                airdropItems.put(Material.valueOf(s), cs.getInt(s));
            } catch (IllegalArgumentException e) {
                LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
            }
        }
        LOG.info(RoyaleMessages.prefix + RoyaleMessages.airdropItems);

        //airdropEnchantments configurationSection
        //todo variables names - pizdec
        cs = cfg.getConfigurationSection("airdropEnchantments");
        items = cs.getKeys(false);
        airdropEnchantments = new HashMap<>();
        for (String s : items) {
            ConfigurationSection ae = cs.getConfigurationSection(s);
            HashMap<Enchantment, Integer> hm = new HashMap<>();
            for (String enc : ae.getKeys(false)) {
                try {
                    hm.put(Enchantment.getByName(enc), ae.getInt(enc));
                } catch (Exception e) {
                    LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
                }
            }
            try {
                airdropEnchantments.put(Material.valueOf(s), hm);
            } catch (IllegalArgumentException e) {
                LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
            }
        }
        LOG.info(RoyaleMessages.prefix + RoyaleMessages.airdropEnchantments);

        //airdropPotions configurationSection
        cs = cfg.getConfigurationSection("airdropPotions");
        Set<String> pots = cs.getKeys(false);
        airdropPotions = new HashMap<>();
        for (String s : pots) {
            try {
                airdropPotions.put(PotionType.valueOf(s), cs.getInt(s));
            } catch (IllegalArgumentException e) {
                LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
            }
        }
        LOG.info(RoyaleMessages.prefix + RoyaleMessages.airdropPotions);

        //airdropStackableItems configurationSection
        cs = cfg.getConfigurationSection("airdropStackableItems");
        items = cs.getKeys(false);
        airdropStackableItems = new HashMap<>();
        for (String s : items) {
            try {
                airdropStackableItems.put(Material.valueOf(s), cs.getInt(s));
            } catch (IllegalArgumentException e) {
                LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
            }
        }
        LOG.info(RoyaleMessages.prefix + RoyaleMessages.airdropStackableItems);

        //todo: refactor if possible
        //todo: comment config fields in yml

        //todo: new features
        //     - map generator
        //    - loot spawn
        //   - background map loading / chest tracking
        //  - portal / beacon features
        // - etc etc etc...
    }

    public static boolean saveConfig(FileConfiguration cfg) {
        cfg.set("zoneStartSize", zoneStartSize);
        cfg.set("zonePreStartSize", zonePreStartSize);
        cfg.set("zoneStartTimer", zoneStartTimer);
        cfg.set("zoneStartDelay", zoneStartDelay);
        cfg.set("zoneEndSize", zoneEndSize);
        cfg.set("zoneEndSpeed", zoneEndSpeed);
        cfg.set("zoneNewSizeMultiplier", zoneNewSizeMultiplier);
        cfg.set("zoneProcessorPeriod", zoneProcessorPeriod);
        cfg.set("zoneWaitTimerMultiplier", zoneWaitTimerMultiplier);
        cfg.set("zoneShrinkTimerMultiplier", zoneShrinkTimerMultiplier);
        cfg.set("zoneCenterOffsetEnabled", zoneCenterOffsetEnabled);
        cfg.set("zoneStartDamage", zoneStartDamage);
        cfg.set("zoneDamageMultiplier", zoneDamageMultiplier);
        cfg.set("zoneLavaFlowSize", zoneLavaFlowSize);
        cfg.set("lavaFlowPeriod", lavaFlowPeriod);
        cfg.set("redzoneEnabled", redzoneEnabled);
        cfg.set("redzoneRadius", redzoneRadius);
        cfg.set("redzonePeriod", redzonePeriod);
        cfg.set("redzoneDuration", redzoneDuration);
        cfg.set("redzoneDensity", redzoneDensity);
        cfg.set("redzoneStartDelay", redzoneStartDelay);
        cfg.set("redzoneDelayMin", redzoneDelayMin);
        cfg.set("redzoneDelayMax", redzoneDelayMax);
        cfg.set("redzoneDisableSize", redzoneDisableSize);
        cfg.set("monstersEnabled", monstersEnabled);
        cfg.set("monstersStartDelay", monstersStartDelay);
        saveSection(cfg, "monstersSpawnChances", monstersSpawnChances);
        cfg.set("elytraStartEnabled", elytraStartEnabled);
        cfg.set("elytraFallHeight", elytraFallHeight);
        cfg.set("lobbyMinVotestarters", lobbyMinVotestarters);
        cfg.set("lobbyMinVotestartersPercent", lobbyMinVotestartersPercent);
        cfg.set("lobbyPostGameCommandEnabled", lobbyPostGameCommandEnabled);
        cfg.set("lobbyPostGameCommand", lobbyPostGameCommand);
        cfg.set("lobbyPostGameCommandDelay", lobbyPostGameCommandDelay);
        cfg.set("squadMaxMembers", squadMaxMembers);
        cfg.set("squadNametagVisiblity", squadNametagVisiblity);
        cfg.set("squadFriendlyFireEnabled", squadFriendlyFireEnabled);
        cfg.set("squadAutoBalancingEnabled", squadAutoBalancingEnabled);
        cfg.set("gameOutsideBreakingEnabled", gameOutsideBreakingEnabled);
        cfg.set("gameOutsideBreakingMaxDistance", gameOutsideBreakingMaxDistance);
        cfg.set("gameOutsideBreakingPeriod", gameOutsideBreakingPeriod);
        cfg.set("gameGiveZoneMap", gameGiveZoneMap);
        cfg.set("gameContainerTrackingEnabled", gameContainerTrackingEnabled);
        cfg.set("gameContainerReplacmentMaterial", gameContainerReplacmentMaterial);
        cfg.set("airdropEnabled", airdropEnabled);
        cfg.set("airdropAlertEnabled", airdropAlertEnabled);
        cfg.set("airdropStartDelay", airdropStartDelay);
        cfg.set("airdropDelayMin", airdropDelayMin);
        cfg.set("airdropDelayMax", airdropDelayMax);
        cfg.set("airdropDisableSize", airdropDisableSize);
        saveSection(cfg, "airdropItems", airdropItems);
        cfg.set("airdropEnchantedItemChance", airdropEnchantedItemChance);
        //todo: check cycle below // saveSection(cfg, "airdropEnchantments", airdropEnchantments);
        ConfigurationSection cs = cfg.createSection("airdropEnchantments");
        for (Material m : airdropEnchantments.keySet()) {
            saveSection(cs, m.toString(), airdropEnchantments.get(m));
        }
        saveSection(cfg, "airdropPotions", airdropPotions);
        saveSection(cfg, "airdropStackableItems", airdropStackableItems);
        try {
            cfg.save(YML);
            return true;
        } catch (IOException e) {
            LOG.warning(suffixRed + RoyaleMessages.prefix + RoyaleMessages.writeConfigException);
            return false;
        }
    }

    public static <T, V> void saveSection(ConfigurationSection cfg, String section, HashMap<T, V> values) {
        ConfigurationSection cs = cfg.createSection(section);
        for (T key : values.keySet()) {
            cs.set(key.toString(), values.get(key));
        }
        //todo: test method
        //todo: if V is conf section? Impl recursive method
    }

    public static void setLogger(Logger l) {
        LOG = l;
    }

    public static void setConfigFile(String file) {
        YML = file;
    }

    private static void cfgGetInt(FileConfiguration cfg, String field, int dflt, IntValidator v) {
        try {
            boolean useSuffix = false;
            Field rcf = RoyaleConfig.class.getDeclaredField(field);
            Field rmf = RoyaleMessages.class.getDeclaredField(field);
            int orig = cfg.getInt(field, dflt);
            int fixed = v.validate(orig, dflt);
            if (orig != fixed) {
                useSuffix = true;
            }
            rcf.set(RoyaleConfig.class, fixed);
            LOG.info(RoyaleMessages.prefix + (useSuffix ? suffixRed : suffixNone)
                    + String.format((String) rmf.get(RoyaleMessages.class), (int) rcf.get(RoyaleConfig.class)));
        } catch (NoSuchFieldException e) {
            LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchConfigField, field));
        } catch (IllegalAccessException e) {
            LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.cantWriteConfigField, field));
        }
    }

    private static void cfgGetDouble(FileConfiguration cfg, String field, double dflt, DoubleValidator v) {
        try {
            boolean useSuffix = false;
            Field rcf = RoyaleConfig.class.getDeclaredField(field);
            Field rmf = RoyaleMessages.class.getDeclaredField(field);
            double orig = cfg.getDouble(field, dflt);
            double fixed = v.validate(orig, dflt);
            if (orig != fixed) {
                useSuffix = true;
            }
            rcf.set(RoyaleConfig.class, fixed);
            LOG.info(RoyaleMessages.prefix + (useSuffix ? suffixRed : suffixNone)
                    + String.format((String) rmf.get(RoyaleMessages.class), (double) rcf.get(RoyaleConfig.class)));
        } catch (NoSuchFieldException e) {
            LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchConfigField, field));
        } catch (IllegalAccessException e) {
            LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.cantWriteConfigField, field));
        }
    }

    private static void cfgGetBoolean(FileConfiguration cfg, String field, boolean dflt) {
        try {
            Field rcf = RoyaleConfig.class.getDeclaredField(field);
            Field rmf = RoyaleMessages.class.getDeclaredField(field);
            rcf.set(RoyaleConfig.class, cfg.getBoolean(field, dflt));
            LOG.info(RoyaleMessages.prefix + String.format((String) rmf.get(RoyaleMessages.class), (boolean) rcf.get(RoyaleConfig.class)));
        } catch (NoSuchFieldException e) {
            LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchConfigField, field));
        } catch (IllegalAccessException e) {
            LOG.warning(suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.cantWriteConfigField, field));
        }

    }

    //todo: 95% of duplicates: int, cfgGetDouble, Boolean, etc...
    //todo: can refactor validators in one class?

    @FunctionalInterface
    public interface DoubleValidator {

        double validate(double value, double dflt);

    }

    @FunctionalInterface
    public interface IntValidator {

        int validate(int value, int dflt);

    }

}
