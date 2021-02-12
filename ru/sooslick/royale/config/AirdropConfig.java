package ru.sooslick.royale.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public class AirdropConfig {
    private static final Map<Material, Integer> defaultAirdropItems = ImmutableMap.<Material, Integer>builder()
            .put(Material.AIR, 1000)
            .put(Material.COBBLESTONE, 100)
            .put(Material.ARROW, 20)
            .put(Material.BOW, 10)
            .put(Material.IRON_SWORD, 2)
            .put(Material.SPLASH_POTION, 1)
            .build();
    private static final Map<Material, Map<Enchantment, Integer>> defaultEnchantments = ImmutableMap.<Material, Map<Enchantment, Integer>>builder()
            .put(Material.BOW, ImmutableMap.of(
                    Enchantment.ARROW_DAMAGE, 3,
                    Enchantment.ARROW_KNOCKBACK, 1))
            .put(Material.IRON_SWORD, ImmutableMap.of(
                    Enchantment.DAMAGE_ALL, 4,
                    Enchantment.KNOCKBACK, 1))
            .build();
    private static final Map<PotionType, Integer> defaultPotions = ImmutableMap.of(
            PotionType.INSTANT_HEAL, 10,
            PotionType.SPEED, 8,
            PotionType.INSTANT_DAMAGE, 5,
            PotionType.STRENGTH, 4);
    private static final Map<Material, Integer> defaultStackables = ImmutableMap.of(
            Material.ARROW, 16,
            Material.COBBLESTONE, 64);

    public static boolean airdropEnabled = true;
    public static boolean airdropAlertEnabled = true;
    public static int airdropStartDelay = 250;
    public static int airdropDelayMin = 120;
    public static int airdropDelayMax = 240;
    public static int airdropDisableSize = 300;
    public static Map<Material, Integer> airdropItems = defaultAirdropItems;
    public static double airdropEnchantedItemChance = 0.1D;
    public static Map<Material, Map<Enchantment, Integer>> airdropEnchantments = defaultEnchantments;
    public static Map<PotionType, Integer> airdropPotions = defaultPotions;
    public static Map<Material, Integer> airdropStackableItems = defaultStackables;

    public static void readConfig(FileConfiguration cfg) {
        airdropEnabled = cfg.getBoolean("airdropEnabled", true);
        if (airdropEnabled) {
            airdropAlertEnabled = cfg.getBoolean("airdropAlertEnabled", true);
            airdropStartDelay = cfg.getInt("airdropStartDelay", 250);
            airdropDelayMin = cfg.getInt("airdropDelayMin", 120);
            airdropDelayMax = cfg.getInt("airdropDelayMin", 240);
            airdropDisableSize = cfg.getInt("airdropDisableSize", 300);
            airdropEnchantedItemChance = cfg.getDouble("airdropEnchantedItemChance", 0.1D);

            // airdropItems
            ConfigurationSection cs = cfg.getConfigurationSection("airdropItems");
            Map<Material, Integer> newItems = new HashMap<>();
            try {
                for (String key : cs.getKeys(false))
                    newItems.put(Material.valueOf(key), cs.getInt(key));
            } catch (Exception e) {
                newItems = defaultAirdropItems;
            }
            airdropItems = newItems;
            //todo log items

            // airdropPotions
            cs = cfg.getConfigurationSection("airdropPotions");
            Map<PotionType, Integer> newPotions = new HashMap<>();
            try {
                for (String key : cs.getKeys(false))
                    newPotions.put(PotionType.valueOf(key), cs.getInt(key));
            } catch (Exception e) {
                newPotions = defaultPotions;
            }
            airdropPotions = newPotions;
            //todo log potions

            // airdropStackables
            cs = cfg.getConfigurationSection("airdropStackableItems");
            newItems = new HashMap<>();
            try {
                for (String key : cs.getKeys(false))
                    newItems.put(Material.valueOf(key), cs.getInt(key));
            } catch (Exception e) {
                newItems = defaultStackables;
            }
            airdropStackableItems = newItems;
            //todo log items

            // airdrop enchantments
            cs = cfg.getConfigurationSection("airdropEnchantments");
            Map<Material, Map<Enchantment, Integer>> newEnchantments = new HashMap<>();
            try {
                for (String key : cs.getKeys(false)) {
                    Map<Enchantment, Integer> encMap = new HashMap<>();
                    ConfigurationSection subCs = cs.getConfigurationSection(key);
                    for (String subKey : subCs.getKeys(false))
                        encMap.put(Enchantment.getByName(subKey), subCs.getInt(subKey));
                    newEnchantments.put(Material.valueOf(key), encMap);
                }
            } catch (Exception e) {
                newEnchantments = defaultEnchantments;
            }
            airdropEnchantments = newEnchantments;
            //todo log items
        }
    }
}
