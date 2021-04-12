package ru.sooslick.royale;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.sooslick.royale.config.LobbyConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RoyaleSquad {
    private static final String TAG_TEMPLATE = "§7§i[Team] %s: ";

    static int maxMembers;
    static Scoreboard sb;   //todo move to SquadList or Royale, static import

    boolean allowRequest;
    boolean allowInvite;
    boolean allowAutobalance;

    private String name;
    private RoyalePlayer leader;
    private List<RoyalePlayer> playerList;
    private ItemStack teamMap;
    private Team team;

    //todo refactor RoyalePlayers args
    public RoyaleSquad(String name, RoyalePlayer leader) {
        this.name = name;
        this.leader = leader;
        playerList = new LinkedList<>();
        playerList.add(leader);
        //todo copy leader settings
        allowRequest = true;
        allowInvite = true;
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

    public boolean rmPlayer(RoyalePlayer p) {
        if (leader.equals(p))
            return false;
        playerList.remove(p);
        p.getPlayer().sendMessage(RoyaleMessages.SQUAD_KICKED_NOTIFICATION);
        return true;
    }

    public RoyalePlayer getLeader() {
        return leader;
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

    public boolean hasSlot() {
        return getPlayersCount() < LobbyConfig.squadMaxMembers;
    }

    public void setRestriction(String param, boolean b) {
        switch (param) {
            case "request":
                allowRequest = b;
                return;
            case "invite":
                allowInvite = b;
                return;
            case "balance":
                allowAutobalance = b;
                return;
            default:
                //todo say gav tяф
        }
    }

    public void sendMessage(RoyalePlayer p, String msg) {
        String tagline = p == null ? "" : String.format(TAG_TEMPLATE, p.getName());
        for (RoyalePlayer rp : playerList) {
            rp.getPlayer().sendMessage(tagline + msg);
        }
    }

    //todo: messages
}
