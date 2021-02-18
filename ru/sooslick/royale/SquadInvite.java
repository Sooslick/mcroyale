package ru.sooslick.royale;

import org.bukkit.entity.Player;

public class SquadInvite {
    private final Player invitedPlayer;
    private final RoyaleSquad targetSquad;

    private int lifetime;

    //todo invite type: invite / request

    public SquadInvite(Player p, RoyaleSquad s) {
        invitedPlayer = p;
        targetSquad = s;
        lifetime = 30;
        invitedPlayer.sendMessage(String.format(RoyaleMessages.SQUAD_INCOMING_INVITE, s.getName()));
    }

    public Player getPlayer() {
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
