package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.sooslick.royale.config.LobbyConfig;
import ru.sooslick.royale.util.TeamColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoyaleSquadList {
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
        return squads.stream().filter(squad -> squad.hasPlayer(p)).findFirst().orElse(null);
    }

    public String formatSquadList() {
        return squads.stream().map(RoyaleSquad::getName).collect(Collectors.joining());
    }

    public String getDefaultSquadName() {
        return "Team" + (getSquadsCount() + 1);
    }

    public void createSquad(String name, Player leader) {
        squads.add(new RoyaleSquad(name, RoyalePlayerList.get(leader)));
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

    //todo copypaste refactoring
}
