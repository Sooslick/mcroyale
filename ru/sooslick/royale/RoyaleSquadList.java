package ru.sooslick.royale;

import java.util.ArrayList;

public class RoyaleSquadList {

    private ArrayList<RoyaleSquad> squads;

    public RoyaleSquadList() {
        squads = new ArrayList<>();
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

}
