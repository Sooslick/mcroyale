package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static ru.sooslick.royale.Royale.R;

public class RoyalePlayer {

    private static final String extension = ".yml";

    //general royale fields
    private Player player;
    private String name;
    private RoyaleSquad squad;

    //current game fields
    private boolean alive;
    private int alertTimer;
    private Location savedPosition;

    //server stats fields
    private int gamesTotal;
    private int gamesWon;
    private int kills;
    private int deadbyPlayer;
    private int deadbyMob;
    private int deadbyEnv;

    public RoyalePlayer(Player p) {
        player = p;
        name = p.getName();
        squad = null;
        alive = false;
        alertTimer = 0;
        readStat();
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
        saveStat();
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

    public void readStat() {
        try {
            YamlConfiguration f = new YamlConfiguration();
            f.load(R.getDataFolder() + File.separator + name + extension);
            gamesTotal = f.getInt("gamesTotal", 0);
            gamesWon = f.getInt("gamesWon", 0);
            kills = f.getInt("kills", 0);
            deadbyPlayer = f.getInt("deadbyPlayer", 0);
            deadbyMob = f.getInt("deadbyMob", 0);
            deadbyEnv = f.getInt("deadbyEnv", 0);
            return;
        } catch (FileNotFoundException e) {
            RoyaleUtil.logInfo(RoyaleMessages.playerFirstJoin);
        } catch (IOException | InvalidConfigurationException e) {
            RoyaleUtil.logInfo(RoyaleMessages.playerInvalidStat);
        }
        //set stats with zeros if exception was caught
        resetStat();
    }

    public void saveStat() {
        YamlConfiguration f = new YamlConfiguration();
        f.set("gamesTotal", gamesTotal);
        f.set("gamesWon", gamesWon);
        f.set("kills", kills);
        f.set("deadbyPlayer", deadbyPlayer);
        f.set("deadbyMob", deadbyMob);
        f.set("deadbyEnv", deadbyEnv);
        try {
            f.save(R.getDataFolder() + File.separator + name + extension);
        } catch (IOException e) {
            RoyaleUtil.logInfo(RoyaleMessages.playerWriteStatError);
        }
    }

    private void resetStat() {
        gamesTotal = 0;
        gamesWon = 0;
        kills = 0;
        deadbyPlayer = 0;
        deadbyMob = 0;
        deadbyEnv = 0;
    }

}
