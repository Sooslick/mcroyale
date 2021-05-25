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

    private final RoyalePlayer leader;
    private final List<RoyalePlayer> playerList;
    private final Team team;

    private String name;
    private ItemStack teamMap;

    //todo refactor RoyalePlayers args
    public RoyaleSquad(String name, RoyalePlayer leader) {
        this.name = name;
        this.leader = leader;
        leader.setSquad(this);
        playerList = new LinkedList<>();
        playerList.add(leader);
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
            RoyalePlayer rp = RoyalePlayerList.get(p);
            playerList.add(rp);
            rp.setSquad(this);
        }
    }

    //todo rmReason
    public boolean rmPlayer(RoyalePlayer p) {
        if (p == null || leader.equals(p))
            return false;
        playerList.remove(p);
        p.setSquad(null);
        p.getPlayer().sendMessage(RoyaleMessages.SQUAD_KICKED_NOTIFICATION);
        return true;
    }

    public RoyalePlayer getLeader() {
        return leader;
    }

    public Boolean getSquadParam(String param) {
        return leader.getSquadParam(param);
    }

    public int getPlayersCount() {
        return playerList.size();
    }

    public boolean hasAlivePlayers() {
        return getAlivesCount() > 0;
    }

    public long getAlivesCount() {
        return playerList.stream()
                .filter(RoyalePlayer::isAlive)
                .count();
    }

    public boolean hasSlot() {
        return getPlayersCount() < LobbyConfig.squadMaxMembers;
    }

    public boolean setRestriction(String param, Boolean b) {
        return leader.setSquadParam(param, b);
    }

    public void sendMessage(String tag, String msg) {
        String tagline = tag == null ? "" : tag;
        for (RoyalePlayer rp : playerList) {
            rp.getPlayer().sendMessage(tagline + msg);
        }
    }
}
