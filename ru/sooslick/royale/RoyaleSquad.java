package ru.sooslick.royale;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RoyaleSquad {
    static int maxMembers;
    static Scoreboard sb;   //todo move to SquadList or Royale, static import

    private String name;
    private RoyalePlayer leader;
    private List<RoyalePlayer> playerList;
    private ItemStack teamMap;
    private boolean allowRequest;
    private boolean allowAutobalance;
    private Team team;

    //todo refactor RoyalePlayers args
    public RoyaleSquad(String name, RoyalePlayer leader) {
        this.name = name;
        this.leader = leader;
        playerList = new LinkedList<>();
        playerList.add(leader);
        //todo copy leader settings
        allowRequest = true;
        allowAutobalance = true;
        //todo create scoreboard
        team = sb.registerNewTeam(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        team.setDisplayName(name);
    }

    public List<RoyalePlayer> getPlayers() {
        return playerList;
    }

    public String formatPlayerList() {
        return playerList.stream().map(RoyalePlayer::getName).collect(Collectors.joining());
    }

    public boolean hasPlayer(Player p) {
        return playerList.stream().anyMatch(rpl -> rpl.getName().equals(p.getName()));
    }

    public void addPlayer(Player p) {
        if ((getPlayersCount() < maxMembers) && !hasPlayer(p)) {
            playerList.add(new RoyalePlayer(p));
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

    public void setRestriction(String param, boolean b) {
        switch (param) {
            case "request":
                allowRequest = b;
                break;
            case "balance":
                allowAutobalance = b;
                break;
            default:
                //todo say gav tяф
        }
    }

    public void sendMessage(RoyalePlayer p, String msg) {
        StringBuilder str = new StringBuilder().append("§7§i[Team] ").append(p.getName()).append(": ").append(msg);
        for (RoyalePlayer rp : playerList) {
            rp.getPlayer().sendMessage(str.toString());
        }
    }

    //todo: messages
}
