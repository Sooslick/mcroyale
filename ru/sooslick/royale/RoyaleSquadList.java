package ru.sooslick.royale;

import java.util.ArrayList;

public class RoyaleSquadList {

    //todo: static?
    private ArrayList<RoyaleSquad> squads;
    private ArrayList<SquadInvite> invites;

    public RoyaleSquadList() {
        squads = new ArrayList<>();
        invites = new ArrayList<>();
    }

    public int getAliveTeams() {
        int alives = 0;
        for (RoyaleSquad s : squads) {
            if (s.getAlivesCount() > 0) {
                alives++;
            }
        }
        return alives;
    }

    public int getAlivePlayers() {
        int alives = 0;
        for (RoyaleSquad s : squads) {
            alives += s.getAlivesCount();
        }
        return alives;
    }

    public int getSquadsCount() {
        return squads.size();
    }

    public int getPlayersCount() {
        int p = 0;
        for (RoyaleSquad s : squads) {
            p += s.getPlayersCount();
        }
        return p;
    }

    public void addSquad(RoyaleSquad squad) {
        squads.add(squad);
    }

    public void rmSquad(RoyaleSquad squad) {
        squads.remove(squad);
    }

    public void clear() {
        squads.clear();
    }

    public void invitePlayer(RoyaleSquad s, RoyalePlayer p) {
        if (getInvite(p,s) != null) {
            //todo error message
            return;
        }
        invites.add(new SquadInvite(p, s));
    }

    public void inviteAccept(SquadInvite inv) {
        inv.getSquad().addPlayer(inv.getPlayer());
        inviteTimeout(inv);
        //todo messages
    }

    public void inviteTimeout(SquadInvite inv) {
        invites.remove(inv);
        //todo messages to player
        //todo messages to console
    }

    public void inviteTick() {
        for (SquadInvite i : invites) {
            i.tick();
            if (i.getLifetime() <= 0) {
                inviteTimeout(i);
            }
        }
    }

    public SquadInvite getInviteByPlayer(RoyalePlayer p) throws SquadInviteException {
        ArrayList<SquadInvite> invs = getInvitesByPlayer(p);
        switch (invs.size()) {
            case 0:
                return null;
            case 1:
                return invs.get(0);
            default:
                throw new SquadInviteException(RoyaleMessages.squadMultipleInvites);
                //todo: exception: StringFormat - squadlist
        }
    }

    public SquadInvite getInvite(RoyalePlayer p, RoyaleSquad s) {
        for (SquadInvite i : invites) {
            if (i.getPlayer().equals(p) && i.getSquad().equals(s)) {
                return i;
            }
        }
        return null;
    }

    public ArrayList<SquadInvite> getInvites() {
        return invites;
    }

    public ArrayList<SquadInvite> getInvitesByPlayer(RoyalePlayer p) {
        ArrayList<SquadInvite> invs = new ArrayList<>();
        for (SquadInvite i : invites) {
            if (i.getPlayer().equals(p)) {
                invs.add(i);
            }
        }
        return invs;
    }

    public void clearInvites() {
        invites.clear();
    }

    //todo: request feature

}
