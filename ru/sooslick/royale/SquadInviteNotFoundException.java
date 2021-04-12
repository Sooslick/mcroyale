package ru.sooslick.royale;

public class SquadInviteNotFoundException extends SquadInviteException {

    public SquadInviteNotFoundException() {
        super(RoyaleMessages.SQUAD_NOT_FOUND);
    }
}
