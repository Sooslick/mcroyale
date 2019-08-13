package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RoyalePlayer {

    //general royale fields
    private Player player;
    private String name;
    private RoyaleSquad squad; //todo + invite

    //current game fields
    private boolean alive;
    private int alertTimer;
    private Location savedPosition;

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

    public void prepare() {
        player.setExp(0);
        player.setFoodLevel(20);
        player.setHealth(20);
        clearInventory();
        player.setGameMode(GameMode.SPECTATOR);
        alive = false;
        alertTimer = 0;
    }

    public void disconnect() {
        savedPosition = player.getLocation();
        //todo if GAME state -> invtochest trigger, game events trigger
        //todo write player state to yml-file
        player = null;
    }

    public void clearInventory() {
        player.getInventory().clear();
        player.getInventory().setBoots(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setHelmet(null);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player p) {
        player = p;
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
