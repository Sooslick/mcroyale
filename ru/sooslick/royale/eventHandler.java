package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Map;

public class eventHandler implements Listener {

    private royale R;
    private zone Z;
    public eventHandler(royale rt) {
        R = rt;
        Z = R.GameZone;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void OnDamage(EntityDamageEvent e)
    {
        if (Z.GameActive)
        {
            if (e.getEntity() instanceof Player)
            {
                Player p1 = (Player) e.getEntity();
                if (Z.eltimer > 0 && (e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL)) {
                    e.setCancelled(true);
                    return;
                }
                if (p1.getHealth() - e.getDamage() <= 0)
                {
                    R.InvToChest(p1);
                    String cause = "";
                    if (e.getCause()==EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) cause = " turned into gibs! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.CONTACT) cause = " cactused! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.CUSTOM) cause = " got some bad stuff! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.DROWNING) cause = " drank himself to dead! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) cause = " had a pookan explosion! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FALL) cause = " found some ALMI! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FALLING_BLOCK) cause = " smashed his skull! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FIRE) cause = "'s perdak burned out! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FIRE_TICK) cause = "'s perdak burned out! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.LAVA) cause = " feels like a Terminator! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.LIGHTNING) cause = " caught the lightning! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.MAGIC) cause = " caught some bad stuff! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.MELTING) cause = " frozen! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.POISON) cause = " drank some bad stuff! ";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.SUFFOCATION) {
                        if (!Z.wb.isInside(p1.getLocation()))
                            cause = " died outside gamezone! ";
                        else
                            cause = " buried alive! ";
                    }
                    else cause = " sucked! ";
                    p1.setHealth(20);
                    p1.setGameMode(GameMode.SPECTATOR);
                    squad s = R.getSquad(p1);
                    s.KillPlayer(p1.getName());
                    R.alertEveryone("§c[Royale] " + p1.getName() + cause);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setGameMode(GameMode.SPECTATOR);  //force spectator
        if ((Z.GameActive) && R.Leavers.contains(p))
        {
            squad s = R.getSquad(p);
            s.RevivePlayer(p.getName());
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            R.clearArmor(p);
            R.Leavers.remove(p);
            R.alertPlayer("§a[Royale] Revived!", p);        //TODO msg cfg
        }
    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Z.GameActive) {
            squad s = R.getSquad(p);
            s.KillPlayer(p.getName());
            R.InvToChest(p);        //TODO save inv
            p.getInventory().clear();
            R.clearArmor(p);
            R.Leavers.add(p);
        }
        if (R.Votestarters.contains(p))
            R.Votestarters.remove(p);
    }

    @EventHandler
    public void OnDrop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() == Material.ELYTRA)
            e.setCancelled(true);
    }

    @EventHandler
    public void MonsterSpawn(CreatureSpawnEvent e)
    {
        if (e.isCancelled()) return;
        if (!Z.GameActive) {e.setCancelled(true); return;}
        if (!Z.MonstersActive) {
            if (e.getEntityType().equals(EntityType.BLAZE)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.CAVE_SPIDER)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.CREEPER)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.ELDER_GUARDIAN)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.ENDERMAN)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.EVOKER)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.GHAST)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.HUSK)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.ILLUSIONER)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.MAGMA_CUBE)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.SKELETON)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.SLIME)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.SPIDER)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.WITCH)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.WITHER)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.WITHER_SKELETON)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.ZOMBIE)) {
                e.setCancelled(true);
                return;
            }
            if (e.getEntityType().equals(EntityType.ZOMBIE_VILLAGER)) {
                e.setCancelled(true);
                return;
            }
        }
        else {
            EntityType et = e.getEntityType();
            for (String k : Z.CFG.getConfigurationSection("MonsterSpawns").getKeys(false))
                if (et.getName().equals(k))
                    if (Math.random() > Z.CFG.getConfigurationSection("MonsterSpawns").getDouble(k, 1)) {
                        e.setCancelled(true);
                        return;
                    }
        }
        return;
    }
}
