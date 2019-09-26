package ru.sooslick.royale;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static ru.sooslick.royale.RoyaleUtil.logInfo;
import static ru.sooslick.royale.RoyaleUtil.logWarning;

public class Royale extends JavaPlugin {

    public static Royale R;
    public static RoyaleConfig CFG;
    public static RoyalePlayerList players;
    public static RoyaleSquadList squads;

    public static GameState gameState;

    @Override
    public void onEnable() {
        R = this;
        RoyaleUtil.LOG = getServer().getLogger();
        logInfo(RoyaleMessages.prefix + RoyaleMessages.onEnable);

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
            if (dataFolderExists) {
                if (new File(getDataFolder().toString() + File.separator + "plugin.yml").exists()) {
                    CFG.readConfig(getConfig());
                    logInfo(RoyaleMessages.readConfig);
                } else {
                    saveDefaultConfig();
                    CFG.setDefaults();
                    logInfo(RoyaleMessages.createConfig);
                }
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
        getServer().getPluginManager().registerEvents(new EventProcessor(), this);
        CommandProcessor cp = new CommandProcessor();
        getCommand("royale").setExecutor(cp);
        getCommand("squad").setExecutor(cp);
        getCommand("zone").setExecutor(cp);
        getCommand("votestart").setExecutor(cp);
    }

    @Override
    public void onDisable() {
        CFG.saveConfig(getConfig());
        //todo: print config file to console if cannot save
    }

    //todo: test, test and test all config manipulations. I still don't understand, HOW this works!

}
