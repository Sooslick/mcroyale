package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.sooslick.royale.commandListener.RoyaleCommandListener;
import ru.sooslick.royale.commandListener.SquadCommandListener;
import ru.sooslick.royale.commandListener.TeamSayCommandListener;
import ru.sooslick.royale.commandListener.VotestartCommandListener;
import ru.sooslick.royale.config.LobbyConfig;
import ru.sooslick.royale.config.RoyaleConfig;
import ru.sooslick.royale.config.ZoneConfig;

import java.util.HashSet;
import java.util.Set;

public class Royale extends JavaPlugin {
    public static Royale R;

    private RoyaleSquadList squads;     //todo unused?
    private GameState gameState;
    private Set<RoyalePlayer> votestarters;

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
                //todo announce + launch start timer
            } else {
                Bukkit.broadcastMessage(String.format(RoyaleMessages.VOTESTARTERS_COUNT, votes, LobbyConfig.lobbyMinVotestarters));
            }
        } else {
            p.sendMessage(RoyaleMessages.VOTESTART_TWICE);
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

        //register events
        //todo: separate lobby & game listeners
        //todo: separate command listeners
        //getServer().getPluginManager().registerEvents(new EventProcessor(this), this);
        getCommand("royale").setExecutor(new RoyaleCommandListener());
        getCommand("squad").setExecutor(new SquadCommandListener());
        getCommand("teamsay").setExecutor(new TeamSayCommandListener());
        //getCommand("zone").setExecutor(cp);
        getCommand("votestart").setExecutor(new VotestartCommandListener());
    }

    @Override
    public void onDisable() {
        //todo ??? cleanup?
    }

    private void changeGameState(GameState newState) {
        gameState = newState;
        switch (gameState) {
            case LOBBY:
                reload();
                squads = new RoyaleSquadList();
                votestarters = new HashSet<>();
                return;
            case GAME:
                return;
            case POSTGAME:
        }
    }
}
