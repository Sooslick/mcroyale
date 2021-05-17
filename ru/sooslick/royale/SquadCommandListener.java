package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SquadCommandListener implements CommandExecutor {
    private static final String SQUAD_CONFIG_USAGE = "§6/squad config <autobalance | invite | request> <allow | deny>";
    private static final String SQUAD_INVITE_USAGE = "§6/squad invite <player name>";
    private static final String SQUAD_KICK_USAGE = "§6/squad kick <player name>";
    private static final String SQUAD_RENAME_USAGE = "§6/squad rename <new name>";
    private static final String SQUAD_REQUEST_USAGE = "§6/squad view <squad name>";
    private static final String SQUAD_SUBCOMMANDS = "§6/squad <list | view | create | invite | accept | request | kick | leave | disband | rename | config | say>";
    private static final String SQUAD_VIEW_USAGE = "§6/squad view <squad name>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(SQUAD_SUBCOMMANDS);
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
                    player.sendMessage(RoyaleMessages.SQUAD_MEMBER_CANNOT);
                    return true;
                }
                String name = args.length == 1 ? RoyaleSquadList.instance.getDefaultSquadName() : args[1];
                squadList.createSquad(name, player);
                return true;

            case "invite":
                //validate command
                if (args.length == 1) {
                    sender.sendMessage(SQUAD_INVITE_USAGE);
                    return true;
                }
                //only player can invite
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                player = (Player) sender;
                squad = squadList.getSquadByPlayer(player);
                //only squad member can invite
                if (squad == null) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_MEMBER);
                    return true;
                }
                if (!squad.getSquadParam(RoyalePlayer.ALLOW_INVITE) && !squad.getLeader().getPlayer().equals(player)) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_LEADER);
                    return true;
                }
                // can invite online player only
                Player who = Bukkit.getPlayer(args[1]);
                if (who == null) {
                    player.sendMessage(RoyaleMessages.PLAYER_NOT_FOUND);
                    return true;
                }
                //check existing requests
                try {
                    SquadInvite request = squadList.getRequest(squad, who);
                    squadList.inviteAccept(request);
                    return true;
                } catch (SquadInviteNotFoundException ignored) {}
                //invite player
                try {
                    //check exists
                    squadList.getInvite(squad, who);
                    player.sendMessage(RoyaleMessages.SQUAD_INVITE_EXISTS);
                } catch (SquadInviteNotFoundException e) {
                    // create invite if not found
                    squadList.invitePlayer(squad, who);
                    player.sendMessage(RoyaleMessages.SQUAD_INVITED);
                }
                return true;

            case "accept":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                player = (Player) sender;
                squad = squadList.getSquadByPlayer(player);
                name = args.length > 1 ? args[1] : null;
                if (squad == null)
                    acceptInvites(player, name);
                else
                    acceptRequests(player, squad, name);
                return true;

            case "request":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(SQUAD_REQUEST_USAGE);
                    return true;
                }
                player = (Player) sender;
                squad = squadList.getSquadByPlayer(player);
                if (squad != null) {
                    sender.sendMessage(RoyaleMessages.SQUAD_MEMBER_CANNOT);
                    return true;
                }
                squad = squadList.getSquad(args[1]);
                if (squad == null){
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_FOUND);
                    return true;
                }
                try {
                    SquadInvite invite = squadList.getInvite(squad, player);
                    squadList.inviteAccept(invite);
                } catch (SquadInviteNotFoundException e) {
                    if (squad.getSquadParam(RoyalePlayer.ALLOW_REQUEST)) {
                        player.sendMessage(RoyaleMessages.SQUAD_REQUESTED);
                        squad.sendMessage(null, String.format(RoyaleMessages.SQUAD_INCOMING_REQUEST, player));
                        squadList.joinRequest(squad, player);
                    }
                    else
                        player.sendMessage(RoyaleMessages.SQUAD_PRIVATE);
                }
                return true;

            case "kick":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(SQUAD_KICK_USAGE);
                    return true;
                }
                player = (Player) sender;
                squad = squadList.getSquadByPlayer(player);
                if (squad == null) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_LEADER);
                    return true;
                }
                RoyalePlayer leader = squad.getLeader();
                if (!leader.getPlayer().equals(player)) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_LEADER);
                    return true;
                }
                Player victim = Bukkit.getPlayer(args[1]);
                if (victim == null) {
                    sender.sendMessage(RoyaleMessages.PLAYER_NOT_FOUND);
                    return true;
                }
                boolean kicked = squad.rmPlayer(RoyalePlayerList.get(victim));
                if (kicked)
                    player.sendMessage(String.format(RoyaleMessages.SQUAD_KICKED_PLAYER, args[1]));
                else
                    player.sendMessage(RoyaleMessages.SQUAD_KICKED_YOURSELF);
                return true;

            case "leave":
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
                boolean left = squad.rmPlayer(RoyalePlayerList.get(player));
                if (left)
                    squad.sendMessage(null, String.format(RoyaleMessages.SQUAD_PLAYER_LEFT, player.getName()));
                else
                    sender.sendMessage(RoyaleMessages.SQUAD_KICKED_YOURSELF);
                return true;

            case "disband":
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
                leader = squad.getLeader();
                if (!leader.getPlayer().equals(player)) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_LEADER);
                    return true;
                }
                squad.sendMessage(null, RoyaleMessages.SQUAD_DISBAND);
                squadList.disbandSquad(squad);
                return true;

            case "rename":
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
                leader = squad.getLeader();
                if (!leader.getPlayer().equals(player)) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_LEADER);
                    return true;
                }
                if (args.length <= 1) {
                    sender.sendMessage(SQUAD_RENAME_USAGE);
                    return true;
                }
                squad.setName(args[1]);
                squad.sendMessage(null, String.format(RoyaleMessages.SQUAD_RENAMED, squad.getName()));
                return true;

            case "config":
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
                leader = squad.getLeader();
                if (!leader.getPlayer().equals(player)) {
                    player.sendMessage(RoyaleMessages.SQUAD_NOT_LEADER);
                    return true;
                }
                if (args.length <= 1) {
                    sender.sendMessage(SQUAD_CONFIG_USAGE);
                    return true;
                }
                if (args.length <= 2) {
                    switch (args[1].toLowerCase()) {
                        case RoyalePlayer.ALLOW_BALANCE:
                            sender.sendMessage(String.format(RoyaleMessages.SQUAD_CONFIG_AUTOBALANCE, squad.getSquadParam(RoyalePlayer.ALLOW_BALANCE)));
                            return true;
                        case RoyalePlayer.ALLOW_INVITE:
                            sender.sendMessage(String.format(RoyaleMessages.SQUAD_CONFIG_INVITE, squad.getSquadParam(RoyalePlayer.ALLOW_INVITE)));
                            return true;
                        case RoyalePlayer.ALLOW_REQUEST:
                            sender.sendMessage(String.format(RoyaleMessages.SQUAD_CONFIG_REQUEST, squad.getSquadParam(RoyalePlayer.ALLOW_REQUEST)));
                            return true;
                        default:
                            sender.sendMessage(SQUAD_CONFIG_USAGE);
                    }
                    return true;
                }
                Boolean value = parsePermission(args[2]);
                if (value == null) {
                    sender.sendMessage(SQUAD_CONFIG_USAGE);
                    return true;
                }
                if (squad.setRestriction(args[1], value))
                    sender.sendMessage(RoyaleMessages.SQUAD_CONFIG_SET);
                else
                    sender.sendMessage(SQUAD_CONFIG_USAGE);
                return true;

            case "say":
                TeamSayCommandListener.execute(sender, args);
                return true;
        }
        return true;
    }

    private void acceptInvites(Player accepter, String squadName) {
        RoyaleSquadList squadList = RoyaleSquadList.instance;
        RoyaleSquad squad = squadList.getSquad(squadName);
        if (squad == null && squadName != null) {
            accepter.sendMessage(RoyaleMessages.SQUAD_NOT_FOUND);
            return;
        }
        try {
            SquadInvite invite = squad == null ? squadList.getInviteByPlayer(accepter) : squadList.getInvite(squad, accepter);
            squadList.inviteAccept(invite);
        } catch (SquadMultipleInvitesException e) {
            accepter.sendMessage(RoyaleMessages.SQUAD_MULTIPLE_INVITES);
        } catch (SquadInviteException e) {
            accepter.sendMessage(RoyaleMessages.SQUAD_INVITE_NOT_FOUND);
        }
    }

    private void acceptRequests(Player accepter, RoyaleSquad squad, String playerName) {
        RoyaleSquadList squadList = RoyaleSquadList.instance;
        if (!squad.getSquadParam(RoyalePlayer.ALLOW_INVITE) && accepter != squad.getLeader().getPlayer()) {
            accepter.sendMessage(RoyaleMessages.SQUAD_NOT_LEADER);
        }
        try {
            SquadInvite req = playerName == null ? squadList.getRequestBySquad(squad) : squadList.getRequest(squad, Bukkit.getPlayer(playerName));
            squadList.inviteAccept(req);
        } catch (SquadMultipleInvitesException e) {
            accepter.sendMessage(RoyaleMessages.SQUAD_MULTIPLE_INVITES);
        } catch (SquadInviteException e) {
            accepter.sendMessage(RoyaleMessages.SQUAD_INVITE_NOT_FOUND);
        }
    }

    private Boolean parsePermission(String value) {
        switch (value.toLowerCase()) {
            case "allow": return Boolean.TRUE;
            case "deny": return Boolean.FALSE;
            default: return null;
        }
    }
}
