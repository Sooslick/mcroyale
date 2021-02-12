package ru.sooslick.royale;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class RestrictedZoneTemplate {
    int size;
    int waitDuration;
    int shrinkDuration;
    double damage;

    public RestrictedZoneTemplate(int size, int wait, int shrink, double dmg) {
        this.size = size;
        this.waitDuration = wait;
        this.shrinkDuration = shrink;
        this.damage = dmg;
    }
}
