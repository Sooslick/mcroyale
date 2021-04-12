package ru.sooslick.royale;

import org.bukkit.entity.Player;

public class SquadInvite {
    private final Player invitedPlayer;
    private final RoyaleSquad targetSquad;
    private final Type type;

    public SquadInvite(Player invitedPlayer, RoyaleSquad targetSquad, Type type) {
        this.invitedPlayer = invitedPlayer;
        this.targetSquad = targetSquad;
        this.type = type;

        if (type == Type.INVITE) {
            if (RoyaleSquadList.instance.getInvitesByPlayer(invitedPlayer).isEmpty())
                invitedPlayer.sendMessage(String.format(RoyaleMessages.SQUAD_INCOMING_INVITE, targetSquad.getName()));
            else
                invitedPlayer.sendMessage(String.format(RoyaleMessages.SQUAD_INCOMING_INVITES, targetSquad.getName()));
        } else {
            if (RoyaleSquadList.instance.getRequestsBySquad(targetSquad).isEmpty())
                invitedPlayer.sendMessage(String.format(RoyaleMessages.SQUAD_INCOMING_REQUEST, invitedPlayer.getName()));
            else
                invitedPlayer.sendMessage(String.format(RoyaleMessages.SQUAD_INCOMING_REQUESTS, invitedPlayer.getName()));
        }
    }

    public Player getPlayer() {
        return invitedPlayer;
    }

    public RoyaleSquad getSquad() {
        return targetSquad;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        INVITE,
        REQUEST
    }
}
