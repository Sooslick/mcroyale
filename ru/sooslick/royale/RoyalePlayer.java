package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RoyalePlayer {

    //general royale fields
    private Player player;
    private String name;
    private RoyaleSquad squad; //todo + invite

    //current game fields
    private boolean alive;
    private int alertTimer;

    //server stats fields
    private int gamesTotal;
    private int gamesWon;

    public RoyalePlayer(Player p) {
        player = p;
        name = p.getName();
        squad = null;
        alive = false;
        alertTimer = 0;
        //todo get stats from yml-file - and refactor it!
    }

    public RoyalePlayer(String name) {
        this.name = name;
        player = Bukkit.getPlayer(name);
        squad = null;
        alive = false;
        alertTimer = 0;
        //todo ref.
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
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
}
