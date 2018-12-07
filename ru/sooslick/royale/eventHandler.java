package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Map;

public class eventHandler implements Listener {

    private royale R;
    private zone Z;
    private String cause = "";
    private String by = "";
    private Runnable DEATH_MESSAGE;
    public eventHandler(royale rt) {
        R = rt;
        Z = R.GameZone;
        this.DEATH_MESSAGE = new Runnable() {
            @Override
            public void run()
            {
                Bukkit.broadcastMessage(cause + by + "!");
                R.alertEveryone("§c[Royale] " + Z.alive + " players left!");
            }
        };
    }

    //todo: messages, event type + refactor
    @EventHandler(priority = EventPriority.HIGHEST)
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
                    if (e.getCause()==EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) cause = " turned into gibs";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.CONTACT) cause = " cactused";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.CUSTOM) cause = " got some bad stuff";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.DROWNING) cause = " drank himself to dead";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) cause = " had a pookan explosion";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FALL) cause = " found some ALMI";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FALLING_BLOCK) cause = " smashed his skull";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FIRE) cause = "'s perdak burned out";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.FIRE_TICK) cause = "'s perdak burned out";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.LAVA) cause = " feels like a Terminator";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.LIGHTNING) cause = " caught the lightning";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.MAGIC) cause = " caught some bad stuff";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.MELTING) cause = " frozen";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.POISON) cause = " drank some bad stuff";
                    else if (e.getCause()==EntityDamageByEntityEvent.DamageCause.PROJECTILE) cause = " shooted";
                    else if (e.getCause()==EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK) cause = " killed";
                    else if (e.getCause()==EntityDamageByEntityEvent.DamageCause.ENTITY_EXPLOSION) cause = " boomed";
                    else if (e.getCause()==EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK) cause = " accidentally killed";
                    else if (e.getCause()==EntityDamageEvent.DamageCause.SUFFOCATION) {
                        if (!Z.wb.isInside(p1.getLocation()))
                            cause = " died outside gamezone";
                        else
                            cause = " buried alive";
                    }
                    else cause = " sucked";
                    p1.setHealth(20);
                    p1.setGameMode(GameMode.SPECTATOR);
                    squad s = R.getSquad(p1);
                    s.KillPlayer(p1.getName());
                    cause = "§c[Royale] " + p1.getName() + cause;
                    Z.alive = Z.alivePlayers();
                    Z.aliveTeams = Z.aliveTeams();
                    R.getServer().getScheduler().scheduleSyncDelayedTask(R, DEATH_MESSAGE, 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnDamageBy(EntityDamageByEntityEvent e)
    {
        if (Z.GameActive)
        {
            if (e.getEntity() instanceof Player)
            {
                Player p1 = (Player) e.getEntity();
                String dmgr = e.getDamager().getName();
                by = " by " + dmgr;
            }
        }
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setGameMode(GameMode.SPECTATOR);  //force spectator
        if ((Z.GameActive) && R.Leavers.contains(p.getName()))
        {
            squad s = R.getSquad(p);
            s.RevivePlayer(p.getName());
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            R.clearArmor(p);
            R.Leavers.remove(p.getName());
            R.alertEveryone("§a[Royale] Player "+p.getName()+" reconnected and revived!");
        }
    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Z.GameActive) {
            squad s = R.getSquad(p);
            if (s.GetAlives().contains(p.getName())) {
                s.KillPlayer(p.getName());
                R.InvToChest(p);
                p.getInventory().clear();
                R.clearArmor(p);
                R.Leavers.add(p.getName());
                R.alertEveryone("§c[Royale] Player " + p.getName() + " disconnected");
            }
        }
        else if (R.Votestarters.contains(p))
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
                if (et.getName().equals(k)) {
                    if (!Z.mob_despawned.containsKey(k)) {
                        Z.mob_despawned.put(k,0);
                        Z.mob_total.put(k,0);
                    }
                    Z.mob_total.put(k, Z.mob_total.get(k)+1);
                    if (Math.random() > Z.CFG.getConfigurationSection("MonsterSpawns").getDouble(k, 1)) {
                        Z.mob_despawned.put(k, Z.mob_despawned.get(k)+1);
                        e.setCancelled(true);
                        return;
                    }
                }
            //todo log monster info
        }
        return;
    }

    //TODO zone block placing player interact event

    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        if (e.isCancelled())
            return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        checkContainer(e.getClickedBlock());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e)
    {
        if (e.isCancelled())
            return;

        checkContainer(e.getBlockPlaced());
    }

    private void checkContainer(Block b) {
        if (b.getType().equals(Material.CHEST))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.FURNACE))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.DISPENSER))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.ENDER_CHEST))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.TRAPPED_CHEST))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.FURNACE))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.HOPPER))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.DROPPER))
            Z.trackChest(b.getLocation());
        else if (b.getType().equals(Material.BREWING_STAND))
            Z.trackChest(b.getLocation());
    }
}
