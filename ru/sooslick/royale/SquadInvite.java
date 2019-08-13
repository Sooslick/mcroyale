package ru.sooslick.royale;

public class SquadInvite {

    private RoyalePlayer invitedPlayer;
    private RoyaleSquad targetSquad;
    private int lifetime;

    public SquadInvite(RoyalePlayer p, RoyaleSquad s) {
        invitedPlayer = p;
        targetSquad = s;
        lifetime = 30;
    }

    public RoyalePlayer getPlayer() {
        return invitedPlayer;
    }

    public RoyaleSquad getSquad() {
        return targetSquad;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void tick() {
        lifetime--;
    }

}
