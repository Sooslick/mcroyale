package ru.sooslick.royale;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static ru.sooslick.royale.RoyaleUtil.*;

public class Royale extends JavaPlugin {

    public static Royale R;
    public static RoyaleConfig CFG;
    public static RoyalePlayerList players;
    public static RoyaleSquadList squads;

    public static GameState gameState;

    @Override
    public void onEnable() {
        R = this;                                                       //todo: replace all Royale parameters to static import
        LOG = getServer().getLogger();
        logInfo(RoyaleMessages.prefix + RoyaleMessages.onEnable);
        RoyaleConfig.setLogger(LOG);                                    //todo: import static util

        //init config folder
        boolean dataFolderExists = false;
        try {
            if (!getDataFolder().exists()) {
                dataFolderExists = getDataFolder().mkdir();
                logInfo(RoyaleMessages.createDataFolder);
            }
        } catch (Exception ex) {
            logWarning(RoyaleMessages.dataFolderException);
        }
        RoyaleConfig.setConfigFile(getDataFolder().toString() + File.separator + "plugin.yml");

        //init config file
        CFG = new RoyaleConfig();
        try {
            if (!dataFolderExists) {
                throw new Exception();          //todo replace this
            }
            if (new File(getDataFolder().toString() + File.separator + "plugin.yml" ).exists()) {
                CFG.readConfig(getConfig());
                logInfo(RoyaleMessages.readConfig);
            }
            else {
                this.saveDefaultConfig();
                CFG.setDefaults();
                logInfo(RoyaleMessages.createConfig);
            }
        } catch (Exception ex) {
            CFG.setDefaults();
            logWarning(RoyaleMessages.createConfigException);
        }

        //init player holders
        players = new RoyalePlayerList();
        squads = new RoyaleSquadList();
        gameState = GameState.LOBBY;

        //register events
        getServer().getPluginManager().registerEvents(new EventProcessor(this), this);
        CommandProcessor cp = new CommandProcessor();
        getCommand("royale").setExecutor(cp);
        getCommand("squad").setExecutor(cp);
        getCommand("zone").setExecutor(cp);
        getCommand("votestart").setExecutor(cp);
    }

    @Override
    public void onDisable() {
        CFG.saveConfig(getConfig());
    }

    //todo: test, test and test all config manipulations. I still don't understand, HOW this works!

}
