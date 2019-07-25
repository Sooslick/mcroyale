package ru.sooslick.royale.legacy;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;

public class zone implements CommandExecutor
{
    public royale plugin;
    public zone(royale p) {plugin = p;}
    public FileConfiguration CFG;

    private World w;
    public WorldBorder wb;
    private double xc;
    private double zc;
    private double nxc;
    private double nzc;
    private double zs;          //zone size
    private double nzs;         //next zone size
    private double nzs_mpl;     //next zone size multiplier
    private double ezs;         //end zone speed
    private double lfzs;        //lavaflow zone size;
    private double wait_mpl;    //zone wait timer multiplier
    private double sh_mpl;      //zone shrink timer multiplier
    public boolean MonstersActive;
    private boolean ZoneShrink;
    private boolean LavaActive;
    private boolean RedzoneActive;
    public boolean FirstZone;
    public boolean GameActive;
    private boolean debug_mode;
    private int freq;       //RoyaleProcessorFrequency
    private int zwt;         //next zone wait timer
    private int zsht;        //next zone shrink timer
    private int zt;             //actual zone timer
    private int ll;             //lava level
    private int lt;             //lava timer
    private int ltReq;          //timer cap
    private int rzt;            //redzone timer
    private int rzrad;          //redzone radius
    private int rzpMin;         //redzone pause
    private int rzpMax;
    private int rzQty;          //redzone qty
    private int rzQtyLeft;
    private int rzDen;          //redzone tnts per tick
    private int rzMinSize;      //redzone min size
    private int rzx;            //redzone coord
    private int rzz;
    private int adt;            //airdrop timer
    private int adpMin;         //airdrop pause
    private int adpMax;
    private int mt;             //monsters timer
    private double dmg;         //zone damage mpl;
    private int ozb;         //outside zone breaking distance
    public int eltimer;        //elytra timer
    private boolean elalert;    //elytra alert
    public ArrayList<Player> Flyers = new ArrayList<>();
    private ArrayList<squad> Teams = new ArrayList<>();
    private HashMap<Player, Integer> Alerts = new HashMap<>();
    public int alive;
    public int aliveTeams;
    public HashMap<String, Integer> mob_despawned = new HashMap<>();
    public HashMap<String, Integer> mob_total = new HashMap<>();
    public int mob_timer;
    private ArrayList<Location> ChestCreated = new ArrayList<>();

    public void init(World world)
    {
        w = world;
        xc = 0;
        zc = 0;
        nxc = 0;
        nzc = 0;
        ZoneShrink = true;
        FirstZone = true;
        LavaActive = false;
        GameActive = false;
        RedzoneActive = false;
        MonstersActive = false;
        debug_mode = false;
        freq = CFG.getInt("RoyaleProcessorFrequency", 20);
        zwt = CFG.getInt("StartTimer", 300) * 20;
        zsht = CFG.getInt("StartTimer", 300) * 20;  //seconds * 20 = gameticks
        zt = CFG.getInt("PreStartTimer", 60)*20;
        zs = CFG.getInt("PreStartZoneSize", 2055);
        nzs = CFG.getInt("StartZoneSize", 2048);
        nzs_mpl = CFG.getDouble("NewZoneSizeMultiplier", 0.5D);
        ezs = (CFG.getInt("EndZoneSize",100) / CFG.getDouble("EndZoneSpeed",0.5D)) * 20;
        lfzs = CFG.getInt("LavaFlowZoneSize", 16);
        wait_mpl = CFG.getDouble("WaitMultiplier", 0.75);
        sh_mpl = CFG.getDouble("ShrinkMultiplier", 0.67);
        ll = 0;
        lt = 0;
        ltReq = CFG.getInt("LavaFlowSpeed", 20);
        rzt = CFG.getInt("FirstRedzoneTime", 350) * 20;
        rzrad = CFG.getInt("RedzoneRadius", 25);
        rzpMax = CFG.getInt("RedzoneMaxPause", 60) * 20;
        rzpMin = CFG.getInt("RedzoneMinPause", 30) * 20;
        rzQty = CFG.getInt("RedzoneLength", 100);
        rzQtyLeft = 0;
        rzDen = CFG.getInt("RedzoneDensity", 5);
        rzMinSize = CFG.getInt("RedzoneMinZoneSize", 250);
        mt = CFG.getInt("EnableMonstersTime", 310) * 20;
        adt = CFG.getInt("FirstAirdropTime", 250) * 20;
        adpMin = CFG.getInt("AirdropMinPause", 100) * 20;
        adpMax = CFG.getInt("AirdropMaxPause", 200) * 20;
        wb = w.getWorldBorder();
        wb.setSize(zs);
        wb.setCenter(xc,zc);
        wb.setDamageBuffer(100);
        wb.setDamageAmount(CFG.getDouble("ZoneStartDamage", 0.01));
        dmg = CFG.getDouble("ZoneDamageMultiplier", 2);
        ozb = CFG.getInt("OutsideZoneBreakingDistance", 3);
        eltimer = 300;
        elalert = true;
        Teams.clear();
        Alerts.clear();
        Flyers.clear();
        alive = 0;
        aliveTeams = 0;

        mob_despawned.clear();
        mob_total.clear();
        mob_timer = 1200;
    }

