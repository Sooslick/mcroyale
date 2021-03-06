package ru.sooslick.royale.legacy;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class eventHandler implements Listener {

    private royale R;
    private zone Z;
    private int alive;
    private String cause = "";
    private String by = "";
    private Event dmgEvent;
    private Runnable DEATH_MESSAGE;
    public eventHandler(royale rt) {
        R = rt;
        Z = R.GameZone;
        this.DEATH_MESSAGE = new Runnable() {
            @Override
            public void run()
            {
                if (dmgEvent.getClass().equals(EntityDamageByEntityEvent.class))
                    Bukkit.broadcastMessage(cause + by + "!");
                else
                    Bukkit.broadcastMessage(cause + "!");
                R.alertEveryone("§c[Royale] " + alive + " players left!");
            }
        };
    }

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
                    R.PlayerInvToChest(p1);
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
                    s.killPlayer(p1.getName());
                    cause = "§c[Royale] " + p1.getName() + cause;
                    Z.alive = Z.alivePlayers();
                    Z.aliveTeams = Z.aliveTeams();                          //wtffffff souqa
                    dmgEvent = e;
                    alive = Z.alive;
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
            //using a respawn method restores health - respawning manually
            squad s = R.getSquad(p);
            s.revivePlayer(p.getName());
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
            if (s != null) {
                if (s.getAlives().contains(p.getName())) {
                    s.killPlayer(p.getName());
                    R.PlayerInvToChest(p);
                    p.getInventory().clear();
                    R.clearArmor(p);
                    R.Leavers.add(p.getName());
                    R.alertEveryone("§c[Royale] Player " + p.getName() + " disconnected");
                }
            }
        }
        else if (R.Votestarters.contains(p.getName()))
            R.Votestarters.remove(p.getName());
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
            switch (e.getEntityType()) {
                case BLAZE:
                case CAVE_SPIDER:
                case CREEPER:
                case ELDER_GUARDIAN:
                case ENDERMAN:
                case EVOKER:
                case GHAST:
                case HUSK:
                case ILLUSIONER:
                case MAGMA_CUBE:
                case SKELETON:
                case SLIME:
                case SPIDER:
                case WITCH:
                case WITHER_SKELETON:
                case ZOMBIE:
                case ZOMBIE_VILLAGER:
                    e.setCancelled(true);
            }
        }
        else {
            EntityType et = e.getEntityType();
            for (String k : Z.CFG.getConfigurationSection("MonsterSpawns").getKeys(false))
                if (et.getName().equals(k.toLowerCase())) {
                    if (!Z.mob_despawned.containsKey(k)) {
                        Z.mob_despawned.put(k, 0);
                        Z.mob_total.put(k, 0);
                    }
                    Z.mob_total.put(k, Z.mob_total.get(k) + 1);
                    if (e.getLocation().getBlockY() < 56)               //spawns in caves anyway without restrictions
                        return;
                    if (Math.random() > Z.CFG.getConfigurationSection("MonsterSpawns").getDouble(k, 1)) {
                        Z.mob_despawned.put(k, Z.mob_despawned.get(k) + 1);
                        e.setCancelled(true);
                        return;
                    }
                }
        }
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        //check container
        if (e.isCancelled())
            return;
        if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
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
        switch (b.getType()) {
            case BREWING_STAND:
            case BURNING_FURNACE:
            case CHEST:
            case DISPENSER:
            case DROPPER:
            case ENDER_CHEST:
            case FURNACE:
            case HOPPER:
            case TRAPPED_CHEST:
                Z.trackChest(b.getLocation());
                break;
        }
    }
}
