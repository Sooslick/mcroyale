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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
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
    public Runnable SD_PROCESSOR;
    public int ZONE_TASK_ID;
    public int INVITE_TASK_ID;
    public int SD_TASK_ID;
    public zone GameZone;
    public FileConfiguration CFG;
    public World w;
    public Scoreboard sb;
    public Team tm;
    public ArrayList<squad> Squads = new ArrayList<>();
    private ArrayList<squadInvite> Invites = new ArrayList<>();
    public ArrayList<String> Leavers = new ArrayList<>();
    public ArrayList<Player> Votestarters= new ArrayList<>();
    public int StartGameTimer = 60;
    public boolean StartGameCountdown = false;
    public int ShutDownTimer = 60;
    public boolean ShutDownCountdown = false;

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
                LOG.info("[Royale] DataFolder created");
            }
        } catch (Exception ex) {
            LOG.warning("[Royale] " + ex);
            LOG.warning("[Royale] Has server necessary permissions to rw datafolder?");
        }
        try {
            File f = new File(getDataFolder().toString() + File.separator + "plugin.yml" );
            if (!f.exists()) {
                saveDefaultConfig();
                LOG.info("[Royale] Default config created!");
            }
        } catch (Exception ex) {
            LOG.warning("[Royale] " + ex);
        }
        this.CFG = getConfig();
        configFixMissing();
        this.saveConfig();              //todo save config problems
        this.reloadConfig();
        LOG.info("[Royale] Read config");

        w = getServer().getWorlds().get(0); //get main world

        GameZone = new zone(this);
        GameZone.CFG = CFG;
        GameZone.init(w);
        LOG.info("[Royale] GameZone prepared");

        Squads.clear();
        Invites.clear();
        Leavers.clear();
        Votestarters.clear();

        getServer().getPluginManager().registerEvents(new eventHandler(this),this);
        getCommand("royale").setExecutor(new royaleCommand(this));
        getCommand("squad").setExecutor(new squadCommand(this));
        getCommand("zone").setExecutor(GameZone);
        getCommand("votestart").setExecutor(this);
        getCommand("rlconfig").setExecutor(this);

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

        this.SD_PROCESSOR = new Runnable() {
            @Override
            public void run() {
                ShutDownTimer--;
                if (ShutDownTimer < 0) {
                    alertEveryone("§a[Royale] Restarting server!");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                }
            }
        };

        INVITE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, SQUAD_INVITE_PROCESSOR, 1, 20);

        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        tm = sb.registerNewTeam("Royale");
        tm.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        LOG.info("[Royale] Plugin enabled!");
    }

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
        for (Player p : Bukkit.getOnlinePlayers())
        {
            tm.addEntry(p.getName());
            p.setScoreboard(sb);
            boolean found = false;
            for (squad s : Squads)
                if (s.HasPlayer(p.getName())) {found = true; break;}
            if (!found)
            {
                squad s = new squad(p, p.getName());
                Squads.add(s);
            }
            p.getInventory().clear();
            clearArmor(p);
            p.setCompassTarget(new Location(w, 0, 0, 0));
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
            p.setGameMode(GameMode.SURVIVAL);
            p.setFoodLevel(20);
            p.setHealth(20);
            GameZone.alive++;
        }

        //teleport teams
        if (CFG.getBoolean("EnableElytraStart", true))
        {
            for (squad s: Squads) {
                GameZone.addTeam(s);
                GameZone.aliveTeams++;
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
                r.init(2055, sc, w.getWorldBorder());
                zmr.addRenderer(r);
                zonemap.setDurability(zmr.getId());
                p.getInventory().addItem(zonemap);
            }
        }
        else {
            for (squad s : Squads) {
                GameZone.addTeam(s);
                GameZone.aliveTeams++;
                Location loc = RandomLocation(CFG.getInt("StartZoneSize", 2048) - 100);
                for (String pname : s.GetPlayers()) {
                    s.RevivePlayer(pname);
                    Player p = Bukkit.getPlayer(pname);
                    p.teleport(loc);
                }
            }
        }

        //zone startgame
        GameZone.startgame();
        ZONE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, ZONE_SECOND_PROCESSOR, 1,CFG.getInt("RoyaleProcessorFrequency", 20));
        Bukkit.getScheduler().cancelTask(INVITE_TASK_ID);
        alertEveryone("§a[Royale] New game is started!");
    }

    public void endgame()
    {
        if (CFG.getBoolean("PostGameShutDown", true))
        {
            ShutDownTimer = CFG.getInt("PostGameShutDownTimer", 60);
            ShutDownCountdown = true;
            alertEveryone("§c[Royale] Game is ended. Server will be restarted in " + ShutDownTimer + " seconds!");
            SD_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, SD_PROCESSOR, 1, 20);
        }
        else
            reset();
    }

    public void reset()
    {
        INVITE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, SQUAD_INVITE_PROCESSOR, 1, 20);
        Bukkit.getScheduler().cancelTask(ZONE_TASK_ID);
        Invites.clear();
        Leavers.clear();
        Votestarters.clear();
        //tm.getEntries()
        ShutDownTimer = CFG.getInt("PostGameShutDownTimer", 60);
        ShutDownCountdown = false;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.SPECTATOR);
            p.getInventory().clear();
            clearArmor(p);
        }
        //todo this code requires testing. Something is wrong

        //todo: track created chests and clear it
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

    //TODO: pause
    //save players loc
    //save invs
    //save zone & timers
    //spectate

    //tODO: cont
    //restore loc & invs
    //restore zone
    //start timer

    public void cancelShutDown() {
        if (ShutDownCountdown) {
            Bukkit.getScheduler().cancelTask(SD_TASK_ID);
            reset();
            alertEveryone("§c[Royale] Cancelled shutting down by admin. Preparing to new royale game...");
        }
    }

    @Override
    public void onDisable()
    {
        saveConfig();
        LOG.info("[Royale] Saved configuration");
    }

    public void alertEveryone(String msg) {Bukkit.broadcastMessage(msg);}

    public void alertPlayer(String msg, Player p) {p.sendMessage(msg);}

    //todo check dangerous locations
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

    public squad onSquadCreate(Player creator, String name) //TODO boolean
    {
        squad s = getSquad(creator);
        //check if creator not in squad
        if (s == null) {
            s = new squad(creator, name);
            Squads.add(s);
            alertPlayer("§a[Royale] Squad created!", creator);
            alertEveryone("§c[Royale] Squad \"" + s.name + "\" created!");
            //todo: remove all pending invites with creator
        }
        else
            alertPlayer("§a[Royale] You are member of squad!", creator);
        return s;
    }

    //todo: cfg squad members cap
    public void onSquadInvite(Player who,String whom)
    {
        squad s = getSquad(who);               //check if inviter in squad
        if (s == null) {
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
        if (s == null){
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }
        if (s.leader.equals(p.getName())) {
            alertPlayer("§c[Royale] You can't leave from your squad. Use /squad disband",p);
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
        if (s == null) {
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }
        if (!s.leader.equals(p.getName())) {
            alertPlayer("§c[Royale] You are not the squad leader!", p);
            return;
        }
        if (s.leader.equals(p.getName())) {
            alertPlayer("§c[Royale] You can't kick yourself from squad. Use /squad disband",p);
            return;
        }

        for (String pn : s.GetPlayers())
        {
            if (pn.equals(kicked))
            {
                s.KickPlayer(kicked);
                alertPlayer("§c[Royale] kicked " + kicked + " from your squad", p);
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
        if (s == null) {
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

    public void onSquadView(Player p, String sn)
    {
        squad s = null;
        if (sn.equals("")) {
            s = getSquad(p);                //check if requester in squad
            if (s == null) {
                alertPlayer("§c[Royale] You are not in squad!", p);
                return;
            }
        }
        else {
            for (squad s1 : Squads)
                if (s1.name.equals(sn)) {
                    s = s1;
                    break;
                }
            if (s == null) {
                alertPlayer("§c[Royale] Squad \""+sn+"\" not found!", p);
                return;
            }
        }

        p.sendMessage("§6Squad name: " + s.name);
        p.sendMessage("§6Squad leader: " + s.leader);
        p.sendMessage("§6Squad members:");
        for (String pn : s.GetPlayers())
            p.sendMessage("§e - " + pn);
    }

    public void sendSquadList(CommandSender a) {
        String str = "§e";
        for (squad s : Squads)
            str = str + s.name + ", ";
        a.sendMessage(str);
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
        return null;
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
        /* there is a bug: armor dublicating in chest. Actual for older spigot versions?
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
        */
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
                sender.sendMessage("GAME IS RUNNING");
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
                    if ((Votestarters.size() / Bukkit.getOnlinePlayers().size() >= CFG.getDouble("MinVotestartPercent", 0.5)) || (Votestarters.size() >= CFG.getInt("MinVotestarts", 3))) {
                        StartGameCountdown = true;
                        StartGameTimer = 60;
                        alertEveryone("§a[Royale] Game start in 60 sec!");
                    }
                    p.sendMessage("§a[Royale] start vote accepted");
                    alertEveryone("§a[Royale] " + p.getName() + " voted to start!");
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
                reloadConfig();                 //todo debug
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
                sender.sendMessage("we don't realize this feature at the moment "); //todo
                return true;
            }
            if (args[0].equals("setparam")) {
                sender.sendMessage("we don't realize this feature at the moment "); //todo
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
        if (CFG.getInt("PreStartTimer", 60)<1) {
            CFG.set("PreStartTimer", 60);
            LOG.info("[ROYALE] Fixed pre-start timer. First zone will be assigned in 60 seconds");
        }
        if (CFG.getInt("EndZoneSize", 100)<=0) {
            CFG.set("EndZoneSize", 100);
            LOG.info("[ROYALE] Fixed last zone size. New size = 100 blocks");
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
        if (CFG.getInt("LavaFlowZoneSize", 16)>25) {
            CFG.set("LavaFlowZoneSize", 16);
            LOG.info("[ROYALE] Fixed lava flow zone size. New size = 16 blocks");
        }
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
        if (CFG.getConfigurationSection("MonsterSpawns").getKeys(false).size()==0)
            LOG.info("[Royale] MonsterSpawns list is empty?");
        if (CFG.getInt("MinVotestarts", 3)<1) {
            CFG.set("MinVotestarts", 3);
            LOG.info("[ROYALE] Fixed minimal number of players are voted to start to proceed. Game starts after 3 votes");
        }
        if (CFG.getDouble("MinVotestartPercent", 0.5)>1) {
            CFG.set("MinVotestartPercent", 0.5);
            LOG.info("[ROYALE] Fixed minimal percent of voted players to game start.");
        }
        CFG.getBoolean("PostGameShutDown", true);
        LOG.info("[ROYALE] Should the server automatically reboot after game? "+ Boolean.toString(CFG.getBoolean("PostGameShutDown")));
        if (CFG.getInt("PostGameShutDownTime", 60)<1) {
            CFG.set("PostGameShutDownTime", 60);
            LOG.info("[ROYALE] Fixed time to shutdown server after game end. New time is 60 seconds.");
        }

        // - Ограничитель количества мобов (и конкретных типов)

        //- аирдроп вкл / выкл
        //    - Частота спавна

        //    - генерация структур вкл / выкл
        //  - всякие параметры плотности и спавна сундуков с лутом
        //todo config param change ingame
        //todo nametag visiblity param
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

//todo track every container to regen map w/ chests