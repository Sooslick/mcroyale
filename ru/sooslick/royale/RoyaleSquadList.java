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
            alives+= s.getAlivesCount();
        }
        return alives;
    }

    public int getSquadsCount() {
        return squads.size();
    }

    public int getPlayersCount() {
        int p = 0;
        for (RoyaleSquad s : squads) {
            p+= s.getPlayersCount();
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
        //todo check and prevent duplicates! req get invite by pl and sq method
        invites.add(new SquadInvite(p, s));
    }

    //todo: invite accept

    //todo: invite die

    //todo: invite ticker

    //todo: getInvite byPlayer

    //todo: getInvite byPlayer and Squad

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

}
