package ru.sooslick.royale.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Team;

public class LobbyConfig {
    public static int lobbyMinVotestarters = 3;
    public static double lobbyMinVotestartersPercent = 0.51D;
    public static boolean lobbyPostGameCommandEnabled = false;
    public static String lobbyPostGameCommand = "say post-game event triggered";
    public static int lobbyPostGameCommandDelay = 60;
    public static int squadMaxMembers = 4;
    public static Team.OptionStatus squadNametagVisiblity = Team.OptionStatus.FOR_OWN_TEAM;
    public static boolean squadFriendlyFireEnabled = true;
    public static boolean squadAutoBalancingEnabled = false;

    public static void readConfig(FileConfiguration cfg) {
        // general
        lobbyMinVotestarters = cfg.getInt("lobbyMinVotestarters", 3);
        lobbyMinVotestartersPercent = cfg.getDouble("lobbyMinVotestartersPercent", 0.51D);
        lobbyPostGameCommandEnabled = cfg.getBoolean("lobbyPostGameCommandEnabled", false);
        squadMaxMembers = cfg.getInt("squadMaxMembers", 4);
        squadFriendlyFireEnabled = cfg.getBoolean("squadFriendlyFireEnabled", true);
        squadAutoBalancingEnabled = cfg.getBoolean("squadAutoBalancingEnabled", false);

        // post-game
        if (lobbyPostGameCommandEnabled) {
            lobbyPostGameCommand = cfg.getString("lobbyPostGameCommand", "say post-game event triggered");
            lobbyPostGameCommandDelay = cfg.getInt("lobbyPostGameCommandDelay", 60);
        }

        // nametag visiblity
        try {
            squadNametagVisiblity = Team.OptionStatus.valueOf(cfg.getString("squadNametagVisiblity", "FOR_OWN_TEAM"));
        } catch (IllegalArgumentException e) {
            //todo log
            squadNametagVisiblity = Team.OptionStatus.FOR_OWN_TEAM;
        }

        //validate
        if (squadMaxMembers < 1) squadMaxMembers = 1;
    }

    private LobbyConfig() {}
}