    public void startgame(boolean debug)
    {
        wb.setSize(nzs, zt/20);
        GameActive = true;
        debug_mode = debug;
    }

    public void stopgame()
    {
        GameActive = false;
        plugin.endgame();
        init(w);
    }

    public void addTeam(squad s) {Teams.add(s);}

    public void tickProcessor()
    {
        if (GameActive) {
            //Lava Processor
            if (LavaActive) {
                lt += freq;
                if (lt >= ltReq) //"LavaFlowSpeed"
                {
                    lt -= ltReq;
                    Location l = wb.getCenter();
                    int ofs = (int)(lfzs / 2) + 2;
                    l.setY(ll+1);
                    int lfx1 = Math.round(l.getBlockX() - ofs);
                    int lfx2 = Math.round(l.getBlockX() + ofs);
                    int lfz1 = Math.round(l.getBlockZ() - ofs);
                    int lfz2 = Math.round(l.getBlockZ() + ofs);
                    //Barrier:
                    for (int i = lfx1; i <= lfx2; i++) {
                        l.setX(i);
                        l.setZ(lfz1 - 1);
                        l.getBlock().setType(Material.BARRIER);
                        l.setZ(lfz2 + 1);
                        l.getBlock().setType(Material.BARRIER);
                    }
                    for (int i = lfz1; i <= lfz2; i++) {
                        l.setZ(i);
                        l.setX(lfx1 - 1);
                        l.getBlock().setType(Material.BARRIER);
                        l.setX(lfx2 + 1);
                        l.getBlock().setType(Material.BARRIER);
                    }
                    //lava under:
                    l.setY(ll);
                    for (int i = lfx1; i < lfx2; i++) {
                        for (int j = lfz1; j < lfz2; j++) {
                            l.setX(i);
                            l.setZ(j);
                            l.getBlock().setType(Material.LAVA);
                        }
                    }
                    //lava upper:
                    l.setY(ll + 1);
                    for (double i = lfx1; i <= lfx2; i++) {
                        for (double j = lfz1; j <= lfz2; j++) {
                            l.setX(i);
                            l.setZ(j);
                            l.getBlock();
                            if (l.getBlock().getType() == Material.AIR)
                                l.getBlock().setType(Material.LAVA);
                        }
                    }
                    ll++;
                    if (ll > w.getMaxHeight()) {
                        ll--;           //over the edge
                    }
                }
            }
            //Zone Processor
            else {
                zt -= freq;
                //if zone shrink
                if (ZoneShrink) {
                    //check lavaflow size to force switch
                    if (wb.getSize() <= lfzs) {
                        wb.setSize(wb.getSize());
                        LavaActive = true;
                        Bukkit.broadcastMessage("§cRestricting play area! §6Lava flow §cbegins right now!");
                    }
                    else if (zt <= 0) //switch moment
                    {
                        ZoneShrink = false;
                        zt = zwt;           //set timer
                        if (!FirstZone)
                            zsht *= sh_mpl;       //"ShrinkMultiplier"
                        xc = nxc;           //set fixed center
                        zc = nzc;
                        double ozs = nzs;   //old zone size;
                        wb.setCenter(xc, zc);   // *= "NewZoneSizeMultiplier"
                        nzs *= nzs_mpl;            //pre-set new size-center
                        //check if last zone (no center offset)
                        if (nzs < CFG.getInt("EndZoneSize", 100)) {
                            nzs = 0;
                            zsht = (int)ezs;    //3 minutes to shrink
                        }
                            //set new center if enabled
                        else if (CFG.getBoolean("EnableCenterOffset", true)) {
                            nxc += Math.random() * (ozs - nzs) - nzs / 2;
                            nzc += Math.random() * (ozs - nzs) - nzs / 2;
                        }
                        wb.setDamageAmount(wb.getDamageAmount() * dmg); //new damage
                        Bukkit.broadcastMessage("=-=-§aRoyale Zone§f-=-=");
                        Bukkit.broadcastMessage("§cRestricting play area in " + Integer.toString((int) (zt / 20)) + " seconds!");
                        Bukkit.broadcastMessage("§cNew zone center at x=" + Integer.toString((int) nxc) + "; z=" + Integer.toString((int) nzc));
                        Bukkit.broadcastMessage("=-=-=-=-=-=-=-=-=-=");
                        //map item
                        giveMap();
                        //todo: /mark x y command

                    } else {                                           //ша "EnableCenterOffset"
                        double z1 = zt;
                        double z2 = zsht;
                        double xx = nxc - ((nxc - xc) * (z1 / z2));  //set center
                        double zz = nzc - ((nzc - zc) * (z1 / z2));
                        wb.setCenter(xx, zz);
                    }
                }
                //if zone wait
                else {
                    if (zt <= 0) {
                        ZoneShrink = true;
                        FirstZone = false;
                        zt = zsht;           //set timer
                        zwt *= wait_mpl;            //"WaitMultiplier"
                        wb.setSize(nzs, zsht/20); //zone shrink by minecraft
                        Bukkit.broadcastMessage("§cRestricting play area!");
                    } else {
                        if (triggerSecond(60, zt, freq)) Bukkit.broadcastMessage("§cRestricting play area in 60 seconds!");
                        else if (triggerSecond(30, zt, freq)) Bukkit.broadcastMessage("§cRestricting play area in 30 seconds!");
                        else if (triggerSecond(10, zt, freq)) Bukkit.broadcastMessage("§cRestricting play area in 10 seconds!");
                    }
                }
            }

            //Redzone processor
            if (CFG.getBoolean("EnableRedzone", true))
            {
                if (RedzoneActive) {
                    if (wb.getSize() > rzMinSize) {
                        if (rzt > 0) {                //timer offset
                            rzt -= freq;
                            if (rzt <= 0) {
                                rzQtyLeft = rzQty;
                                Bukkit.broadcastMessage("§4Redzone started!");
                            }
                            else if (triggerSecond(30, rzt, freq)) Bukkit.broadcastMessage("§4Redzone in 30 seconds!");
                        } else {
                            if (rzQtyLeft <= 0)               //set new redzone params
                            {
                                //set new redzone center
                                rzx = wb.getCenter().getBlockX() + (int)((Math.random()-0.5)*nzs);
                                rzz = wb.getCenter().getBlockZ() + (int)((Math.random()-0.5)*nzs);
                                rzt = (int) (Math.random() * (rzpMax - rzpMin)) + rzpMin;
                                Bukkit.broadcastMessage("§cNew redzone at x=" + Integer.toString((int)rzx) + "; z=" + Integer.toString((int)rzz) + " in " + Integer.toString((int)(rzt/20)) + " seconds");
                            } else {
                                for (int i = 0; i < rzDen; i++) {
                                    rzQtyLeft--;
                                    Location l = new Location(w, 0, 255, 0);               //spawn tnt
                                    l.setX(rzx + (Math.random() - 0.5) * rzrad*2);
                                    l.setZ(rzz + (Math.random() - 0.5) * rzrad*2);               //"RedzoneRadius"
                                    TNTPrimed tnt = w.spawn(l, TNTPrimed.class);
                                    tnt.setFuseTicks(250 + (int) (Math.random() * freq));
                                }
                            }
                        }
                    }
                } else {
                    //enable redzone
                    rzt -= freq;
                    if (rzt <= 0) {
                        RedzoneActive = true;
                        rzt = 0;
                        rzQtyLeft = 0;
                    }
                }
            }

            //airdrop processor
            //MEGA TODO: cheak this code
            if (CFG.getBoolean("AirdropEnable", true))
            {
                if (CFG.getInt("AirdropMinZoneSize", 100) < wb.getSize())
                {
                    adt -= freq;
                    if (adt <= 0) {
                        adt = (int) (Math.random() * (adpMax - adpMin) + adpMin);
                        //rand loc for chest
                        Location l = plugin.RandomLocation((int) (wb.getSize()*0.8));
                        l.setX(l.getX() + wb.getCenter().getX());
                        l.setZ(l.getZ() + wb.getCenter().getZ());
                        l.setY(w.getHighestBlockYAt(l));
                        l.getBlock().setType(Material.CHEST);
                        Chest c = (Chest) l.getBlock().getState();
                        //todo optimise: read cfg only once at game start -> after declaring Airdrop class
                        //get itemstack map
                        int MaxVar = 0;
                        HashMap<Material, Integer> ItemVar = new HashMap<>();
                        ConfigurationSection cs = CFG.getConfigurationSection("AirdropItems");
                        for (String s : cs.getKeys(false)) {
                            MaxVar+= cs.getInt(s);
                            ItemVar.put(Material.getMaterial(s),cs.getInt(s));
                        }
                        //stack map
                        HashMap<Material, Integer> StackVar = new HashMap<>();
                        cs = CFG.getConfigurationSection("StackableItems");
                        for (String s : cs.getKeys(false))
                            StackVar.put(Material.getMaterial(s),cs.getInt(s));
                        //enchantments map
                        HashMap<Material, HashMap> EncItems = new HashMap<>();
                        cs = CFG.getConfigurationSection("Enchantments");
                        for (String s : cs.getKeys(false)) {
                            HashMap<Enchantment, Integer> hm = new HashMap<>();
                            ConfigurationSection css = cs.getConfigurationSection(s);
                            for (String s1 : css.getKeys(false))
                                hm.put(Enchantment.getByName(s1),cs.getInt(s1));
                            EncItems.put(Material.getMaterial(s), hm);
                            //todo maxvar 4 every enchantment?
                        }
                        //potioon map
                        int MaxPot = 0;
                        HashMap<PotionType, Integer> PotVar = new HashMap<>();
                        cs = CFG.getConfigurationSection("Potions");
                        for (String s : cs.getKeys(false)) {
                            PotVar.put(PotionType.valueOf(s), cs.getInt(s));
                            MaxPot+= cs.getInt(s);
                        }
                        //process chest
                        for (int i=0; i<27; i++) {
                            //get itemstack from map
                            int r = (int)(Math.random()*MaxVar);
                            ItemStack IS = null;
                            for (Material m : ItemVar.keySet()) {
                                r-= ItemVar.get(m);
                                if (m == null)          //it is possible? Why crashed a new ItemStack(m)? Try to check material air
                                    continue;
                                if (r <= 0) {
                                    IS = new ItemStack(m);
                                    //get stack qty
                                    if (StackVar.containsKey(m)) {
                                        IS.setAmount((int)(Math.random()*StackVar.get(m))+1);
                                    }
                                    else
                                        IS.setAmount(1);
                                    //get enchant
                                    if (Math.random() < CFG.getDouble("EnchantedItems", 0.1))
                                        if (EncItems.containsKey(IS.getType())) {
                                            int maxe = 0;
                                            HashMap<Enchantment, Integer> hme = EncItems.get(IS.getType());
                                            for (Enchantment e : hme.keySet())
                                                maxe+= hme.get(e);
                                            maxe = (int)(Math.random()*maxe);
                                            for (Enchantment e : hme.keySet()) {
                                                maxe-= hme.get(e);
                                                if (maxe<=0) {
                                                    IS.addEnchantment(e,1);
                                                    break;
                                                }
                                            }
                                        }
                                    //get type for pot
                                    if ((IS.getType().equals(Material.SPLASH_POTION)) || (IS.getType().equals(Material.POTION))) {
                                        int p = (int)(Math.random()*MaxPot);
                                        for (PotionType pt : PotVar.keySet()) {
                                            p-= PotVar.get(pt);
                                            if (p<=0) {
                                                PotionMeta pm = (PotionMeta) IS.getItemMeta();
                                                pm.setBasePotionData(new PotionData(pt));
                                                IS.setItemMeta(pm);
                                                plugin.LOG.info(pm.getBasePotionData().getType().toString() + " / " + pt.toString());
                                                break;
                                            }
                                        }
                                    }
                                    if (IS.getType() != Material.AIR)
                                        c.getInventory().addItem(IS);
                                    break;
                                }
                            }
                        }
                        //spawn fireworks
                        for (int i=2; i<10; i++) {
                            Location fwl = l.clone();
                            fwl.setX(fwl.getX() + Math.random()*i-(i/2));
                            fwl.setZ(fwl.getZ() + Math.random()*i-(i/2));
                            Firework fw = (Firework) w.spawnEntity(fwl, EntityType.FIREWORK);
                            FireworkMeta fwm = fw.getFireworkMeta();
                            FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(Color.BLUE).withTrail().with(FireworkEffect.Type.BURST).build();
                            fwm.addEffect(fwe);
                            fwm.setPower(i);
                            fw.setFireworkMeta(fwm);
                        }
                        //alert
                        if (CFG.getBoolean("AirdropAlert", true))
                            plugin.alertEveryone("§6[Royale] Bonus Chest is spawned! X = " +l.getBlockX() + "; Z = " + l.getBlockZ());
                    }
                }
            }

            //Monsters processor
            if (CFG.getBoolean("EnableMonsters", true)) {
                if (!MonstersActive) {
                    mt -= freq;
                    if (mt <= 0) {
                        MonstersActive = true;
                        Bukkit.broadcastMessage("§cMonsters enabled!");
                    }
                }
            }

            //elytra processor
            if (eltimer > 0)
            {
                for (Player p : Flyers){
                    if (p.getLocation().getY() < w.getHighestBlockYAt(p.getLocation())+20) {
                        Flyers.remove(p);
                        PlayerInventory inv = p.getInventory();
                        if (inv.contains(Material.ELYTRA))
                            inv.remove(Material.ELYTRA);
                        if (inv.getChestplate().getType() == Material.ELYTRA)
                            inv.setChestplate(new ItemStack(Material.AIR));
                        break;
                    }
                    else if (p.getLocation().getY() < 200) {
                        if (!p.isGliding()) {
                            p.setGliding(true);
                            p.sendMessage("Here we go!");
                        }
                    }
                }
                if (Flyers.size() == 0) {
                    if (elalert) {
                        elalert = false;
                        Bukkit.broadcastMessage("§cFall damage will be enabled in 15 seconds!");
                        for (squad s : Teams) {
                            for (String pn : s.getAlives()) {
                                PlayerInventory inv = Bukkit.getPlayer(pn).getInventory();
                                if (inv.contains(Material.ELYTRA))
                                    inv.remove(Material.ELYTRA);
                                if (inv.getChestplate() != null)
                                    if (inv.getChestplate().getType() == Material.ELYTRA)
                                        inv.setChestplate(new ItemStack(Material.AIR));
                            }
                        }
                    }
                    eltimer -= freq;
                }
            }

            //wingame processor
            if (!debug_mode) {
                if (aliveTeams <= 1) {
                    //find alive squad
                    squad f = null;
                    for (squad s : Teams)
                        if (s.haveAlive()) {
                            f = s;
                            break;
                        }
                    stopgame();
                    Bukkit.broadcastMessage("§a" + f.getName() + " won the game!");
                }
            }

            //Alert processor + player damage by zone
            for (squad s : Teams)
                for (String pn : s.getAlives())
                {
                    Player p = Bukkit.getPlayer(pn);
                    Location ploc = p.getLocation();

                    //check in new zone
                    if ((ploc.getBlockX() > nxc - nzs/2) &&
                            (ploc.getBlockX() < nxc + nzs/2) &&
                            (ploc.getBlockZ() > nzc - nzs/2) &&
                            (ploc.getBlockZ() < nzc + nzs/2))
                        continue;
                    else
                    {
                        if (Alerts.containsKey(p))
                        {
                            if (Alerts.get(p) < 0)
                            {
                                p.sendMessage("§cYou are not in safe zone! Type §6/zone §cfor details");
                                Alerts.replace(p, (p.getLocation().getBlockY()+10)*25);
                            }
                            else
                                Alerts.replace(p, Alerts.get(p) - freq);
                        }
                        else
                        {
                            p.sendMessage("§cYou are not in safe zone! Type §6/zone §cfor details");
                            Alerts.put(p, (p.getLocation().getBlockY()+10)*25);
                        }
                        //damage if outside
                        if (!wb.isInside(ploc)) {
                            double dmg = distanceOutside(ploc) * wb.getDamageAmount();
                            double h = p.getHealth();
                            if (dmg > h)
                                Bukkit.getServer().getPluginManager().callEvent(new EntityDamageEvent(p, EntityDamageEvent.DamageCause.SUFFOCATION, dmg));
                            else
                                p.setHealth(h - dmg);
                            if (CFG.getBoolean("OutsideZoneBreakingEnable", true)) {
                                Block b = p.getTargetBlock(null, ozb);
                                if (b != null)
                                    b.setType(Material.AIR);
                            }
                        }
                    }
                }

            //debug monster log
            mob_timer-= freq;
            if (mob_timer <= 0) {
                mob_timer = 1200;
                plugin.LOG.info("[Royale Monster Log] ");
                for (String et : mob_total.keySet()) {
                    plugin.LOG.info("[Royale Monster Log] " + et + " " + mob_despawned.get(et).toString() + "/" + mob_total.get(et) + " despawned");
                }
            }
        }
    }

