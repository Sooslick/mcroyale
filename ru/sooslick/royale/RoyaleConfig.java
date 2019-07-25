package ru.sooslick.royale;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class RoyaleConfig {

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
        hm.put(EntityType.ZOMBIE, 0.95);
        return hm;
    }

    public static HashMap<Material, Integer> getDefaultAirdropItems() {
        HashMap<Material, Integer> hm = new HashMap<>();
        hm.put(Material.AIR, 200);
        hm.put(Material.COBBLESTONE, 100);
        hm.put(Material.ARROW, 20);
        hm.put(Material.BOW, 10);
        hm.put(Material.IRON_SWORD, 2);
        hm.put(Material.POTION, 1);
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
}
