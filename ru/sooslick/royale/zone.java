package ru.sooslick.royale;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;

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
    private double lfzs;        //lavaflow zone size;
    private double wait_mpl;    //zone wait timer multiplier
    private double sh_mpl;      //zone shrink timer multiplier
    public boolean MonstersActive;
    private boolean ZoneShrink;
    private boolean LavaActive;
    private boolean RedzoneActive;
    public boolean FirstZone;
    public boolean GameActive;
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
    private int mt;             //monsters timer
    private double dmg;         //zone damage mpl;
    public int eltimer;        //elytra timer
    private boolean elalert;    //elytra alert
    public ArrayList<Player> Flyers = new ArrayList<>();
    private ArrayList<squad> Teams = new ArrayList<>();
    private HashMap<Player, Integer> Alerts = new HashMap<>();

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
        freq = CFG.getInt("RoyaleProcessorFrequency", 20);
        zwt = CFG.getInt("StartTimer", 300) * 20;
        zsht = CFG.getInt("StartTimer", 300) * 20;  //seconds * 20 = gameticks
        zt = 60*20;     //pre-start value todo config
        zs = CFG.getInt("PreStartZoneSize", 2055);
        nzs = CFG.getInt("StartZoneSize", 2048);
        nzs_mpl = CFG.getDouble("NewZoneSizeMultiplier", 0.5D);
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
        wb = w.getWorldBorder();
        wb.setSize(zs);
        wb.setCenter(xc,zc);
        wb.setDamageBuffer(100);
        wb.setDamageAmount(CFG.getDouble("ZoneStartDamage", 0.01));
        dmg = CFG.getDouble("ZoneDamageMultiplier", 2);
        eltimer = 300;
        elalert = true;
        Teams.clear();
        Alerts.clear();
        Flyers.clear();
    }

    public void startgame()
    {
        GameActive = true;
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
                    if (ll > 254) {
                        ll--;           //over the edge     //TODO check! max world h
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
                        if (nzs < CFG.getInt("EndZoneSize", 100))
                            nzs = 0;
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
                        Bukkit.broadcastMessage("§4[name:\"Zone Center\", x:"+Integer.toString((int) nxc)+",y:0,z:"+Integer.toString((int) nzc)+"]");
                        //map item
                        if (CFG.getBoolean("GiveZoneMap")) {
                            //todo: common squad map + /zone marker x y feature
                            if (nzs >= 100) {
                                int sc;
                                for (Player pl : Bukkit.getOnlinePlayers())
                                {
                                    ItemStack zonemap = new ItemStack(Material.MAP);
                                    MapView zmr = Bukkit.createMap(w);
                                    //zmr.getRenderers().clear();
                                    zmr.setUnlimitedTracking(true);
                                    zmr.setCenterX((int) nxc);
                                    zmr.setCenterZ((int) nzc);
                                    if (nzs < 128) {zmr.setScale(MapView.Scale.CLOSEST); sc = 2;}
                                    else if (nzs < 256) {zmr.setScale(MapView.Scale.CLOSE); sc = 4;}
                                    else if (nzs < 512) {zmr.setScale(MapView.Scale.NORMAL); sc = 8;}
                                    else if (nzs < 1024) {zmr.setScale(MapView.Scale.FAR); sc = 16;}
                                    else {zmr.setScale(MapView.Scale.FARTHEST); sc = 32;}
                                    Renderer r = new Renderer();
                                    r.initialize(zmr);
                                    r.init((int)nzs, sc);
                                    zmr.addRenderer(r);
                                    zonemap.setDurability(zmr.getId());
                                    pl.getInventory().addItem(zonemap);
                                }
                            }
                        }

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
                        //TODO check first-zone & monsters enabled
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
                            if (rzt <= 0)
                                rzQtyLeft = rzQty;
                        } else {
                            if (rzQtyLeft <= 0)               //set new redzone params
                            {
                                rzx = 0;
                                rzz = 0;
                                int modifier = 0;
                                int alive = 0;
                                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                                    if (plugin.isAliveInSquad(p.getName())) {
                                        rzx += p.getLocation().getBlockX();
                                        rzz += p.getLocation().getBlockZ();
                                        alive++;
                                        if (Math.random() > 0.5) {
                                            rzx += p.getLocation().getBlockX();
                                            rzz += p.getLocation().getBlockZ();
                                            modifier++;
                                        }
                                    }
                                }
                                rzx = (int) Math.round(rzx / (alive + modifier) + (Math.random() - 0.5) * 200);
                                rzz = (int) Math.round(rzz / (alive + modifier) + (Math.random() - 0.5) * 200);
                                rzt = (int) (Math.random() * (rzpMax - rzpMin)) + rzpMin;
                                Bukkit.broadcastMessage("§cNew redzone at x=" + Integer.toString((int)rzx) + "; z=" + Integer.toString((int)rzz) + " in " + Integer.toString((int)(rzt/20)) + " seconds");
                            } else {
                                for (int i = 0; i < rzDen; i++) {
                                    rzQtyLeft--;
                                    Location l = new Location(w, 0, 255, 0);               //spawn tnt
                                    l.setX(rzx + (Math.random() - 0.5) * rzrad*2);               //todo radius & timers
                                    l.setZ(rzz + (Math.random() - 0.5) * rzrad*2);               //"RedzoneRadius"
                                    TNTPrimed tnt = w.spawn(l, TNTPrimed.class);
                                    tnt.setFuseTicks(250 + (int) (Math.random() * 20));
                                }
                            }
                        }
                    }
                } else {
                    rzt -= freq;
                    if (rzt <= 0) {
                        RedzoneActive = true;
                        rzt = 0;
                        rzQtyLeft = 0;
                        Bukkit.broadcastMessage("§4Redzone started!");
                    }
                    else if (triggerSecond(60, rzt, freq)) Bukkit.broadcastMessage("§4Redzone in 60 seconds!");
                    else if (triggerSecond(30, rzt, freq)) Bukkit.broadcastMessage("§4Redzone in 30 seconds!");
                    else if (triggerSecond(10, rzt, freq)) Bukkit.broadcastMessage("§4Redzone in 10 seconds!");
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
                            for (String pn : s.GetAlives()) {
                                PlayerInventory inv = Bukkit.getPlayer(pn).getInventory();
                                if (inv.contains(Material.ELYTRA))
                                    inv.remove(Material.ELYTRA);
                                if (inv.getChestplate().getType() == Material.ELYTRA)
                                    inv.setChestplate(new ItemStack(Material.AIR));
                            }
                        }
                    }
                    eltimer -= freq;
                }
            }

            //wingame processor
            //TODO replace alive counter
            int a = 0;
            squad f = plugin.EmptySquad;
            for (squad s : Teams)
            {
                if (s.HaveAlive())
                {
                    a++;
                    f = s;
                }
            }
            if (a<=1)
            {
                stopgame();
                Bukkit.broadcastMessage("§a" + f.name + " won the game!");
            }

            //Alert processor
            {
                for (squad s : Teams)
                    for (String pn : s.GetAlives())
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
                                    p.sendMessage("§cYou are not in safe zone! Type /zone for details");
                                    Alerts.replace(p, (p.getLocation().getBlockY()+10)*25);
                                }
                                else
                                    Alerts.replace(p, Alerts.get(p) - freq);
                            }
                            else
                            {
                                p.sendMessage("§cYou are not in safe zone! Type /zone for details");
                                Alerts.put(p, (p.getLocation().getBlockY()+10)*25);
                            }
                            //damage if outside
                            if (!wb.isInside(ploc)) {
                                Bukkit.getPluginManager().callEvent(new EntityDamageEvent(p, EntityDamageEvent.DamageCause.SUFFOCATION, wb.getDamageAmount()));
                            }
                        }
                    }
            }

            //TODO Alive Counter
            //debug log
            /*
            String s = "";
            s+= zt + ";";
            s+= zwt + ";";
            s+= zsht + ";";
            s+= xc + ";";
            s+= zc + ";";
            s+= nxc + ";";
            s+= nzc + ";";
            s+= zs + ";";
            s+= nzs + ";";
            s+= wb.getCenter().getBlockX() + ";";
            s+= wb.getCenter().getBlockZ() + ";";
            s+= wb.getSize() + ";";
            s+= ZoneShrink + ";";
            s+= FirstZone + ";\n";
            plugin.zonelog(s);
            */
        }
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
            sender.sendMessage("§cYou are not in safe zone!");

        sender.sendMessage("§6Current zone size: " + Integer.toString((int)wb.getSize()));
        sender.sendMessage("§6Next zone size: " + nzs);
        sender.sendMessage("§6Zone center: x=" + Integer.toString((int)nxc) + "; z=" + Integer.toString((int)nzc));
        sender.sendMessage("§6Your location: x=" + Integer.toString(ploc.getBlockX()) + "; z=" + Integer.toString(ploc.getBlockZ()));
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
            if (s.HaveAlive())
                a++;
        return a;
    }

    public int alivePlayers()
    {
        int a = 0;
        for (squad s : Teams)
            a+= s.GetAliveCount();
        return a;
    }
}
