package ru.sooslick.royale;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.logging.Logger;

public class royale extends JavaPlugin implements CommandExecutor, Listener
{
    public Logger LOG;
    public ru.sooslick.royale.royale ROYALE;
    public Runnable ZONE_SECOND_PROCESSOR;
    public Runnable SQUAD_INVITE_PROCESSOR;
    public int ZONE_TASK_ID;
    public int INVITE_TASK_ID;
    public zone GameZone;
    public squad EmptySquad;
    public FileConfiguration CFG;
    public World w;
    private ArrayList<squad> Squads = new ArrayList<>();
    private ArrayList<squadInvite> Invites = new ArrayList<>();
    public ArrayList<Player> Leavers = new ArrayList<>();
    public ArrayList<Player> Votestarters= new ArrayList<>();
    public int StartGameTimer = 60;
    public boolean StartGameCountdown = false;

    @Override
    public void onEnable()
    {
        ROYALE = this;
        LOG = getServer().getLogger();
        LOG.info("[Royale] Loading...");

        //datafolder
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
                saveDefaultConfig();
                LOG.info("[Royale] DataFolder created");
            }
        } catch (Exception ex) {
            LOG.warning("[Royale] " + ex);
        }
        this.CFG = getConfig();
        configFixMissing();
        this.saveConfig();
        this.reloadConfig();
        LOG.info("[Royale] Config");

        w = getServer().getWorlds().get(0); //get main world

        GameZone = new zone(this);
        GameZone.CFG = CFG;
        GameZone.init(w);
        LOG.info("[Royale] GameZone created");

        EmptySquad = new squad();
        Leavers.clear();

        getServer().getPluginManager().registerEvents(new eventHandler(this),this);
        getCommand("royale").setExecutor(new royaleCommand(this));
        getCommand("squad").setExecutor(new squadCommand(this));
        getCommand("zone").setExecutor(GameZone);
        getCommand("votestart").setExecutor(this);
        getCommand("rlconfig").setExecutor(this);
        //TODO fix royale.yml

        this.ZONE_SECOND_PROCESSOR = new Runnable() {
            @Override
            public void run()
            {
                GameZone.tickProcessor();
            }
        };

        this.SQUAD_INVITE_PROCESSOR = new Runnable() {
            @Override
            public void run()
            {
                for (squadInvite si : Invites)
                {
                    si.Timer--;
                    if (si.Timer<=0) {
                        Invites.remove(si);
                        break;
                    }       //костыльное решение чтобы не крашилось
                }
                if (StartGameCountdown)
                {
                    StartGameTimer--;
                    if (StartGameTimer==0) {
                        onStartgameCmd();
                        StartGameCountdown = false;
                    }
                    else if (Math.floorMod(StartGameTimer,10)==0)
                        alertEveryone("§a[Royale] Game starts in " + Integer.toString(StartGameTimer)+ " seconds!");
                }
            }
        };

        INVITE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, SQUAD_INVITE_PROCESSOR, 1, 20);

        LOG.info("[Royale] Plugin enabled!");
    }
    //TODO: Reload func

    //on StartGame
    //squad processor pre-start game
    public void onStartgameCmd()
    {
        if (GameZone.GameActive)
        {
            LOG.warning("[Royale] onStartgameCmd error: game is running!");
            return;
        }
        //create team 4 every player
        Team rl = Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam("Royale");
        rl.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers())
        {
            rl.addPlayer(p);
            boolean found = false;
            for (squad s : Squads)
                if (s.HasPlayer(p.getName())) {found = true; break;}
            if (!found)
            {
                squad s = new squad();
                s.leader = p.getName();
                s.Reset();
                s.AddPlayer(p.getName());
                Squads.add(s);
            }
            p.getInventory().clear();
            clearArmor(p);
            p.setGameMode(GameMode.SURVIVAL);
        }

        //teleport teams
        if (CFG.getBoolean("EnableElytraStart", true))
        {
            for (squad s: Squads) {
                GameZone.addTeam(s);
                for (String pname : s.GetPlayers())
                    s.RevivePlayer(pname);
            }
            Location loc = RandomLocation(CFG.getInt("StartZoneSize", 2048) - 100);
            loc.setY(CFG.getInt("StartFallHeight"));
            for (Player p : Bukkit.getOnlinePlayers())
            {
                loc.setX(loc.getX() + Math.random()*16 - 8);
                loc.setZ(loc.getZ() + Math.random()*16 - 8);
                p.teleport(loc);
                p.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
                GameZone.Flyers.add(p);

                ItemStack zonemap = new ItemStack(Material.MAP);
                MapView zmr = Bukkit.createMap(w);
                //zmr.getRenderers().clear();
                zmr.setUnlimitedTracking(true);
                zmr.setCenterX(0);
                zmr.setCenterZ(0);
                zmr.setScale(MapView.Scale.FARTHEST);
                int sc = 32;
                Renderer r = new Renderer();
                r.init(2055, sc);
                zmr.addRenderer(r);
                zonemap.setDurability(zmr.getId());
                p.getInventory().addItem(zonemap);
            }
        }
        else {
            for (squad s : Squads) {
                GameZone.addTeam(s);
                Location loc = RandomLocation(CFG.getInt("StartZoneSize", 2048) - 100);
                for (String pname : s.GetPlayers()) {
                    s.RevivePlayer(pname);
                    Player p = Bukkit.getPlayer(pname);
                    p.teleport(loc);
                    //elytra code
                    //if (CFG.CFG.getBoolean("EnableElytraStart", true)) {
                    //    p.getInventory().getChestplate().setType(Material.ELYTRA);
                    //}
                }
            }
        }

        //zone startgame
        GameZone.startgame();
        ZONE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, ZONE_SECOND_PROCESSOR, 1,CFG.getInt("RoyaleProcessorFrequency", 20));
        Bukkit.getScheduler().cancelTask(INVITE_TASK_ID);
        alertEveryone("§a[Royale] New game is started!");
    }

    public void reset()
    {
        INVITE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, SQUAD_INVITE_PROCESSOR, 1, 20);
        Bukkit.getScheduler().cancelTask(ZONE_TASK_ID);
    }

    public void onStopgameCmd()
    {
        if (!GameZone.GameActive)
        {
            LOG.warning("[Royale] onStopgameCmd error: game is not running!");
            return;
        }

        GameZone.stopgame();
        Leavers.clear();
        Bukkit.getScheduler().cancelTask(ZONE_TASK_ID);
        INVITE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, SQUAD_INVITE_PROCESSOR, 1, 20);
        alertEveryone("§c[Royale] Game is stopped by admin!");
    }

    public void onPausegameCmd()
    {
        LOG.warning("[Royale] onPausegameCmd error: broken feature!");
        alertEveryone("§c[Royale] Failed attempt to pause game!");
        return;
    }

    public void onContinuegameCmd()
    {
        LOG.warning("[Royale] onContinuegameCmd error: broken feature!");
        alertEveryone("§c[Royale] Failed attempt to start game!");
        return;
    }

    //TODO:
    //save players loc
    //save invs
    //save zone & timers
    //spectate

    //tODO
    //restore loc & invs
    //restore zone
    //start timer

    @Override
    public void onDisable()
    {
        //TODO Cfg saving
        LOG.info("[Royale] Saved configuration");
    }

    public void alertEveryone(String msg)    {        Bukkit.broadcastMessage(msg);    }

    public void alertPlayer(String msg, Player p) {p.sendMessage(msg);}

    public Location RandomLocation(int Max)
    {
        Location l = new Location(w, 0, 64, 0);
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * Max - Max/2);
            l.setX(x);
            int z = (int) (Math.random() * Max - Max/2);
            l.setZ(z);
            l.getChunk().load();
            int y = l.getWorld().getHighestBlockYAt(x, z);
            if ((w.getBlockAt(x, y - 1, z).getTypeId() <= 11) && (w.getBlockAt(x, y - 1, z).getTypeId() >= 8))
                continue;
            int footTypeId = w.getBlockAt(x, y, z).getTypeId();
            if ((w.getBlockAt(x, y + 1, z).getTypeId() != 0) || ((footTypeId != 0) && (footTypeId != 78) && (footTypeId != 31) && (footTypeId != 32) && (footTypeId != 6)))
                continue;
            l.setY(y);
            return l;
        }
        return l;
    }

    public squad onSquadCreate(Player creator) //TODO boolean
    {
        squad s = getSquad(creator);               //check creator not in squad
        if (s.equals(EmptySquad)) {
            s = new squad();
            s.leader = creator.getName();
            s.Reset();
            s.AddPlayer(creator.getName());
            Squads.add(s);
            alertPlayer("§a[Royale] Squad created!", creator);
            alertEveryone("§c[Royale] Squad \"" + s.name + "\" created!");
        }
        return s;
    }

    public void onSquadInvite(Player who,String whom)
    {
        squad s = getSquad(who);               //check if inviter in squad
        if (s.equals(EmptySquad)) {
            alertPlayer("§c[Royale] You are not a member of any squad!", who);
            return;
        }
        if (s.isFull())
        {
            alertPlayer("§c[Royale] Your squad is full", who);
            return;
        }
        boolean found = false;                      //check if "whom" on server
        Player p = who;                             //чтобы компилятор не ругался, что переменная мб не инициализирована. Код не пропустит параметр who в блок обработки команды
        for (Player p1 : Bukkit.getOnlinePlayers())
            if (p1.getName().equals(whom)) {found = true; p = p1; break;}
        if (!found) {
            alertPlayer("§c[Royale] Player you invited are offline!", who);
            return;
        }

         //check if "whom" in squad
        if (isPlayerInSquad(whom)) {
            alertPlayer("§c[Royale] Player you invited are member of squad!", who);
            return;
        }

        found = false;                  //check if "whom" has the invite
        for (squadInvite si : Invites)
            if (si.p.equals(p))
            {
                found = true;
                        break;
            }
        if (found) {
            alertPlayer("§c[Royale] Player have already invited!", who);
            return;
        }

        squadInvite si = new squadInvite();
        si.s = s;
        si.p = p;
        Invites.add(si);
        alertPlayer("§a[Royale] Invited!!", who);
        alertPlayer("§a[Royale] You are invited in squad by " + who.getName(), p);
    }

    public void onSquadInviteAccept(Player p) //TODO boolean
    {
        boolean found = false;
        for (squadInvite si : Invites)
            if (si.p.equals(p)) {
                found = true;
                if (si.s.isFull())
                {
                    alertPlayer("§c[Royale] You can't accept the invite bcs this squad is full now!", p);
                    alertPlayer("§c[Royale] Player " + p.getName() + " can't accept the invite bcs your squad is full!", Bukkit.getPlayer(si.s.leader));
                }
                else {
                    si.s.AddPlayer(p.getName());
                    alertPlayer("§a[Royale] You accepted the invite!", p);
                    alertPlayer("§a[Royale] Invitation accepted by " + p.getName(), Bukkit.getPlayer(si.s.leader));
                    alertEveryone("§c[Royale] Now the squad \"" + si.s.name + "\" have " + Integer.toString(si.s.GetPlayersCount()) + " members!");
                }
                Invites.remove(si);
                break;
            }
        if (!found) {
            alertPlayer("§c[Royale] Can't accept...", p);
            return;
        }
    }

    public void onSquadInviteDecline(Player p) //TODO boolean //why?
    {
        boolean found = false;
        squadInvite si11 = new squadInvite();       //TODO do same as accept
        for (squadInvite si : Invites)
            if (si.p.equals(p)) {found = true; si11 = si; break;}
        alertPlayer("§a[Royale] You declined the invite!", p);
        if (!found) return;

        //TODO other checks
        alertPlayer("§c[Royale] Player "+p.getName()+" declined your invite!", Bukkit.getPlayer(si11.s.leader));
        Invites.remove(si11);
    }

    public void onSquadLeave(Player p)
    {
        squad s = getSquad(p);               //check if leaver in squad
        if (s.equals(EmptySquad)){
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }

        s.KickPlayer(p.getName());
        alertPlayer("§c[Royale] you left the squad!", p);
        alertPlayer("§c[Royale] Player "+p.getName()+" left the squad!", Bukkit.getPlayer(s.leader));
        alertEveryone("§c[Royale] Now the squad \"" + s.name + "\" have " + Integer.toString(s.GetPlayersCount()) + " members!");
    }

    public void onSquadKick(Player p, String kicked)
    {
        squad s = getSquad(p);               //check kicked by leader
        if (s.equals(EmptySquad)) {
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }
        if (!s.leader.equals(p.getName())) {
            alertPlayer("§c[Royale] You are not the squad leader!", p);
            return;
        }

        for (String pn : s.GetPlayers())
        {
            if (pn.equals(kicked))
            {
                s.KickPlayer(kicked);
                alertPlayer("§c[Royale] kicked " + kicked, p);
                alertPlayer("§c[Royale] You have been kicked from the squad!", Bukkit.getPlayer(kicked));
                alertEveryone("§c[Royale] Now the squad \"" + s.name + "\" have " + Integer.toString(s.GetPlayersCount()) + " members!");
                return;
            }
        }
        alertPlayer("§c[Royale] Player "+kicked+" is not found in your squad!", p);
        return;
    }

    public void onSquadDisband(Player p)
    {
        squad s = getSquad(p);               //check disbanded by leader
        if (s.equals(EmptySquad)) {
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }
        if (!s.leader.equals(p.getName())) {
            alertPlayer("§c[Royale] You are not the squad leader!", p);
            return;
        }

        for (String teammate : s.GetPlayers())
            alertPlayer("§c[Royale] Your squad are disbanded!", Bukkit.getPlayer(teammate));
        for (squadInvite si : Invites)
        {
            if (si.s.equals(s))
            {
                alertPlayer("§c[Royale] Squad your invited are disbanded!", si.p);
                Invites.remove(si);
            }
        }
        alertEveryone("§c[Royale] Squad \"" + s.name + "\" disbanded!");
        Squads.remove(s);
    }

    public void onSquadView(Player p)
    {
        squad s = getSquad(p);                //check if inviter in squad
        if (s.equals(EmptySquad)) {
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }

        p.sendMessage("§6Squad name: " + s.name);
        p.sendMessage("§6Squad leader: " + s.leader);
        p.sendMessage("§6Squad members:");
        for (String pn : s.GetPlayers())
            p.sendMessage("§e - " + pn);
    }

    public boolean isPlayerInSquad(String pn)
    {
        for (squad s : Squads) {
            if (s.HasPlayer(pn)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAliveInSquad(String pn)
    {
        for (squad s : Squads) {
            if (s.HasAlive(pn)) {
                return true;
            }
        }
        return false;
    }

    public squad getSquad(Player p)
    {
        for (squad s : Squads) {
            if (s.HasPlayer(p.getName())) {
                return s;
            }
        }
        return EmptySquad;
    }

    public void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public void InvToChest(Player p)
    {
        Location tempLoc = p.getLocation();
        Block tempBlock = tempLoc.getBlock();
        tempBlock.setType(Material.CHEST);
        Chest c = (Chest) tempBlock.getState();
        int items = 0;
        for (ItemStack IS : p.getInventory().getContents())
        {
            if (IS != null)
            {
                c.getInventory().addItem(IS);
                items++;
                if (items == 27)
                {
                    tempLoc.setX(tempLoc.getBlockX() + 1);
                    tempBlock = tempLoc.getBlock();
                    tempBlock.setType(Material.CHEST);
                    c = (Chest) tempBlock.getState();
                }
            }
        }
        for (ItemStack IS : p.getInventory().getArmorContents())
        {
            if (IS != null)
            {
                c.getInventory().addItem(IS);
                items++;
                if (items == 27)
                {
                    tempLoc.setX(tempLoc.getBlockX() + 1);
                    tempBlock = tempLoc.getBlock();
                    tempBlock.setType(Material.CHEST);
                    c = (Chest) tempBlock.getState();
                }
            }
        }
        p.getInventory().clear();
        clearArmor(p);
    }

    //Votestart
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equals("votestart"))
        {
            if (GameZone.GameActive)
            {
                sender.sendMessage("GAME IS RUNNING");  //todo fix messags
                return true;
            }
            if (!sender.hasPermission("royale.vote"))
            {
                sender.sendMessage("§c[Royale] req royale.vote permission");
                return true;
            }
            if (StartGameCountdown) {
                sender.sendMessage("§c[Royale] Game starts in " + StartGameTimer + " seconds!");
                return true;
            }
            if (sender instanceof Player)
            {
                Player p = (Player) sender;
                if (!Votestarters.contains(p))
                {
                    Votestarters.add(p);
                    //TODO get cfg values
                    if ((Votestarters.size() / Bukkit.getOnlinePlayers().size() > 0.5) || (Votestarters.size() > 3)) {
                        StartGameCountdown = true;
                        StartGameTimer = 60;
                        alertEveryone("§a[Royale] Game start in 60 sec!");
                    }
                    p.sendMessage("§a[Royale] start vote accepted");
                }
                else {
                    p.sendMessage("§c[Royale] You have voted yet");
                    return true;
                }
            } else
                sender.sendMessage("Console can't vote to start");
        }
        else {
            //config command
            if (!sender.hasPermission("royale.admin"))
            {
                sender.sendMessage("§c[Royale] req royale.admin permission");
                return true;
            }
            if (args.length == 0)
            {
                sender.sendMessage("§cConfig commands: reload, save, defaults, setparam");
                return true;
            }
            if (args[0].equals("reload")) {
                reloadConfig();
                configFixMissing();
                sender.sendMessage("Config reloaded. ");
                return true;
            }
            if (args[0].equals("save")) {
                saveConfig();
                sender.sendMessage("Config saved. ");
                return true;
            }
            if (args[0].equals("defaults")) {
                sender.sendMessage("we don't realie this feature at the moment ");
                return true;
            }
            if (args[0].equals("setparam")) {
                sender.sendMessage("we don't realie this feature at the moment ");
                return true;
            }
            sender.sendMessage("Wrong command");
        }
        return true;
    }

    public void configFixMissing()
    {
        if (CFG.getInt("StartZoneSize", 2048)<20) {
            CFG.set("StartZoneSize", 2048);
            LOG.info("[ROYALE] Fixed start zone size. New size = 2048 blocks" );
        }
        if (CFG.getInt("PreStartZoneSize", 2055)<=CFG.getInt("StartZoneSize")) {
            CFG.set("PreStartZoneSize", 2055);
            LOG.info("[ROYALE] Fixed pre-start zone size. New size = 2055 blocks");
        }
        if (CFG.getDouble("NewZoneSizeMultiplier", 0.5D)>=1) {
            CFG.set("NewZoneSizeMultiplier", 0.5D);
            LOG.info("[ROYALE] Fixed new zone size multiplier. New qf = 0.5");
        }
        if (CFG.getInt("StartTimer", 300)<=60) {
            CFG.set("StartTimer", 300);
            LOG.info("[ROYALE] Fixed start timer. Set timer on 300 seconds");
        }
        if (CFG.getInt("RoyaleProcessorFrequency", 20)<1) {
            CFG.set("RoyaleProcessorFrequency", 20);
            LOG.info("[ROYALE] Fixed royale processor frequency. Data will be calculated every second");
        }
        if (CFG.getDouble("WaitMultiplier", 0.75)>=1) {
            CFG.set("WaitMultiplier", 0.75);
            LOG.info("[ROYALE] Fixed zone wait timer multiplier. New qf = 0.75");
        }
        if (CFG.getDouble("ShrinkMultiplier", 0.67)>=1) {
            CFG.set("ShrinkMultiplier", 0.67);
            LOG.info("[ROYALE] Fixed zone wait timer multiplier. New qf = 0.67");
        }
        CFG.getBoolean("EnableCenterOffset", true);
        LOG.info("[ROYALE] Zone center offset enabled? "+ Boolean.toString(CFG.getBoolean("EnableCenterOffset")));
        if (CFG.getInt("LavaFlowSpeed", 20)<1) {
            CFG.set("LavaFlowSpeed", 20);
            LOG.info("[ROYALE] Fixed lava flow speed. New speed = 1 block per second");
        }
        CFG.getBoolean("EnableRedzone", true);
        LOG.info("[ROYALE] Redzone enabled? "+ Boolean.toString(CFG.getBoolean("EnableRedzone")));
        if (CFG.getInt("RedzoneRadius", 25)<1) {
            CFG.set("RedzoneRadius", 25);
            LOG.info("[ROYALE] Fixed redzone radius. New radius = 25 blocks");
        }
        if (CFG.getInt("RedzoneLength", 100)<1) {
            CFG.set("RedzoneLength", 100);
            LOG.info("[ROYALE] Fixed redzone length. 100 tnts will be detonated while active");
        }
        if (CFG.getInt("RedzoneDensity", 5)<1) {
            CFG.set("RedzoneDensity", 5);
            LOG.info("[ROYALE] Fixed redzone density. New density = 5 tnts per zone processor tick");
        }
        if (CFG.getInt("FirstRedzoneTime", 350)<1) {
            CFG.set("FirstRedzoneTime", 350);
            LOG.info("[ROYALE] Redzone will be enabled after 350 seconds from start");
        }
        if (CFG.getInt("RedzoneMinPause", 40)<1) {
            CFG.set("RedzoneMinPause", 40);
            LOG.info("[ROYALE] Fixed minimum time between redzones. New min = 40");
        }
        if (CFG.getInt("RedzoneMaxPause", 90)<1) {
            CFG.set("RedzoneMaxPause", 90);
            LOG.info("[ROYALE] Fixed maximum time between redzones. New max = 90");
        }
        if (CFG.getInt("RedzoneMinZoneSize", 250)<1) {
            CFG.set("RedzoneMinZoneSize", 250);
            LOG.info("[ROYALE] Redzone will be disabled when game zone reaches 250 blocks");
        }
        CFG.getBoolean("EnableMonsters", true);
        LOG.info("[ROYALE] Monsters enabled? "+ Boolean.toString(CFG.getBoolean("EnableMonsters")));
        if (CFG.getInt("EnableMonstersTime", 310)<1) {
            CFG.set("EnableMonstersTime", 310);
            LOG.info("[ROYALE] Monsters will be enabled after 310 seconds from start");
        }
        if (CFG.getDouble("ZoneStartDamage", 0.01)<=0) {
            CFG.set("ZoneStartDamage", 0.01);
            LOG.info("[ROYALE] Fixed zone start damage. New damage = 0.01 health point per block");
        }
        if (CFG.getDouble("ZoneDamageMultiplier", 2)<=1) {
            CFG.set("ZoneDamageMultiplier", 2);
            LOG.info("[ROYALE] Fixed zone damage multiplier. Damage will be multiplied by 2 every zone");
        }
        CFG.getBoolean("GiveZoneMap", true);
        LOG.info("[ROYALE] Give players zone map? "+ Boolean.toString(CFG.getBoolean("GiveZoneMap")));
        CFG.getBoolean("EnableElytraStart", true);
        LOG.info("[ROYALE] Does the game start with elytra fall? "+ Boolean.toString(CFG.getBoolean("EnableElytraStart")));
        if (CFG.getInt("StartFallHeight", 2500)<1000) {
            CFG.set("StartFallHeight", 2500);
            LOG.info("[ROYALE] Fixed start height to elytra flight. New height is 2500 blocks");
        }
        if (CFG.getMapList("MonsterSpawns").size()==0)
            LOG.info("[Royale] MonsterSpawns list is empty?");
        //TODO fix monster list
        // - Ограничитель количества мобов (и конкретных типов)

        //- аирдроп вкл / выкл
        //    - Частота спавна

        //    - генерация структур вкл / выкл
        //  - всякие параметры плотности и спавна сундуков с лутом
        //TODO min players votestart
        //TODO min % votestart
        //todo config param change ingame + cfg save
    }

    public void zonelog(String s)
    {
        String filePath = getDataFolder().getPath() + "zonelog.csv";
        try {
            Files.write(Paths.get(filePath), s.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            LOG.info(e.toString());
        }
    }
}
//TODO: player stats & cnfigs: enable random squad 4 balancing