package ru.sooslick.royale;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class squad {

    private String name;
    private String leader;
    private boolean open;
    private ArrayList<String> players = new ArrayList<>();
    private ArrayList<String> alives = new ArrayList<>();
    private ItemStack MapItem;
    public Team tm;
    static int MaxMembers = 4;
    static Scoreboard sb;                                       //todo: remove "player add to custom sb" from royale's startgame to squad

    public void Reset() {           //is it used somewhere else?
        players.clear();
        alives.clear();
    }

    public squad(Player p, String s) {
        leader = p.getName();
        name = s;
        open = true;
        tm = sb.registerNewTeam(name);
        Reset();
        addPlayer(leader);
    }

    public void setName(String param) {
        name = param;
        tm.setDisplayName(name);        //todo: check real name. Can game register squad w/ old names?
    }

    public String getName() {
        return name;
    }

    public void setLeader(String param) {
        leader = param;
    }       //unused

    public String getLeader() {
        return leader;
    }

    public void setOpen(boolean op) {
        open = op;
    }

    public boolean getOpen() {
        return open;
    }

    public void addPlayer(String pname) {
        players.add(pname);
        tm.addEntry(pname);                 //todo friendlyfire bug + nametags?
    }

    public void joinGame() {
        alives.clear();
        for (String s : players) alives.add(s);
        //todo respawn players
    }

    public void revivePlayer(String pname) {
        if (!alives.contains(pname)) alives.add(pname);
    }

    public void killPlayer(String pname) {
        if (alives.contains(pname)) alives.remove(pname);
    }

    public void kickPlayer(String pname) {
        if (!leader.equals(pname)) {
            players.remove(pname);
            alives.remove(pname);
            tm.removeEntry(pname);
        }
    }

    public boolean hasPlayer(String pname) {
        return players.contains(pname);
    }

    public boolean hasAlive(String pname) {
        return alives.contains(pname);
    }

    public boolean haveAlive() {
        return alives.size() > 0;
    }

    public boolean isFull() {
        return players.size() >= MaxMembers;
    }

    public int getAliveCount() {
        return alives.size();
    }

    public int getPlayersCount() {
        return players.size();
    }

    public ArrayList<String> getAlives() {
        return alives;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public ItemStack getMap() {return MapItem;}

    public void setMap(ItemStack is) {MapItem = is;}
}