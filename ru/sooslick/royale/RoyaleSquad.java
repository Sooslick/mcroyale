package ru.sooslick.royale;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.LinkedList;

public class RoyaleSquad {

    //static royale... todo
    static int maxMembers;
    static Scoreboard sb;

    private String name;
    private RoyalePlayer leader;
    private LinkedList<RoyalePlayer> playerList;
    private ItemStack teamMap;
    private boolean accessibility;
    private Team team;

    public RoyaleSquad(String name, RoyalePlayer leader) {
        this.name = name;
        this.leader = leader;
        playerList = new LinkedList<>();
        playerList.add(leader);
        accessibility = true;
        team = sb.registerNewTeam(name);
    }

    public void setName(String name) {
        this.name = name;
        team.setDisplayName(name);
    }

    public LinkedList<RoyalePlayer> getPlayers() {
        return playerList;
    }

    public boolean hasPlayer(RoyalePlayer p) {
        for (RoyalePlayer rp : playerList) {
            if (rp.equals(p)) {
                return true;
            }
        }
        return false;
    }

    public void addPlayer(RoyalePlayer p) {
        if ((getPlayersCount() < maxMembers) && !hasPlayer(p)) {
            playerList.add(p);
        }
    }

    public void rmPlayer(RoyalePlayer p) {
        if (!leader.equals(p)) {
            playerList.remove(p);
        }
    }

    public int getPlayersCount() {
        return playerList.size();
    }

    public int getAlivesCount() {
        int a = 0;
        for (RoyalePlayer p : playerList) {
            if (p.isAlive()) {
                a++;
            }
        }
        return a;
    }

    public void setAccessibility(boolean b) {
        accessibility = b;
    }

    public void sendMessage(RoyalePlayer p, String msg) {
        StringBuilder str = new StringBuilder().append("§7§i[Team] ").append(p.getName()).append(": ").append(msg);
        for (RoyalePlayer rp : playerList) {
            rp.getPlayer().sendMessage(str.toString());
        }
    }

    //todo: messages
}