    public void giveMap() {
        if (CFG.getBoolean("GiveZoneMap")) {
            if (nzs >= 100) {
                int sc;
                for (squad s : Teams) {
                    //give zone map
                    ItemStack zonemap;
                    if (s.getMap() == null)
                        zonemap = new ItemStack(Material.MAP);      //is this required now? Todo check
                    else
                        zonemap = s.getMap();
                    MapView zmr = Bukkit.createMap(w);
                    zmr.getRenderers().clear();
                    zmr.setUnlimitedTracking(true);
                    zmr.setCenterX((int) nxc);
                    zmr.setCenterZ((int) nzc);
                    if (nzs < 64) {zmr.setScale(MapView.Scale.CLOSEST); sc = 2;}
                    else if (nzs < 128) {zmr.setScale(MapView.Scale.CLOSE); sc = 4;}
                    else if (nzs < 256) {zmr.setScale(MapView.Scale.NORMAL); sc = 8;}
                    else if (nzs < 512) {zmr.setScale(MapView.Scale.FAR); sc = 16;}
                    else {zmr.setScale(MapView.Scale.FARTHEST); sc = 32;}
                    Renderer r = new Renderer();
                    r.initialize(zmr);
                    r.init((int)nzs, sc, wb, s);
                    zmr.addRenderer(r);
                    zonemap.setDurability(zmr.getId());
                    s.setMap(zonemap);
                    for (String str : s.getPlayers()) {
                        Player p = Bukkit.getPlayer(str);
                        p.setCompassTarget(new Location(w, nxc, 0, nzc));
                        for (ItemStack is : p.getInventory().getContents()) {
                            if (is != null)
                                if (is.getType().equals(Material.MAP))
                                    p.getInventory().remove(is);
                        }
                        p.getInventory().addItem(zonemap);      //todo test / debug replacing
                    }
                }
            }
        }
    }

