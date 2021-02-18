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

    public boolean invitePlayer(RoyaleSquad s, Player p) {
        try {
            getInvite(s, p);
            return false;
        } catch (SquadInviteException e) {
            return invites.add(new SquadInvite(p, s));
        }
    }

    public void inviteAccept(SquadInvite inv) {
        inv.getSquad().addPlayer(inv.getPlayer());
        inviteDeactivate(inv);
        //todo messages
    }

    public void inviteDecline(SquadInvite inv) {
        //todo messages
        inviteDeactivate(inv);
    }

    public void inviteDeactivate(SquadInvite inv) {
        invites.remove(inv);
    }

    public void inviteTimeout(SquadInvite inv) {
        inviteDeactivate(inv);
        //todo messages to player //invite accept used this method
        //todo messages to console
    }

    //todo stream
    public void inviteTick() {
        for (SquadInvite i : invites) {
            i.tick();
            if (i.getLifetime() <= 0) {
                inviteTimeout(i);
            }
        }
    }

    public SquadInvite getInviteByPlayer(Player p) throws SquadInviteException {
        List<SquadInvite> invs = invites.stream().filter(i -> i.getPlayer().equals(p)).collect(Collectors.toList());
        switch (invs.size()) {
            case 0:
                throw new SquadInviteException(RoyaleMessages.SQUAD_INVITE_NOT_FOUND);
            case 1:
                return invs.get(0);
            default:
                throw new SquadInviteException(RoyaleMessages.SQUAD_MULTIPLE_INVITES);
        }
    }

    public SquadInvite getInvite(RoyaleSquad squad, Player p) throws SquadInviteException {
        if (squad == null)
            throw new SquadInviteException(RoyaleMessages.SQUAD_NOT_FOUND);
        return invites.stream().filter(i -> i.getPlayer().equals(p) && i.getSquad().equals(squad)).findFirst()
                .orElseThrow(() -> new SquadInviteException(RoyaleMessages.SQUAD_INVITE_NOT_FOUND));
    }

    public ArrayList<SquadInvite> getInvites() {
        return invites;
    }

    //todo: request feature

}
