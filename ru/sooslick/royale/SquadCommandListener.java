package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SquadCommandListener implements CommandExecutor {
    private static final String SQUAD_INVITE_USAGE = "§6/squad invite <player name>";
    private static final String SQUAD_VIEW_USAGE = "§6/squad view <squad name>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            //todo send message
            return true;
        }
        RoyaleSquadList squadList = RoyaleSquadList.instance;
        switch (args[0].toLowerCase()) {
            case "list":
                sender.sendMessage(String.format(RoyaleMessages.SQUAD_LIST_FORMAT, squadList.formatSquadList()));
                return true;

            case "view":
                if (args.length == 1) {
                    sender.sendMessage(SQUAD_VIEW_USAGE);
                    return true;
                }
                RoyaleSquad squad = squadList.getSquad(args[1]);
                if (squad == null)
                    sender.sendMessage(RoyaleMessages.SQUAD_NOT_FOUND);
                else
                    sender.sendMessage(String.format(RoyaleMessages.SQUAD_PLAYER_LIST, squad.getName(), squad.formatPlayerList()));
                return true;

            case "create":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                Player player = (Player) sender;
                if (squadList.getSquadByPlayer(player) != null) {
                    player.sendMessage(RoyaleMessages.SQUAD_MEMBER_CREATES);
                    return true;
                }
                String name = args.length == 1 ? RoyaleSquadList.instance.getDefaultSquadName() : args[1];
                squadList.createSquad(name, player);
                return true;

            case "invite":
                if (args.length == 1) {
                    sender.sendMessage(SQUAD_INVITE_USAGE);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                player = (Player) sender;
                squad = squadList.getSquadByPlayer(player);
                if (squad == null) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_MEMBER);
                    return true;
                }
                Player who = Bukkit.getPlayer(args[1]);
                if (who == null) {
                    player.sendMessage(RoyaleMessages.PLAYER_NOT_FOUND);
                    return true;
                }
                //todo check only leader can invite
                //todo invite type
                if (squadList.invitePlayer(squad, who))
                    player.sendMessage(RoyaleMessages.SQUAD_INVITED);
                else
                    player.sendMessage(RoyaleMessages.SQUAD_INVITE_EXISTS);
                return true;

            case "accept":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                player = (Player) sender;
                if (squadList.getSquadByPlayer(player) != null) {
                    player.sendMessage(RoyaleMessages.SQUAD_MEMBER_ACCEPTS);
                    return true;
                }
                SquadInvite invite;
                try {
                    if (args.length == 1)
                        invite = squadList.getInviteByPlayer(player);
                    else
                        invite = squadList.getInvite(squadList.getSquad(args[1]), player);
                } catch (SquadInviteException e) {
                    sender.sendMessage(e.getMessage());
                    return true;
                }
                //todo check invite type
                squadList.inviteAccept(invite);
                return true;

            case "decline":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                player = (Player) sender;
                try {
                    if (args.length == 1)
                        invite = squadList.getInviteByPlayer(player);
                    else
                        invite = squadList.getInvite(squadList.getSquad(args[1]), player);
                } catch (SquadInviteException e) {
                    sender.sendMessage(e.getMessage());
                    return true;
                }
                //todo check invite type
                squadList.inviteDecline(invite);
                return true;
        }
        return false;
    }
}