    public void trackChest(Location l) {
        if (!ChestCreated.contains(l))
            ChestCreated.add(l);
    }

    public void restoreChests() {
        for (Location l : ChestCreated) {
            if (l.getBlock().getState() instanceof InventoryHolder) {
                ((InventoryHolder) l.getBlock().getState()).getInventory().clear();
                l.getBlock().setType(Material.getMaterial(CFG.getString("RestoreChestBlock", "MOSSY_COBBLESTONE")));
            }
//            if (l.getBlock().getType().equals(Material.CHEST)) {
//                Chest c = (Chest)l.getBlock();
//                c.getBlockInventory().clear();
//                l.getBlock().setType(Material.getMaterial(CFG.getString("RestoreChestBlock", "MOSSY_COBBLESTONE")));
//            }
//            else
//                l.getBlock().setType(Material.AIR);
        }
        Bukkit.getConsoleSender().sendMessage("[Royale] restored " + ChestCreated.size() + " containters.");
        ChestCreated.clear();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!GameActive)
        {
            sender.sendMessage("§cGame is not started! You can try /votestart to begin new game");
            return true;
        }
        if (! (sender instanceof Player))
        {
            sender.sendMessage("§cOnly player can check /zone");
            return true;
        }
        Player p = (Player) sender;
        Location ploc = p.getLocation();

