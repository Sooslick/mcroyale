package ru.sooslick.royale;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Team;
import ru.sooslick.royale.Validators.DoubleValidator;
import ru.sooslick.royale.Validators.IntValidator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class RoyaleConfig {

    private static Logger LOG;

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
    public static int gameOutsideBreakingMaxDistance;
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
        //todo: RoyaleMessages.suffix
        String suffixRed = "§4";
        String suffixNone = "";
        String suffix = suffixNone;

        cfgGetInt       (cfg, "zoneStartSize",              2048,            (val, def) -> {if (val < 1             || val > 4096)            return def; else return val;} );
        cfgGetInt       (cfg, "zonePreStartSize",           zoneStartSize+5, (val, def) -> {if (val < zoneStartSize || val > 4200)            return def; else return val;} );
        cfgGetInt       (cfg, "zoneStartDelay",             60,              (val, def) -> {if (val < 0             || val > 300)             return def; else return val;} );
        cfgGetInt       (cfg, "zoneStartTimer",             300,             (val, def) -> {if (val < 60            || val > 1000)            return def; else return val;} );
        cfgGetInt       (cfg, "zoneEndSize",                100,             (val, def) -> {if (val < 1             || val > zoneStartSize)   return def; else return val;} );
        cfgGetDouble    (cfg, "zoneEndSpeed",               0.5,             (val, def) -> {if (val <= 0            || val > 5)               return def; else return val;} );
        cfgGetDouble    (cfg, "zoneNewSizeMultiplier",      0.5,             (val, def) -> {if (val <= 0            || val >= 1)              return def; else return val;} );
        cfgGetInt       (cfg, "zoneProcessorPeriod",        10,              (val, def) -> {if (val < 1             || val > 20)              return def; else return val;} );
        cfgGetDouble    (cfg, "zoneWaitTimerMultiplier",    0.75,            (val, def) -> {if (val <= 0            || val > 2)               return def; else return val;} );
        cfgGetDouble    (cfg, "zoneShrinkTimerMultiplier",  0.666,           (val, def) -> {if (val <= 0            || val > 2)               return def; else return val;} );
        cfgGetBoolean   (cfg, "zoneCenterOffsetEnabled",    true);
        cfgGetDouble    (cfg, "zoneStartDamage",            0.01,            (val, def) -> {if (val < 0)                                      return def; else return val;} );
        cfgGetDouble    (cfg, "zoneDamageMultiplier",       2,               (val, def) -> {if (val < 1)                                      return def; else return val;} );
        cfgGetInt       (cfg, "zoneLavaFlowSize",           16,              (val, def) -> {if (val < 1             || val > 32)              return def; else return val;} );
        cfgGetInt       (cfg, "lavaFlowPeriod",             20,              (val, def) -> {if (val < 10)                                     return def; else return val;} );
        cfgGetBoolean   (cfg, "redzoneEnabled",             true);

        redzoneRadius = cfg.getInt("RedzoneRadius", 25);
        if (redzoneRadius < 1 || redzoneRadius > zoneStartSize / 2) {
            redzoneRadius = 25;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzoneRadius, redzoneRadius));
        suffix = suffixNone;

        redzonePeriod = cfg.getInt("RedzonePeriod", 10);
        if (redzonePeriod < 1 || redzonePeriod > 100) {
            redzonePeriod = 10;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzonePeriod, redzonePeriod));
        suffix = suffixNone;

        redzoneDuration = cfg.getInt("RedzoneLength", 10);
        if (redzoneDuration < 1 || redzoneDuration > 60) {
            redzoneDuration = 10;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzoneDuration, redzoneDuration));
        suffix = suffixNone;

        redzoneDensity = cfg.getInt("RedzoneDensity", 5);
        if (redzoneDensity < 1 || redzoneDensity > redzoneRadius) {
            redzoneDensity = 5;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzoneDensity, redzoneDensity));
        suffix = suffixNone;

        redzoneStartDelay = cfg.getInt("FirstRedzoneTime", 350);
        if (redzoneStartDelay < 0) {
            redzoneStartDelay = 350;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzoneStartDelay, redzoneStartDelay));
        suffix = suffixNone;

        redzoneDelayMin = cfg.getInt("RedzoneMinPause", 60);
        if (redzoneDelayMin < 0) {
            redzoneDelayMin = 60;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzoneDelayMin, redzoneDelayMin));
        suffix = suffixNone;

        redzoneDelayMax = cfg.getInt("RedzoneMaxPause", 120);
        if (redzoneDelayMax < redzoneDelayMin) {
            redzoneDelayMax = redzoneDelayMin + 60;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzoneDelayMax, redzoneDelayMax));
        suffix = suffixNone;

        redzoneDisableSize = cfg.getInt("RedzoneMinZoneSize", 250);
        if (redzoneDisableSize < zoneLavaFlowSize) {
            redzoneDisableSize = 250;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.redzoneDisableSize, redzoneDisableSize));
        suffix = suffixNone;

        monstersEnabled = cfg.getBoolean("EnableMonsters", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.monstersEnabled, monstersEnabled));

        monstersStartDelay = cfg.getInt("EnableMonstersTime", 310);
        if (monstersStartDelay < 0) {
            monstersStartDelay = 310;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.monstersStartDelay, monstersStartDelay));
        suffix = suffixNone;

        //monsterSpawnChances configurationSection
        ConfigurationSection cs = cfg.getConfigurationSection("monstersSpawnChances");
        Set<String> monsters = cs.getKeys(false);
        monstersSpawnChances = new HashMap<>();
        for (String s : monsters) {
            try {
                monstersSpawnChances.put(EntityType.valueOf(s), cs.getDouble(s));
            } catch (IllegalArgumentException e) {
                LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
            }
        }
        //todo log.info

        if (cfg.getConfigurationSection("MonsterSpawns").getKeys(false).size() == 0) {
            LOG.info("[Royale] MonsterSpawns list is empty?");
            HashMap<String, Double> hm = new HashMap<>();
            hm.put(EntityType.ZOMBIE.toString(), 0.95);
            cfg.createSection("MonsterSpawns", hm);
        }

        elytraStartEnabled = cfg.getBoolean("EnableElytraStart", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.elytraStartEnabled, elytraStartEnabled));

        elytraFallHeight = cfg.getInt("StartFallHeight", 1600);
        if (elytraFallHeight < 256) {
            elytraFallHeight = 1600;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.elytraFallHeight, elytraFallHeight));
        suffix = suffixNone;

        lobbyMinVotestarters = cfg.getInt("MinVotestarts", 3);
        if (lobbyMinVotestarters < 1) {
            lobbyMinVotestarters = 3;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.lobbyMinVotestarters, lobbyMinVotestarters));
        suffix = suffixNone;

        lobbyMinVotestartersPercent = cfg.getDouble("MinVotestartPercent", 0.51);
        if (lobbyMinVotestartersPercent <= 0 || lobbyMinVotestartersPercent > 1) {
            lobbyMinVotestartersPercent = 0.51;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.lobbyMinVotestartersPercent, lobbyMinVotestartersPercent));
        suffix = suffixNone;

        lobbyPostGameCommandEnabled = cfg.getBoolean("PostGameCommandEnable", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.lobbyPostGameCommandEnabled, lobbyPostGameCommandEnabled));

        lobbyPostGameCommandDelay = cfg.getInt("PostGameCommandTime", 60);
        if (lobbyPostGameCommandDelay < 0) {
            lobbyPostGameCommandDelay = 60;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.lobbyPostGameCommandDelay, lobbyPostGameCommandDelay));
        suffix = suffixNone;

        lobbyPostGameCommand = cfg.getString("PostGameCommand", "say post-game event triggered!");
        if (lobbyPostGameCommand.length() == 0) {
            lobbyPostGameCommand = "say post-game event triggered!";
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.lobbyPostGameCommand, lobbyPostGameCommand));
        suffix = suffixNone;

        squadMaxMembers = cfg.getInt("MaxSquadMembers", 4);
        if (squadMaxMembers <= 0) {
            squadMaxMembers = 4;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.squadMaxMembers, squadMaxMembers));
        suffix = suffixNone;

        //todo: valueOf(not Enum value) - handle exception
        squadNametagVisiblity = Team.OptionStatus.valueOf(cfg.getString("NametagVisiblity", "FOR_OWN_TEAM"));
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.squadNametagVisiblity, squadNametagVisiblity));

        squadFriendlyFireEnabled = cfg.getBoolean("FriendlyFire", false);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.squadFriendlyFireEnabled, squadFriendlyFireEnabled));

        squadAutoBalancingEnabled = cfg.getBoolean("EnableSquadBalancing", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.squadAutoBalancingEnabled, squadAutoBalancingEnabled));

        gameOutsideBreakingEnabled = cfg.getBoolean("OutsideZoneBreakingEnable", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.gameOutsideBreakingEnabled, gameOutsideBreakingEnabled));

        gameOutsideBreakingMaxDistance = cfg.getInt("OutsideZoneBreakingDistance", 3);
        if (gameOutsideBreakingMaxDistance < 2 || gameOutsideBreakingMaxDistance > 10) {
            gameOutsideBreakingMaxDistance = 3;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.gameOutsideBreakingMaxDistance, gameOutsideBreakingMaxDistance));
        suffix = suffixNone;

        gameOutsideBreakingPeriod = cfg.getInt("OutsideZoneBreakingPeriod", 5);
        if (gameOutsideBreakingPeriod < 1) {
            gameOutsideBreakingPeriod = 5;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.gameOutsideBreakingPeriod, gameOutsideBreakingPeriod));
        suffix = suffixNone;

        gameGiveZoneMap = cfg.getBoolean("GiveZoneMap", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.gameGiveZoneMap, gameGiveZoneMap));

        gameContainerTrackingEnabled = cfg.getBoolean("EnableChestTracking", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.gameContainerTrackingEnabled, gameContainerTrackingEnabled));

        //todo: valueOf(not Enum value)
        gameContainerReplacmentMaterial = Material.valueOf(cfg.getString("RestoreChestBlock", "MOSSY_COBBLESTONE"));
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.gameContainerReplacmentMaterial, gameContainerReplacmentMaterial));

        airdropEnabled = cfg.getBoolean("AirdropEnable", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.airdropEnabled, airdropEnabled));

        airdropAlertEnabled = cfg.getBoolean("AirdropAlert", true);
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.airdropAlertEnabled, airdropAlertEnabled));

        airdropStartDelay = cfg.getInt("FirstAirdropTime", 250);
        if (airdropStartDelay < 0) {
            airdropStartDelay = 250;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.airdropStartDelay, airdropStartDelay));
        suffix = suffixNone;

        airdropDelayMin = cfg.getInt("AirdropMinPause", 150);
        if (airdropDelayMin < 0) {
            airdropDelayMin = 150;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.airdropDelayMin, airdropDelayMin));
        suffix = suffixNone;

        airdropDelayMax = cfg.getInt("AirdropMaxPause", 240);
        if (airdropDelayMax < airdropDelayMin) {
            airdropDelayMax = airdropDelayMin + 90;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.airdropDelayMax, airdropDelayMax));
        suffix = suffixNone;

        airdropDisableSize = cfg.getInt("AirdropMinZoneSize", 300);
        if (airdropDisableSize < zoneLavaFlowSize) {
            airdropDisableSize = zoneEndSize;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.airdropDisableSize, airdropDisableSize));
        suffix = suffixNone;

        //airdropItems configurationSection
        cs = cfg.getConfigurationSection("airdropItems");
        Set<String> items = cs.getKeys(false);
        airdropItems = new HashMap<>();
        for (String s : items) {
            try {
                airdropItems.put(Material.valueOf(s), cs.getInt(s));
            } catch (IllegalArgumentException e) {
                LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchEnum, s));
            }
        }
        //todo log.info

        airdropEnchantedItemChance = cfg.getDouble("EnchantedItems", 0.1);
        if (airdropEnchantedItemChance < 0 || airdropEnchantedItemChance > 1) {
            airdropEnchantedItemChance = 0.1;
            suffix = suffixRed;
        }
        LOG.info(RoyaleMessages.prefix + suffix + String.format(RoyaleMessages.airdropEnchantedItemChance, airdropEnchantedItemChance));
        suffix = suffixNone;

        /* todo
        airdropEnchantments = getDefaultEnchantments();
        if (cfg.getConfigurationSection("Enchantments").getKeys(false).size() == 0) {
            LOG.info("[Royale] Enchantments list is empty?");
            cfg.createSection("Enchantments");
            ConfigurationSection cs = cfg.getConfigurationSection("Enchantments");
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put(Enchantment.ARROW_DAMAGE.toString(), 10);
            cs.createSection(Material.BOW.toString(), hm);
        }*/

        /* todo
        airdropPotions = getDefaultPotions();
        if (cfg.getConfigurationSection("Potions").getKeys(false).size() == 0) {
            LOG.info("[Royale] Potions list is empty?");
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put(PotionType.INSTANT_DAMAGE.toString(), 5);
            cfg.createSection("Potions", hm);
        }*/

        /* todo
        airdropStackableItems = getDefaultStackables();
        if (cfg.getConfigurationSection("StackableItems").getKeys(false).size() == 0) {
            LOG.info("[Royale] StackableItems list is empty?");
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put(Material.ARROW.toString(), 20);
            cfg.createSection("StackableItems", hm);
        }*/

        //todo: refactor if possible
        //todo: rename config fields
        //todo: comment config fields

        //todo: new features
        //     - map generator
        //    - loot spawn
        //   - background map loading / chest tracking
        //  - portal / beacon features
        // - etc etc etc...
    }

    public static void setLogger(Logger l) {
        LOG = l;
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
            LOG.info(RoyaleMessages.prefix + (useSuffix ? RoyaleMessages.suffixRed : RoyaleMessages.suffixNone)
                    + String.format((String)rmf.get(RoyaleMessages.class), (int)rcf.get(RoyaleConfig.class)));
        } catch (NoSuchFieldException e) {
            LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchConfigField, field));
        } catch (IllegalAccessException e) {
            LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.cantWriteConfigField, field));
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
            LOG.info(RoyaleMessages.prefix + (useSuffix ? RoyaleMessages.suffixRed : RoyaleMessages.suffixNone)
                    + String.format((String)rmf.get(RoyaleMessages.class), (double)rcf.get(RoyaleConfig.class)));
        } catch (NoSuchFieldException e) {
            LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchConfigField, field));
        } catch (IllegalAccessException e) {
            LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.cantWriteConfigField, field));
        }
    }

    private static void cfgGetBoolean(FileConfiguration cfg, String field, boolean dflt) {
        try {
            Field rcf = RoyaleConfig.class.getDeclaredField(field);
            Field rmf = RoyaleMessages.class.getDeclaredField(field);
            rcf.set(RoyaleConfig.class, cfg.getBoolean(field, dflt));
            LOG.info(RoyaleMessages.prefix + String.format((String)rmf.get(RoyaleMessages.class), (boolean)rcf.get(RoyaleConfig.class)));
        } catch (NoSuchFieldException e) {
            LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.noSuchConfigField, field));
        } catch (IllegalAccessException e) {
            LOG.warning(RoyaleMessages.suffixRed + RoyaleMessages.prefix + String.format(RoyaleMessages.cantWriteConfigField, field));
        }

    }

    //todo: 95% of duplicates: int, cfgGetDouble, Boolean, etc...
    //todo: can refactor validators in one class?
}
