package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by sooslick on 26.06.2018.
 */
public class squadCommand implements CommandExecutor {
    public royale plugin;
    public squadCommand(royale p) {plugin = p;}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!sender.hasPermission("royale.squad"))
        {
            sender.sendMessage("§c[Royale] req royale.squad permission");
            return true;
        }
        if (args[0].equals("list"))
        {
            plugin.sendSquadList(sender);
            return true;
        }
        if (plugin.GameZone.GameActive)
        {
            sender.sendMessage("§c[Royale] You can't manage squad while game is running!");
            return true;
        }
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§c[Royale] console are banned from battle royale. Console is cheater!");
            return true;
        }
        if (args.length == 0)
        {
            sender.sendMessage("§6Squad commands: create, invite, accept, decline, kick, disband, view, leave, list");
            return true;
        }
        if (args[0].equals("create"))
        {
            String sn;
            if (args.length == 1)
                sn = sender.getName();
            else
                sn = args[1];
            plugin.onSquadCreate((Player)sender, sn);
            return true;
        }
        if (args[0].equals("invite"))
        {
            if (args.length == 1) {
                sender.sendMessage("§6/squad invite <player1> [player2] [player3]...");
                //todo multiplr invite proc
                return true;
            }
            plugin.onSquadInvite((Player)sender, args[1]);
            return true;
        }
        if (args[0].equals("accept"))
        {
            plugin.onSquadInviteAccept((Player)sender);
            return true;
        }
        if (args[0].equals("decline"))
        {
            plugin.onSquadInviteDecline((Player)sender);
            return true;
        }
        if (args[0].equals("kick"))
        {
            if (args.length == 1) {
                sender.sendMessage("§6/squad kick <player>");
                return true;
            }
            plugin.onSquadKick((Player)sender, args[1]);
            return true;
        }
        if (args[0].equals("disband"))
        {
            plugin.onSquadDisband((Player)sender);
            return true;
        }
        if (args[0].equals("view"))
        {
            if (args.length == 1)
                plugin.onSquadView((Player)sender, "");
            else
                plugin.onSquadView((Player)sender, args[1]);
            return true;
        }
        if (args[0].equals("leave"))
        {
            plugin.onSquadLeave((Player)sender);
            return true;
        }
        if (args[0].equals("rename"))
        {
            if (args.length == 1) {
                sender.sendMessage("§cTry /squad rename <SquadName>");
                return true;
            }
            squad s = plugin.getSquad((Player)sender);
            if (s.equals(plugin.EmptySquad)) {
                sender.sendMessage("§c[Royale] You are not in squad!");
                return true;
            }
            if (!s.leader.equals(sender.getName())) {
                sender.sendMessage("§c[Royale] You are not the squad leader!");
                return true;
            }
            Bukkit.broadcastMessage("§c[Royale] Now the squad \"" + s.name + "\" have new name \"" + args[1] + "\"!");
            s.name = args[1];
            sender.sendMessage("§a[Royale] Your squad renamed.");
            return true;
        }
        //TODO: squad join request
        //TODO: squad list cmd
        sender.sendMessage("§6Squad commands: create, rename invite, accept, decline, kick, disband, view, leave");
        return true;
    }
}