        //check in new zone
        if ((ploc.getBlockX() > nxc - nzs/2) &&
                (ploc.getBlockX() < nxc + nzs/2) &&
                (ploc.getBlockZ() > nzc - nzs/2) &&
                (ploc.getBlockZ() < nzc + nzs/2))
            sender.sendMessage("§aYou are in safe zone!");
        else
            sender.sendMessage("§cDistance to zone: " + Math.ceil(distanceToZone(((Player) sender).getLocation())));

        sender.sendMessage("§6Current zone size: " + Integer.toString((int)wb.getSize()));
        sender.sendMessage("§6Next zone size: " + nzs);
        sender.sendMessage("§6 ");
        sender.sendMessage("§6Zone center: x=" + Integer.toString((int)nxc) + "; z=" + Integer.toString((int)nzc));
        sender.sendMessage("§6Your location: x=" + Integer.toString(ploc.getBlockX()) + "; z=" + Integer.toString(ploc.getBlockZ()));
        sender.sendMessage("§6 ");
        sender.sendMessage("§7Your compass is always leads on the zone center. Special map shows to you the next zone borders.");
        return true;
    }

    public boolean triggerSecond(double second, double tick, double freq){
        second*= 20;
        return ((tick < second)&&(tick + freq >= second));
    }

    public int aliveTeams()
    {
        int a = 0;
        for (squad s : Teams)
            if (s.haveAlive())
                a++;
        return a;
    }

    public int alivePlayers()
    {
        int a = 0;
        for (squad s : Teams)
            a+= s.getAliveCount();
        return a;
    }

    public double distanceToZone(Location l) {
        double left = nxc - nzs/2;
        double right = nxc + nzs/2;
        double top = nzc + nzs/2;
        double bottom = nzc - nzs/2;
        double hd, vd;
        double x = l.getX();
        double z = l.getZ();
        if (x < left)
            hd = left - x;
        else if ((x > left) && (x < right))
            hd = 0;
        else
            hd = x - right;
        if (z < bottom)
            vd = bottom - z;
        else if ((z > bottom) && (z < top))
            vd = 0;
        else
            vd = z - top;
        return Math.sqrt(hd*hd + vd*vd);
    }
    //todo refactor distances
    public double distanceOutside(Location l) {
        //Bukkit.broadcastMessage(l.toString());
        //Bukkit.broadcastMessage(wb.getCenter().toString());
        //Bukkit.broadcastMessage(wb.getSize() + " ");
        double left = wb.getCenter().getX() - wb.getSize()/2;
        double right = wb.getCenter().getX() + wb.getSize()/2;
        double top = wb.getCenter().getZ() + wb.getSize()/2;
        double bottom = wb.getCenter().getZ() - wb.getSize()/2;
        double hd, vd;
        double x = l.getX();
        double z = l.getZ();
        if (x < left)
            hd = left - x;
        else if ((x > left) && (x < right))
            hd = 0;
        else
            hd = x - right;
        if (z < bottom)
            vd = bottom - z;
        else if ((z > bottom) && (z < top))
            vd = 0;
        else
            vd = z - top;
        return Math.sqrt(hd*hd + vd*vd);
    }
}
