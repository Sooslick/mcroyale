package ru.sooslick.royale;

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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static Scoreboard sb;
    public static Team.OptionStatus tmo;
    public static boolean ff;
    public ArrayList<squad> Squads = new ArrayList<>();
    private ArrayList<squadInvite> Invites = new ArrayList<>();
    public ArrayList<String> Leavers = new ArrayList<>();
    public ArrayList<String> Votestarters= new ArrayList<>();
    public int StartGameTimer = 60;
    public boolean StartGameCountdown = false;
    public int ShutDownTimer = 60;
    public boolean ShutDownCountdown = false;

    @Override
    public void onEnable()
    {
        //init globals
        ROYALE = this;
        LOG = getServer().getLogger();
        LOG.info("[Royale] Loading...");

        //create alternate scoreboard
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        squad.sb = sb;

        //datafolder check & create
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
                LOG.info("[Royale] DataFolder created");
            }
        } catch (Exception ex) {
            LOG.warning("[Royale] " + ex);
            LOG.warning("[Royale] Has server necessary permissions to rw datafolder?");
        }
        //cfg check & create
        try {
            File f = new File(getDataFolder().toString() + File.separator + "plugin.yml" );
            if (!f.exists()) {
                ROYALE.saveDefaultConfig();
                LOG.info("[Royale] Default config created!");
            }
        } catch (Exception ex) {
            LOG.warning("[Royale] " + ex);
        }
        //save cfg & reload
        CFG = ROYALE.getConfig();          //assign original cfg file
        configFixMissing();                 //write or repair bad fields
        ROYALE.saveConfig();
        ROYALE.reloadConfig();             //apply changes
        CFG = ROYALE.getConfig();          //reassign repaired config
        LOG.info("[Royale] Read config");

        //prepare gamezone
        w = getServer().getWorlds().get(0); //get main world
        GameZone = new zone(this);
        GameZone.CFG = CFG;
        GameZone.init(w);
        LOG.info("[Royale] GameZone prepared");

        //prepare lists
        Squads.clear();
        Invites.clear();
        Leavers.clear();
        Votestarters.clear();

        //prepare executors
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
                    }       //костыльное решение чтобы не крашилось. todo fix it
                }
                if (StartGameCountdown)
                {
                    StartGameTimer--;
                    if (StartGameTimer==0) {
                        onStartgameCmd(false);
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
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), CFG.getString("PostGameCommand", "stop"));
                }
            }
        };

        //run background squad task
        INVITE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, SQUAD_INVITE_PROCESSOR, 1, 20);
        LOG.info("[Royale] Plugin enabled!");
    }

    //squad processor pre-start game
    public void onStartgameCmd(boolean debug)
    {
        //check post-game status
        if (ShutDownCountdown)
            reset();
        //check running game
        if (GameZone.GameActive)
        {
            LOG.warning("[Royale] onStartgameCmd error: game is running!");
            return;
        }
        //clear tracked containers
        if (CFG.getBoolean("EnableChestTracking", true))
            GameZone.restoreChests();
        //balance squads
        if (CFG.getBoolean("EnableSquadBalancing", true))
            autobalance();
        //create team 4 every player if someone missed & respawn him
        for (Player p : Bukkit.getOnlinePlayers())
            respawnPlayer(p);

        //teleport teams
        if (CFG.getBoolean("EnableElytraStart", true))
        {
            for (squad s: Squads) {
                s.joinGame();                                               //todo squad joingame: refactor
                s.tm.setOption(Team.Option.NAME_TAG_VISIBILITY, tmo);
                s.tm.setAllowFriendlyFire(ff);                              //todo remove this flags to squad constructor. Wtf?
                GameZone.addTeam(s);
                GameZone.aliveTeams++;
            }
            Location loc = RandomLocation(CFG.getInt("StartZoneSize", 2048) - 100);
            loc.setY(CFG.getInt("StartFallHeight"));
            GameZone.giveMap();
        }
        else {
            for (squad s : Squads) {
                s.joinGame();
                s.tm.setOption(Team.Option.NAME_TAG_VISIBILITY, tmo);
                s.tm.setAllowFriendlyFire(ff);
                GameZone.addTeam(s);                                        //add team + aliveteams: should ref? todo
                GameZone.aliveTeams++;
                Location loc = RandomLocation(CFG.getInt("StartZoneSize", 2048) - 100);
                for (String pname : s.getPlayers()) {
                    Player p = Bukkit.getPlayer(pname);
                    p.teleport(loc);
                }
            }
        }

        //zone startgame
        GameZone.startgame(debug);
        ZONE_TASK_ID = getServer().getScheduler().scheduleSyncRepeatingTask(this, ZONE_SECOND_PROCESSOR, 1,CFG.getInt("RoyaleProcessorFrequency", 20));
        Bukkit.getScheduler().cancelTask(INVITE_TASK_ID);
        alertEveryone("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        alertEveryone("§a[Royale] New game is started!");
        alertEveryone("§a[Royale] Game Zone will be marked soon");
        alertEveryone("§a[Royale] Use §6/zone §acommand to check actual zone size. Now zone is §6"+CFG.getInt("StartZoneSize", 2048)+" §ablocks wide");
    }

    public void endgame()
    {
        if (CFG.getBoolean("PostGameCommandEnable", true))
        {
            ShutDownTimer = CFG.getInt("PostGameCommandTimer", 60);
            ShutDownCountdown = true;
            alertEveryone("§c[Royale] Game is ended!");
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
        ShutDownTimer = CFG.getInt("PostGameCommandTimer", 60);
        ShutDownCountdown = false;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.SPECTATOR);
            p.getInventory().clear();
            clearArmor(p);
        }
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

    public Location RandomLocation(int Max)
    {
        Location l = new Location(w, 0, 64, 0);
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * Max - Max/2);
            l.setX(x);
            int z = (int) (Math.random() * Max - Max/2);
            l.setZ(z);
            l.getChunk().load();
            int y = l.getWorld().getHighestBlockYAt(x, z); //highest y+1 *
            //liquid check
            if ((w.getBlockAt(x, y-1, z).getTypeId() <= 11) && (w.getBlockAt(x, y-1, z).getTypeId() >= 8))
                continue;
            int footTypeId = w.getBlockAt(x, y, z).getTypeId();
            if ((w.getBlockAt(x, y + 1, z).getTypeId() != 0) || ((footTypeId != 0) && (footTypeId != 78) && (footTypeId != 31) && (footTypeId != 32) && (footTypeId != 6)))
                continue;
            l.setY(y+1);
            l.setX(l.getX()+0.5);
            l.setZ(l.getZ()+0.5);
            return l;
        }
        return l;
    }

    public void autobalance()
    {
        //get open squad list, avg & max members count 4 further calculations
        ArrayList<squad> opened = new ArrayList<>();
        int max = 0;
        double avg = 0;
        for (squad s : Squads) {
            if (s.getPlayersCount() > max)
                max = s.getPlayersCount();
            avg+= s.getPlayersCount();
            if (s.getOpen())
                opened.add(s);
        }
        avg/= Squads.size();

        //get solo plrs list & count
        ArrayList<Player> solist = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            squad s = getSquad(p);
            if (s == null)
                solist.add(p);
        }

        //if avg squad > 1 -> req balancing
        if (max > 1) {
            //if solo
            if (solist.size() == 1) {
                //get squad w/ min amount of members
                int min = squad.MaxMembers;                     //if each opened squad is full
                squad target = null;                            //solo player will play as solo player
                for (squad s : opened)
                    if (s.getPlayersCount() < min) {
                        min = s.getPlayersCount();
                        target = s;
                    }
                if (target != null) {
                    target.addPlayer(solist.get(0).getName());
                    //todo squad message and nametag
                }
            }
            //else balance to avg count
            else if (solist.size() > 1) {
                double sqs_mems = solist.size() / (Math.ceil( solist.size() / avg ));
                double required = sqs_mems;
                double processed = 0;
                squad curr_squad = null;
                for (Player p : solist) {
                    //check last solo
                    if (processed+1 == solist.size()) {
                        //get squad w/ min amount of members
                        int min = squad.MaxMembers;                     //if each opened squad is full
                        squad target = null;                            //solo player will play as solo player
                        for (squad s : opened)
                            if (s.getPlayersCount() < min) {
                                min = s.getPlayersCount();
                                target = s;
                            }
                        if (target != null)
                            target.addPlayer(solist.get(0).getName());
                            //todo refactor solo balance
                            //todo: possiblity to add solo-player in auto-created squads?
                    }
                    //otherwise balance players
                    else if (curr_squad == null)
                        curr_squad = new squad(p, p.getName());
                    else
                        curr_squad.addPlayer(p.getName());
                    processed++;
                    if (processed >= required) {
                        required+= sqs_mems;
                        curr_squad = null;
                    }
                }
            }
        }
    }

    public void onSquadCreate(Player creator, String name)
    {
        //check if squad with same name exists
        squad s = getSquad(name);
        if (s != null) {
            alertPlayer("§a[Royale] There is squad with same name!", creator);
            return;
        }
        //check if creator not in squad
        s = getSquad(creator);
        if (s != null) {
            alertPlayer("§a[Royale] You are member of squad!", creator);
            return;
        }
        //create squad
        s = new squad(creator, name);
        Squads.add(s);
        alertPlayer("§a[Royale] Squad created!", creator);
        alertEveryone("§c[Royale] Squad \"" + s.getName() + "\" created!");
        //remove pending invites
        for (squadInvite si : Invites) {
            if (si.p.equals(creator)) {
                Invites.remove(creator);
                break;
            }
        }
    }

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

    public void onSquadInviteAccept(Player p)
    {
        boolean found = false;
        for (squadInvite si : Invites)
            if (si.p.equals(p)) {
                found = true;
                if (si.s.isFull())
                {
                    alertPlayer("§c[Royale] You can't accept the invite bcs this squad is full now!", p);
                    alertPlayer("§c[Royale] Player " + p.getName() + " can't accept the invite bcs your squad is full!", Bukkit.getPlayer(si.s.getLeader()));
                }
                else {
                    si.s.addPlayer(p.getName());
                    alertPlayer("§a[Royale] You accepted the invite!", p);
                    alertPlayer("§a[Royale] Invitation accepted by " + p.getName(), Bukkit.getPlayer(si.s.getLeader()));
                    alertEveryone("§c[Royale] Now the squad \"" + si.s.getName() + "\" have " + Integer.toString(si.s.getPlayersCount()) + " members!");
                }
                Invites.remove(si);
                break;
            }
        if (!found) {
            alertPlayer("§c[Royale] Can't accept...", p);
            return;
        }
    }

    public void onSquadInviteDecline(Player p)
    {
        squadInvite si11 = null;
        for (squadInvite si : Invites)
            if (si.p.equals(p)) {
                si11 = si;
                break;
            }
        if (si11 == null) {
            alertPlayer("§a[Royale] You haven't any invites!", p);
            return;
        }
        alertPlayer("§a[Royale] You declined the invite!", p);
        alertPlayer("§c[Royale] Player "+p.getName()+" declined your invite!", Bukkit.getPlayer(si11.s.getLeader()));
        Invites.remove(si11);
    }

    public void onSquadLeave(Player p)
    {
        squad s = getSquad(p);               //check if leaver in squad
        if (s == null){
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }
        if (s.getLeader().equals(p.getName())) {
            alertPlayer("§c[Royale] You can't leave from your squad. Use /squad disband",p);
            return;
        }

        s.kickPlayer(p.getName());
        alertPlayer("§c[Royale] you left the squad!", p);
        alertPlayer("§c[Royale] Player "+p.getName()+" left the squad!", Bukkit.getPlayer(s.getLeader()));
        alertEveryone("§c[Royale] Now the squad \"" + s.getName() + "\" have " + Integer.toString(s.getPlayersCount()) + " members!");
    }

    public void onSquadKick(Player p, String kicked)
    {
        squad s = getSquad(p);               //check kicked by leader
        if (s == null) {
            alertPlayer("§c[Royale] You are not in squad!", p);
            return;
        }
        if (!s.getLeader().equals(p.getName())) {
            alertPlayer("§c[Royale] You are not the squad leader!", p);
            return;
        }
        if (s.getLeader().equals(kicked)) {
            alertPlayer("§c[Royale] You can't kick yourself from squad. Use /squad disband",p);
            return;
        }

        for (String pn : s.getPlayers())
        {
            if (pn.equals(kicked))
            {
                s.kickPlayer(kicked);
                alertPlayer("§c[Royale] kicked " + kicked + " from your squad", p);
                alertPlayer("§c[Royale] You have been kicked from the squad!", Bukkit.getPlayer(kicked));
                alertEveryone("§c[Royale] Now the squad \"" + s.getName() + "\" have " + Integer.toString(s.getPlayersCount()) + " members!");
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
        if (!s.getLeader().equals(p.getName())) {
            alertPlayer("§c[Royale] You are not the squad leader!", p);
            return;
        }

        for (String teammate : s.getPlayers())
            alertPlayer("§c[Royale] Your squad are disbanded!", Bukkit.getPlayer(teammate));
        for (squadInvite si : Invites)
        {
            if (si.s.equals(s))
            {
                alertPlayer("§c[Royale] Squad your invited are disbanded!", si.p);
                Invites.remove(si);
            }
        }
        alertEveryone("§c[Royale] Squad \"" + s.getName() + "\" disbanded!");
        s.tm.removeEntry(s.getLeader());
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
                if (s1.getName().equals(sn)) {
                    s = s1;
                    break;
                }
            if (s == null) {
                alertPlayer("§c[Royale] Squad \""+sn+"\" not found!", p);
                return;
            }
        }

        p.sendMessage("§6Squad name: " + s.getName());
        p.sendMessage("§6Squad leader: " + s.getLeader());
        p.sendMessage("§6Squad members:");
        for (String pn : s.getPlayers())
            p.sendMessage("§e - " + pn);
    }

    public void sendSquadList(CommandSender a) {
        String str = "§e";
        for (squad s : Squads)
            str = str + s.getName() + ", ";
        a.sendMessage(str);
    }

    public boolean isPlayerInSquad(String pn) {
        for (squad s : Squads)
            if (s.hasPlayer(pn))
                return true;
        return false;
    }

    public boolean isAliveInSquad(String pn) {
        for (squad s : Squads)
            if (s.hasAlive(pn))
                return true;
        return false;
    }

    public squad getSquad(Player p) {
        for (squad s : Squads)
            if (s.hasPlayer(p.getName()))
                return s;
        return null;
    }

    public squad getSquad(String sname) {
        for (squad s : Squads)
            if (s.getName().equals(sname))
                return s;
        return null;
    }

    public void respawnPlayer(Player p) {
        p.setScoreboard(sb);                //custom scoreboard: apply nametag
        squad s = getSquad(p);              //check squad
        if (s == null) {
            s = new squad(p, p.getName());
            Squads.add(s);
        }
        p.getInventory().clear();           //check inv
        clearArmor(p);
        p.setCompassTarget(new Location(w, GameZone.wb.getCenter().getX(), 0, GameZone.wb.getCenter().getZ()));
        p.getInventory().addItem(new ItemStack(Material.COMPASS));
        p.setGameMode(GameMode.SURVIVAL);
        p.setFoodLevel(20);                 //respawn
        p.setHealth(20);
        s.revivePlayer(p.getName());
    }

    public void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public void PlayerInvToChest(Player p)    //todo refactor: param inventry; fix airdrop
    {
        Location tempLoc = p.getLocation();
        Block tempBlock = tempLoc.getBlock();
        tempBlock.setType(Material.CHEST);
        GameZone.trackChest(tempLoc);
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
                    GameZone.trackChest(tempLoc);
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
                if (!Votestarters.contains(p.getName()))
                {
                    Votestarters.add(p.getName());
                    if ((Votestarters.size() / Bukkit.getOnlinePlayers().size() >= CFG.getDouble("MinVotestartPercent", 0.5)) || (Votestarters.size() >= CFG.getInt("MinVotestarts", 3))) {
                        StartGameCountdown = true;
                        StartGameTimer = 61;
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
                ROYALE.reloadConfig();
                CFG = ROYALE.getConfig();
                configFixMissing();
                sender.sendMessage("Config reloaded. ");
                return true;
            }
            if (args[0].equals("save")) {
                ROYALE.saveConfig();
                sender.sendMessage("Config saved. ");
                return true;
            }
            if (args[0].equals("defaults")) {
                ROYALE.getConfig().options().copyDefaults(true);
                ROYALE.saveConfig();
                ROYALE.reloadConfig();
                configFixMissing();
                CFG = ROYALE.getConfig();
                sender.sendMessage("Config rewrited to default. ");
                //todo test: rly rewrites?
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
        if (CFG.getDouble("EndZoneSpeed", 0.5D)<=0) {
            CFG.set("EndZoneSpeed", 0.5D);
            LOG.info("[ROYALE] Fixed end zone speed. New speed = 0.5 blocks per second");
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
        if (CFG.getConfigurationSection("MonsterSpawns").getKeys(false).size()==0) {
            LOG.info("[Royale] MonsterSpawns list is empty?");
            HashMap<String, Double> hm = new HashMap<>();
            hm.put(EntityType.ZOMBIE.toString(), 0.95);
            CFG.createSection("MonsterSpawns", hm);
        }
        if (CFG.getInt("MinVotestarts", 3)<1) {
            CFG.set("MinVotestarts", 3);
            LOG.info("[ROYALE] Fixed minimal number of players are voted to start to proceed. Game starts after 3 votes");
        }
        if (CFG.getDouble("MinVotestartPercent", 0.5)>1) {
            CFG.set("MinVotestartPercent", 0.5);
            LOG.info("[ROYALE] Fixed minimal percent of voted players to game start.");
        }
        CFG.getBoolean("PostGameCommandEnable", true);
        LOG.info("[ROYALE] Should the server automatically execute command after game? "+ Boolean.toString(CFG.getBoolean("PostGameCommandEnable")));
        if (CFG.getInt("PostGameCommandTime", 60)<1) {
            CFG.set("PostGameCommandTime", 60);
            LOG.info("[ROYALE] Fixed time to execute command after game end. New time is 60 seconds.");
        }
        if (CFG.getString("PostGameCommand", "stop").equals("")) {
            CFG.set("PostGameCommand", "stop");
            LOG.info("[ROYALE] Fixed executing command after game end: /stop");
        }
        if (CFG.getInt("MaxSquadMembers", 4)<1) {
            CFG.set("MaxSquadMembers", 4);
            LOG.info("[ROYALE] Fixed max amount of players that may be in squad. Max squad members is 4 players.");
        }
        CFG.getBoolean("NametagVisiblity", false);
        LOG.info("[ROYALE] Is player's nametag visible for all players? "+ Boolean.toString(CFG.getBoolean("NametagVisiblity")));
        CFG.getBoolean("FriendlyFire", false);
        LOG.info("[ROYALE] Can teammates hit each other? "+ Boolean.toString(CFG.getBoolean("FriendlyFire")));
        CFG.getBoolean("EnableSquadBalancing", true);
        LOG.info("[ROYALE] Should game to make squad from solo-players? "+ Boolean.toString(CFG.getBoolean("EnableSquadBalancing")));
        CFG.getBoolean("EnableChestTracking", true);
        LOG.info("[ROYALE] Restore all created chest after game? "+ Boolean.toString(CFG.getBoolean("EnableChestTracking")));
        if (CFG.getString("RestoreChestBlock", "MOSSY_COBBLESTONE").equals("")) {
            CFG.set("RestoreChestBlock", "MOSSY_COBBLESTONE");
            LOG.info("[ROYALE] Fixed chest restore block. All created chest will be replaced with mossy cobblestone");
        }
        CFG.getBoolean("AirdropEnable", true);
        LOG.info("[ROYALE] Is Airdrop enabled? "+ Boolean.toString(CFG.getBoolean("AirdropEnable")));
        CFG.getBoolean("AirdropAlert", true);
        LOG.info("[ROYALE] Should plugin to broadcast airdrop location? "+ Boolean.toString(CFG.getBoolean("AirdropAlert")));
        if (CFG.getInt("FirstAirdropTime", 250)<1) {
            CFG.set("FirstAirdropTime", 250);
            LOG.info("[ROYALE] Fixed first airdrop occur time. It will drop in 250 seconds from start");
        }
        if (CFG.getInt("AirdropMinPause", 100)<1) {
            CFG.set("AirdropMinPause", 100);
            LOG.info("[ROYALE] Fixed minimum time between airdrops. New min = 100 seconds");
        }
        if (CFG.getInt("AirdropMaxPause", 200)<1) {
            CFG.set("AirdropMaxPause", 200);
            LOG.info("[ROYALE] Fixed maximum time between airdrops. New max = 200 seconds");
        }
        if (CFG.getInt("AirdropMinZoneSize", 300)<1) {
            CFG.set("AirdropeMinZoneSize", 300);
            LOG.info("[ROYALE] Airdrop will be disabled when game zone reaches 300 blocks");
        }
        if (CFG.getConfigurationSection("AirdropItems").getKeys(false).size()==0) {
            LOG.info("[Royale] AirdropItems list is empty?");
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put(Material.AIR.toString(), 1000);
            CFG.createSection("AirdropItems", hm);
        }
        if (CFG.getDouble("EnchantedItems", 0.1)>1) {
            CFG.set("EnchantedItems", 0.1);
            LOG.info("[ROYALE] Fixed percent chance of item enchanting in airdrop. New chance = 0.1");
        }
        if (CFG.getConfigurationSection("Enchantments").getKeys(false).size()==0) {
            LOG.info("[Royale] Enchantments list is empty?");
            CFG.createSection("Enchantments");
            ConfigurationSection cs = CFG.getConfigurationSection("Enchantments");
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put(Enchantment.ARROW_DAMAGE.toString(), 10);
            cs.createSection(Material.BOW.toString(), hm);
        }
        if (CFG.getConfigurationSection("Potions").getKeys(false).size()==0) {
            LOG.info("[Royale] Potions list is empty?");
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put(PotionType.INSTANT_DAMAGE.toString(), 5);
            CFG.createSection("Potions", hm);
        }
        if (CFG.getConfigurationSection("StackableItems").getKeys(false).size()==0) {
            LOG.info("[Royale] StackableItems list is empty?");
            HashMap<String, Integer> hm = new HashMap<>();
            hm.put(Material.ARROW.toString(), 20);
            CFG.createSection("StackableItems", hm);
        }

        //fix static variables
        squad.MaxMembers = CFG.getInt("MaxSquadMembers", 4);
        if (CFG.getBoolean("NametagVisiblity", false))
            tmo = Team.OptionStatus.ALWAYS;
        else
            tmo = Team.OptionStatus.FOR_OWN_TEAM;
        ff = CFG.getBoolean("FriendlyFire", false);

        //    - генерация структур вкл / выкл
        //  - всякие параметры плотности и спавна сундуков с лутом
    }
}