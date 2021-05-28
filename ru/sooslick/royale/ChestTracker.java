package ru.sooslick.royale;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.InventoryHolder;
import ru.sooslick.royale.util.WorldUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Class for tracking and rolling back players' stuff
 */
public class ChestTracker {
    private static final String REPORT_TEMPLATE = "ChestTracker cleanup report:\nContainers: %s\nBlocks: %s\nEntities: %s";
    private static final String TRACKED_FORCE = "Force tracking on block %s";
    private static final String TRACKED_CONTAINER = "Tracked container %s at %s";
    private static final String TRACKED_BLOCK = "Tracked important block %s at %s";
    private static final String TRACKED_ENTITY = "Tracked entity %s at %s";

    private static final ArrayList<EntityType> TRACKED_ENTITY_TYPES;
    private static final ArrayList<Material> TRACKED_BLOCKS;

    private static ChestTracker instance;

    private final LinkedHashSet<Block> trackedContainers;
    private final LinkedHashSet<Block> trackedBlocks;
    private final LinkedHashSet<Entity> trackedEntities;

    static {
        TRACKED_ENTITY_TYPES = new ArrayList<>();
        TRACKED_ENTITY_TYPES.add(EntityType.DROPPED_ITEM);
        TRACKED_ENTITY_TYPES.add(EntityType.MINECART_CHEST);
        TRACKED_ENTITY_TYPES.add(EntityType.MINECART_HOPPER);
        TRACKED_ENTITY_TYPES.add(EntityType.ARMOR_STAND);
        TRACKED_ENTITY_TYPES.add(EntityType.ITEM_FRAME);
        TRACKED_ENTITY_TYPES.add(EntityType.HORSE);
        TRACKED_ENTITY_TYPES.add(EntityType.MULE);
        TRACKED_ENTITY_TYPES.add(EntityType.DONKEY);
        TRACKED_ENTITY_TYPES.add(EntityType.BOAT);

        TRACKED_BLOCKS = new ArrayList<>();
        TRACKED_BLOCKS.add(Material.IRON_ORE);
        TRACKED_BLOCKS.add(Material.IRON_BLOCK);
        TRACKED_BLOCKS.add(Material.GOLD_ORE);
        TRACKED_BLOCKS.add(Material.GOLD_BLOCK);
        TRACKED_BLOCKS.add(Material.COAL_BLOCK);
        TRACKED_BLOCKS.add(Material.DIAMOND_BLOCK);
        TRACKED_BLOCKS.add(Material.ANCIENT_DEBRIS);
        TRACKED_BLOCKS.add(Material.NETHERITE_BLOCK);
    }

    public static ChestTracker getNewInstance() {
        if (instance != null)
            instance.cleanup();
        instance = new ChestTracker();
        return instance;
    }

    public static ChestTracker getCurrentInstance() {
        if (instance == null)
            instance = new ChestTracker();
        return instance;
    }

    private ChestTracker() {
        trackedContainers = new LinkedHashSet<>();
        trackedBlocks = new LinkedHashSet<>();
        trackedEntities = new LinkedHashSet<>();
    }

    /**
     * Detect specified block and mark it for rollback if it meets rollback criteria
     * @param b tracked block
     */
    public void detectBlock(Block b) {
        detectBlock(b, false);
    }

    /**
     * Detect specified block and mark it for rollback if it meets rollback criteria
     * @param b tracked block
     * @param force forced track flag
     */
    public void detectBlock(Block b, boolean force) {
        if (force) {
            if (trackedBlocks.add(b))
                RoyaleLogger.debug(String.format(TRACKED_FORCE, WorldUtil.formatLocation(b.getLocation())));
            return;
        }
        if (b.getState() instanceof InventoryHolder) {
            if (trackedContainers.add(b))
                RoyaleLogger.debug(String.format(TRACKED_CONTAINER, b.getType(), WorldUtil.formatLocation(b.getLocation())));
        } else if (TRACKED_BLOCKS.contains(b.getType())) {
            if (trackedBlocks.add(b))
                RoyaleLogger.debug(String.format(TRACKED_BLOCK, b.getType(), WorldUtil.formatLocation(b.getLocation())));
        }
    }

    /**
     * Detect the specified entity and mark it for rollback if it meets rollback criteria
     * @param e tracked entity
     */
    public void detectEntity(Entity e) {
        if (TRACKED_ENTITY_TYPES.contains(e.getType()))
            if (trackedEntities.add(e)) {
                RoyaleLogger.debug(String.format(TRACKED_ENTITY, e.getType(), WorldUtil.formatLocation(e.getLocation())));
            }
    }

    /**
     * Remove all tracked stuff
     */
    private void cleanup() {
        int chests = trackedContainers.size();
        int blocks = trackedBlocks.size();
        int ent = trackedEntities.size();
        //clear and delete containers
        trackedContainers.forEach(b -> {
            if (b.getState() instanceof InventoryHolder) {
                ((InventoryHolder) b.getState()).getInventory().clear();
                b.setType(Material.AIR);
            }
        });
        trackedContainers.clear();
        //clear simple blocks and fluids
        trackedBlocks.forEach(b -> b.setType(Material.AIR));
        trackedBlocks.clear();
        //clear entities (via iterator because concurrent modification exception)
        Iterator<Entity> i = trackedEntities.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (i.hasNext()) {
            Entity e = i.next();
            if (e != null)
                e.remove();
        }
        RoyaleLogger.info(String.format(REPORT_TEMPLATE, chests, blocks, ent));
    }
}