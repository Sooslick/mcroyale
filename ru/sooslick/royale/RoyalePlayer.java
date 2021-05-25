package ru.sooslick.royale;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RoyalePlayer {
    public static final String ALLOW_REQUEST = "request";
    public static final String ALLOW_INVITE = "invite";
    public static final String ALLOW_BALANCE = "balance";
    public static final String USE_COMPASS = "compass";
    public static final String USE_MAP = "map";

    //general royale fields
    private Player player;
    private RoyaleSquad squad;

    //current game fields
    private boolean alive;
    private int alertTimer;

    //server stats fields
    //todo: games, wins, kills, deaths (player, mob, environment)

    private Map<String, Boolean> squadParams = new HashMap<>();

    public RoyalePlayer(Player p) {
        player = p;
        squad = null;
        alive = false;
        alertTimer = 0;
        //todo get default squad settings from profile
        squadParams.put(ALLOW_BALANCE, Boolean.TRUE);
        squadParams.put(ALLOW_REQUEST, Boolean.TRUE);
        squadParams.put(ALLOW_INVITE, Boolean.TRUE);
        squadParams.put(USE_COMPASS, Boolean.TRUE);
        squadParams.put(USE_MAP, Boolean.TRUE);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player p) {
        player = p;
    }

    public String getName() {
        return player.getName();
    }

    public RoyaleSquad getSquad() {
        return squad;
    }

    public void setSquad(RoyaleSquad s) {
        squad = s;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean a) {
        alive = a;
    }

    public int getAlertTimer() {
        return alertTimer;
    }

    public void setAlertTimer(int t) {
        alertTimer = t;
    }

    public Boolean getSquadParam(String param) {
        return squadParams.get(param);
    }

    public boolean setSquadParam(String param, Boolean value) {
        if (squadParams.get(param) != null) {
            squadParams.put(param, value);
            return true;
        }
        return false;
    }
}
