package ru.sooslick.royale;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Royale extends JavaPlugin {

    public static Logger LOG;
    //public static EventHandler eventHandler;
    public static RoyalePlayerList players;
    public static RoyaleSquadList squads;

    public static GameState gameState;

    //private boolean datafolderexists? todo

    @Override
    public void onEnable() {
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
        try {
            if (new File(getDataFolder().toString() + File.separator + "plugin.yml" ).exists()) {
                RoyaleConfig.readConfig(this.getConfig());
            }
            else {
                this.saveDefaultConfig();
                RoyaleConfig.setDefaults();
                LOG.info(RoyaleMessages.prefix + RoyaleMessages.createConfig);
            }
        } catch (Exception ex) {
            RoyaleConfig.setDefaults();
            LOG.warning(RoyaleMessages.prefix + RoyaleMessages.createConfigException);
            LOG.warning("[Royale] " + ex);
        }

        //cfg blah blah blah

        //init player holders
        players = new RoyalePlayerList();
        squads = new RoyaleSquadList();
        gameState = GameState.LOBBY;

        //init EventHandler: register events
        getServer().getPluginManager().registerEvents(new EventProcessor(this), this);
    }

}
