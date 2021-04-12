package ru.sooslick.royale;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoyaleSquadList {
    public static RoyaleSquadList instance;

    private final ArrayList<RoyaleSquad> squads;
    private final ArrayList<SquadInvite> invites;

    public RoyaleSquadList() {
        instance = this;
        squads = new ArrayList<>();
        invites = new ArrayList<>();
    }

    //todo stream
    public int getAliveSquadsCount() {
        int alives = 0;
        for (RoyaleSquad s : squads) {
            if (s.getAlivesCount() > 0) {
                alives++;
            }
        }
        return alives;
    }

    //todo stream
    public int getAlivePlayersCount() {
        int alives = 0;
        for (RoyaleSquad s : squads) {
            alives += s.getAlivesCount();
        }
        return alives;
    }

    public int getSquadsCount() {
        return squads.size();
    }

    //todo stream
    public int getPlayersCount() {
        int p = 0;
        for (RoyaleSquad s : squads) {
            p += s.getPlayersCount();
        }
        return p;
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
        squads.add(new RoyaleSquad(name, new RoyalePlayer(leader)));
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

    //todo copypaste refactoring
}
