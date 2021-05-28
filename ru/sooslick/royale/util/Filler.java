package ru.sooslick.royale.util;

import org.bukkit.Material;
import org.bukkit.World;
import ru.sooslick.royale.RoyaleLogger;

/**
 * Utility builder for filling area with certain type of blocks
 */
public class Filler {
    private static final String FILL_SUCCESS = "Filled area from %s, %s, %s to %s, %s, %s";
    private static final String WORLD_NOT_SPECIFIED = "fill operation cancelled, world not specified";

    private World world;
    private int startX;
    private int startY;
    private int startZ;
    private int endX;
    private int endY;
    private int endZ;
    private Material material;

    /**
     * Set world where filler will operate
     * @param world required world
     * @return filler instance
     */
    public Filler setWorld(World world) {
        this.world = world;
        return this;
    }

    /**
     * Set starting X coordinate
     * @param value starting X coordinate
     * @return filler instance
     */
    public Filler setStartX(int value) {
        startX = value;
        return this;
    }

    /**
     * Set starting Y coordinate
     * @param value starting Y coordinate
     * @return filler instance
     */
    public Filler setStartY(int value) {
        startY = value;
        return this;
    }

    /**
     * Set starting Z coordinate
     * @param value starting Z coordinate
     * @return filler instance
     */
    public Filler setStartZ(int value) {
        startZ = value;
        return this;
    }

    /**
     * Set ending X coordinate
     * @param value ending X coordinate
     * @return filler instance
     */
    public Filler setEndX(int value) {
        endX = value;
        return this;
    }

    /**
     * Set ending Y coordinate
     * @param value ending Y coordinate
     * @return filler instance
     */
    public Filler setEndY(int value) {
        endY = value;
        return this;
    }

    /**
     * Set ending Z coordinate
     * @param value ending Z coordinate
     * @return filler instance
     */
    public Filler setEndZ(int value) {
        endZ = value;
        return this;
    }

    /**
     * Set block type
     * @param value block type
     * @return filler instance
     */
    public Filler setMaterial(Material value) {
        material = value;
        return this;
    }

    /**
     * Return block count involved in fill operation
     * @return amount of blocks to fill
     */
    public int size() {
        return (Math.abs(endX - startX) + 1) * (Math.abs(endY - startY) + 1) * (Math.abs(endZ - startZ) + 1);
    }

    /**
     * Fill the area by specified coordinates (both boundaries included)
     * @return true if filled successfully, false otherwise
     */
    public boolean fill() {
        // validate world
        if (world == null) {
            RoyaleLogger.warn(WORLD_NOT_SPECIFIED);
            return false;
        }
        //validate material
        if (material == null) {
            material = Material.AIR;
        }
        //proceed
        for (int x = startX; x <= endX; x++)
            for (int y = startY; y <= endY; y++)
                for (int z = startZ; z <= endZ; z++)
                    world.getBlockAt(x, y, z).setType(material);
        RoyaleLogger.debug(String.format(FILL_SUCCESS, startX, startY, startZ, endX, endY, endZ));
        return true;
    }
}