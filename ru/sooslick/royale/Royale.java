package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.sooslick.royale.commandListener.RoyaleCommandListener;
import ru.sooslick.royale.commandListener.SquadCommandListener;
import ru.sooslick.royale.commandListener.TeamSayCommandListener;
import ru.sooslick.royale.commandListener.VotestartCommandListener;
import ru.sooslick.royale.commandListener.ZoneCommandListener;
import ru.sooslick.royale.config.LobbyConfig;
import ru.sooslick.royale.config.RoyaleConfig;
import ru.sooslick.royale.config.ZoneConfig;
import ru.sooslick.royale.events.LobbyEvents;

import java.util.HashSet;
import java.util.Set;

public class Royale extends JavaPlugin {
    public static Royale R;

    private RoyaleSquadList squads;     //todo unused?
    private GameState gameState;
    private Set<RoyalePlayer> votestarters;
    private Listener activeEvents;

    private int prestartTaskId = 0;
    private int prestartTimer = 0;

    public static GameState getGameState() {
        return R.gameState;
    }

    public static void reload() {
        RoyaleConfig.readConfig();

        //restore world border
        WorldBorder wb = Bukkit.getWorlds().get(0).getWorldBorder();
        wb.setCenter(0, 0);
        wb.setSize(ZoneConfig.zonePreStartSize);
    }

    public static void votestart(Player p) {
        if (getGameState() != GameState.LOBBY) {
            p.sendMessage(RoyaleMessages.ROYALE_IS_RUNNING);
            return;
        }
        Set<RoyalePlayer> vs = R.votestarters;
        if (vs.add(RoyalePlayerList.get(p))) {
            Bukkit.broadcastMessage(String.format(RoyaleMessages.VOTESTART, p.getName()));
            int votes = vs.size();
            double percent = (double) votes / Bukkit.getOnlinePlayers().size();
            if ((votes >= LobbyConfig.lobbyMinVotestarters) || (percent >= LobbyConfig.lobbyMinVotestartersPercent)) {
                R.prestart(false);
            } else {
                Bukkit.broadcastMessage(String.format(RoyaleMessages.VOTESTARTERS_COUNT, votes, LobbyConfig.lobbyMinVotestarters));
            }
        } else {
            p.sendMessage(RoyaleMessages.VOTESTART_TWICE);
        }
    }

    public static void unvote(Player p) {
        Set<RoyalePlayer> vs = R.votestarters;
        if (vs.remove(RoyalePlayerList.get(p))) {
            if (R.prestartTaskId == 0)
                Bukkit.broadcastMessage(String.format(RoyaleMessages.VOTESTARTERS_COUNT, vs.size(), LobbyConfig.lobbyMinVotestarters));
        }
    }

    @Override
    public void onEnable() {
        R = this;                                                       //todo: replace all Royale parameters to static import
        RoyaleLogger.initWith(getLogger());

        //create data folder and default config if not exists
        saveDefaultConfig();

        //init lobby
        changeGameState(GameState.LOBBY);

        //register commands
        getCommand("royale").setExecutor(new RoyaleCommandListener());
        getCommand("squad").setExecutor(new SquadCommandListener());
        getCommand("teamsay").setExecutor(new TeamSayCommandListener());
        getCommand("votestart").setExecutor(new VotestartCommandListener());
        getCommand("zone").setExecutor(new ZoneCommandListener());
    }

    @Override
    public void onDisable() {
        //todo ??? cleanup?
    }

    private void changeGameState(GameState newState) {
        gameState = newState;
        switch (gameState) {
            case LOBBY:
                //init
                reload();
                squads = new RoyaleSquadList();
                votestarters = new HashSet<>();
                prestartTaskId = 0;
                prestartTimer = 0;

                //unregister previous state events and add new listener
                if (activeEvents != null)
                    HandlerList.unregisterAll(activeEvents);
                activeEvents = new LobbyEvents();
                Bukkit.getPluginManager().registerEvents(activeEvents, this);
                return;

            case GAME:
                Bukkit.getScheduler().cancelTask(prestartTaskId);
                HandlerList.unregisterAll(activeEvents);
                ChestTracker.getNewInstance();
                squads.balanceTeams();
                squads.prepareScoreboard();
                return;

            case POSTGAME:
        }
    }

    public boolean prestart(boolean force) {
        if (prestartTaskId != 0)
            return false;
        if (force)
            prestartTimer = 60;
        Bukkit.broadcastMessage(RoyaleMessages.ROYALE_PRESTART);
        prestartTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (++prestartTimer > 60)
                changeGameState(GameState.GAME);
            else if (prestartTimer % 10 == 0)
                Bukkit.broadcastMessage(String.format(RoyaleMessages.ROYALE_PRESTART_TIMER, 60 - prestartTimer));
        }, 1, 20);
        return true;
    }
}
