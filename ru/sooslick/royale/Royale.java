package ru.sooslick.royale;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Royale extends JavaPlugin {

    public static Royale R;
    public static Logger LOG;
    public static RoyaleConfig CFG;
    public static RoyalePlayerList players;
    public static RoyaleSquadList squads;

    public static GameState gameState;

    //private boolean datafolderexists? todo

    @Override
    public void onEnable() {
        R = this;                                                       //todo: replace all Royale parameters to static import
        LOG = getServer().getLogger();
        LOG.info(RoyaleMessages.prefix + RoyaleMessages.onEnable);      //todo: logger util to prevent concats at every string
        RoyaleConfig.setLogger(LOG);

        //init config folder
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();        //todo: If mkdir returns success or fail replace trycatch
                LOG.info(RoyaleMessages.prefix + RoyaleMessages.createDataFolder);
            }
        } catch (Exception ex) {
            LOG.warning(RoyaleMessages.prefix + RoyaleMessages.dataFolderException);
            LOG.warning(ex.getMessage());
        }
        RoyaleConfig.setConfigFile(getDataFolder().toString() + File.separator + "plugin.yml");

        //init config file
        //todo: if datafolder exists - prevent exception
        CFG = new RoyaleConfig();
        try {
            if (new File(getDataFolder().toString() + File.separator + "plugin.yml" ).exists()) {
                CFG.readConfig(getConfig());
                LOG.info(RoyaleMessages.prefix + RoyaleMessages.readConfig);
            }
            else {
                this.saveDefaultConfig();
                CFG.setDefaults();
                LOG.info(RoyaleMessages.prefix + RoyaleMessages.createConfig);
            }
        } catch (Exception ex) {
            CFG.setDefaults();
            LOG.warning(RoyaleMessages.prefix + RoyaleMessages.createConfigException);
            LOG.warning("[Royale] " + ex);
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
