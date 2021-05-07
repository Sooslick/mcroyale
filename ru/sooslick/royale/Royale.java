package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;
import ru.sooslick.royale.config.RoyaleConfig;
import ru.sooslick.royale.config.ZoneConfig;

public class Royale extends JavaPlugin {
    public static Royale R;

    private RoyaleSquadList squads;     //todo royalePlayerList - unused?
    private GameState gameState;

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
        //getCommand("royale").setExecutor(cp);
        getCommand("squad").setExecutor(new SquadCommandListener());
        getCommand("teamsay").setExecutor(new TeamSayCommandListener());
        //getCommand("zone").setExecutor(cp);
        //getCommand("votestart").setExecutor(cp);
    }

    @Override
    public void onDisable() {
        //todo ??? cleanup?
    }

    private void changeGameState(GameState newState) {
        gameState = newState;
        switch (gameState) {
            case LOBBY:
                RoyaleConfig.readConfig();
                squads = new RoyaleSquadList();

                //restore world border
                WorldBorder wb = Bukkit.getWorlds().get(0).getWorldBorder();
                wb.setCenter(0, 0);
                wb.setSize(ZoneConfig.zonePreStartSize);
                return;
            case GAME:
                return;
            case POSTGAME:
        }
    }
}
