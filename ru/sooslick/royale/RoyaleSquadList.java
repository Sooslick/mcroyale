package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.sooslick.royale.config.LobbyConfig;
import ru.sooslick.royale.util.TeamColorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoyaleSquadList {
    private final static String CANNOT_GENERATE_SQUAD = "Failed to generate squad. Resulting squad is empty";

    public static RoyaleSquadList instance;

    private final ArrayList<RoyaleSquad> squads;
    private final ArrayList<SquadInvite> invites;

    private Scoreboard sb;

    public RoyaleSquadList() {
        instance = this;
        squads = new ArrayList<>();
        invites = new ArrayList<>();
    }

    public long getAliveSquadsCount() {
        return squads.stream()
                .filter(RoyaleSquad::hasAlivePlayers)
                .count();
    }

    public long getAlivePlayersCount() {
        return squads.stream()
                .mapToLong(RoyaleSquad::getAlivesCount)
                .sum();
    }

    public List<RoyaleSquad> getSquadList() {
        return squads;
    }

    public int getSquadsCount() {
        return squads.size();
    }

    public int getPlayersCount() {
        return squads.stream()
                .mapToInt(RoyaleSquad::getPlayersCount)
                .sum();
    }

    public RoyaleSquad getSquad(String name) {
        if (name == null)
            return null;
        return squads.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public RoyaleSquad getSquadByPlayer(Player p) {
        RoyalePlayer rp = RoyalePlayerList.get(p);
        return squads.stream().filter(squad -> squad.hasPlayer(rp)).findFirst().orElse(null);
    }

    public String formatSquadList() {
        return squads.stream().map(RoyaleSquad::getName).collect(Collectors.joining());
    }

    public String getDefaultSquadName() {
        return "Team" + (getSquadsCount() + 1);
    }

    public RoyaleSquad createSquad(String name, Player leader) {
        return createSquad(name, RoyalePlayerList.get(leader));
    }

    public RoyaleSquad createSquad(String name, RoyalePlayer leader) {
        if (name == null) name = getDefaultSquadName();
        RoyaleSquad squad = new RoyaleSquad(name, leader);
        squads.add(squad);
        return squad;
    }

    public void disbandSquad(RoyaleSquad squad) {
        squads.remove(squad);
    }

    public boolean invitePlayer(RoyaleSquad targetSquad, Player invitedPlayer) {
        try {
            getInvite(targetSquad, invitedPlayer);
            return false;
        } catch (SquadInviteException e) {
            return invites.add(new SquadInvite(invitedPlayer, targetSquad, SquadInvite.Type.INVITE));
        }
    }

    public boolean joinRequest(RoyaleSquad targetSquad, Player invitedPlayer) {
        try {
            getRequest(targetSquad, invitedPlayer);
            return false;
        } catch (SquadInviteException e) {
            return invites.add(new SquadInvite(invitedPlayer, targetSquad, SquadInvite.Type.REQUEST));
        }
    }

    public void inviteAccept(SquadInvite inv) {
        RoyaleSquad squad = inv.getSquad();
        Player invPlayer = inv.getPlayer();
        //todo check squad still alive
        if (squad.hasSlot()) {
            squad.addPlayer(invPlayer);
            squad.sendMessage(null, String.format(RoyaleMessages.SQUAD_JOINED, invPlayer));
        } else {
            invPlayer.sendMessage(RoyaleMessages.SQUAD_IS_FULL);
            squad.getLeader().getPlayer().sendMessage(String.format(RoyaleMessages.SQUAD_FULL_NOTIFICATION, invPlayer.getName()));
        }
        inviteDeactivate(inv);
    }

    public void inviteDeactivate(SquadInvite inv) {
        invites.remove(inv);
    }

    public SquadInvite getInviteByPlayer(Player p) throws SquadInviteException {
        List<SquadInvite> invs = getInvitesByPlayer(p);
        switch (invs.size()) {
            case 0:
                throw new SquadInviteNotFoundException();
            case 1:
                return invs.get(0);
            default:
                throw new SquadMultipleInvitesException();
        }
    }

    public List<SquadInvite> getInvitesByPlayer(Player p) {
        return invites.stream()
                .filter(i -> i.getPlayer().equals(p) && i.getType() == SquadInvite.Type.INVITE)
                .collect(Collectors.toList());
    }

    public SquadInvite getInvite(RoyaleSquad squad, Player p) throws SquadInviteNotFoundException {
        if (squad == null)
            throw new SquadInviteNotFoundException();
        return invites.stream()
                .filter(i -> i.getPlayer().equals(p) && i.getSquad().equals(squad) && i.getType() == SquadInvite.Type.INVITE)
                .findFirst()
                .orElseThrow(SquadInviteNotFoundException::new);
    }

    public SquadInvite getRequest(RoyaleSquad squad, Player p) throws SquadInviteNotFoundException {
        if (squad == null)
            throw new SquadInviteNotFoundException();
        return invites.stream()
                .filter(i -> i.getPlayer().equals(p) && i.getSquad().equals(squad) && i.getType() == SquadInvite.Type.REQUEST)
                .findFirst()
                .orElseThrow(SquadInviteNotFoundException::new);
    }

    public List<SquadInvite> getRequestsBySquad(RoyaleSquad squad) {
        return invites.stream()
                .filter(i -> i.getSquad().equals(squad) && i.getType() == SquadInvite.Type.REQUEST)
                .collect(Collectors.toList());
    }

    public SquadInvite getRequestBySquad(RoyaleSquad squad) throws SquadInviteException {
        List<SquadInvite> invs = getRequestsBySquad(squad);
        switch (invs.size()) {
            case 0:
                throw new SquadInviteNotFoundException();
            case 1:
                return invs.get(0);
            default:
                throw new SquadMultipleInvitesException();
        }
    }

    public void prepareScoreboard() {
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        for (RoyaleSquad squad : squads) {
            Team team = sb.registerNewTeam(squad.getName());
            team.setAllowFriendlyFire(LobbyConfig.squadFriendlyFireEnabled);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, LobbyConfig.squadNametagVisiblity);
            team.setColor(TeamColorUtil.getRandomColor());
            for (RoyalePlayer rp : squad.getPlayers()) {
                team.addEntry(rp.getName());
            }
            squad.setTeam(team);
        }
    }

    public void balanceTeams() {
        // looking for free players
        List<RoyalePlayer> freePlayers = RoyalePlayerList.getList()
                .stream()
                .filter(rp -> rp.getSquad() == null)
                .collect(Collectors.toList());
        Collections.shuffle(freePlayers);

        // check max squad size
        int maxMembers = LobbyConfig.squadAutoBalancingEnabled ? LobbyConfig.squadMaxMembers : 1;
        //no autobalance required if max squad size is 1
        if (maxMembers == 1) {
            //todo make solo squads
            return;
        }

        // looking for shortage squads
        List<RoyaleSquad> availableSquads = squads.stream()
                .filter(squad -> squad.getPlayers().size() < maxMembers &&
                        squad.getSquadParam(RoyalePlayer.ALLOW_BALANCE))
                .collect(Collectors.toList());

        // calc avg members in staffed squads
        double avgMembers = squads.stream()
                .filter(squad -> squad.getPlayers().size() >= maxMembers ||
                        !squad.getSquadParam(RoyalePlayer.ALLOW_BALANCE))
                .mapToInt(squad -> squad.getPlayers().size())
                .average()
                .orElse(maxMembers);

        // count players required for balancing existing squads without creating new teams
        int shortage = (int) Math.ceil(availableSquads.stream()
                .filter(squad -> squad.getPlayers().size() < avgMembers)
                .mapToDouble(squad -> avgMembers - squad.getPlayers().size())
                .sum());

        // calc amount of squads required to cover remaining players
        int newSquadsAmount = freePlayers.size() <= shortage ? 0 :
                (int) Math.ceil((freePlayers.size() - shortage) / avgMembers);

        //create squads
        double preSize = 0;
        for (int i = 0; i < newSquadsAmount; i++) {
            preSize += avgMembers;
            int size = (int) Math.floor(preSize);
            RoyaleSquad squad = createRandomSquad(freePlayers, size);
            if (squad != null) {
                List<RoyalePlayer> members = squad.getPlayers();
                if (members.size() < maxMembers) {
                    availableSquads.add(squad);
                }
                freePlayers.removeAll(members);
            }
            preSize -= size;
        }

        // join remaining players to squads
        for (RoyalePlayer rp : freePlayers) {
            RoyaleSquad squad = availableSquads.stream()
                    .min(Comparator.comparingInt(RoyaleSquad::getPlayersCount))
                    .orElse(null);
            if (squad == null) {
                Collections.shuffle(availableSquads);
                squad = availableSquads.get(0);
            }
            squad.addPlayer(rp);
        }
    }

    private RoyaleSquad createRandomSquad(List<RoyalePlayer> players, int size) {
        // invalid parameters
        if (players == null || players.size() == 0 || size <= 0) {
            RoyaleLogger.warn(CANNOT_GENERATE_SQUAD);
            return null;
        }
        // players < max squad size
        if (players.size() <= size) {
            return createSquad(players);
        }
        // players > max squad size
        List<RoyalePlayer> subPlayers = players.subList(0, size);
        return createSquad(subPlayers);
    }

    private RoyaleSquad createSquad(List<RoyalePlayer> players) {
        if (players == null || players.size() == 0) {
            RoyaleLogger.warn(CANNOT_GENERATE_SQUAD);
            return null;
        }
        RoyalePlayer leader = players.get(0);
        players.remove(0);
        RoyaleSquad squad = createSquad(null, leader);
        for (RoyalePlayer rp : players)
            squad.addPlayer(rp);
        return squad;
    }

    //todo copypaste refactoring
}
