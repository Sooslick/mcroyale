package ru.sooslick.royale;

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
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§c[Royale] console are banned from battle royale. Console is cheater!");
            return true;
        }
        if (args.length == 0)
        {
            sender.sendMessage("§6Squad commands: create, invite, accept, decline, kick, disband, view, leave");
            return true;
        }
        if (args[0].equals("create"))
        {
            //if !empty args 1
            //    todo squad name
            plugin.onSquadCreate((Player)sender);
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
            plugin.onSquadView((Player)sender);
            return true;
        }
        if (args[0].equals("leave"))
        {
            plugin.onSquadLeave((Player)sender);
            return true;
        }
        //TODO: squad view: param squadname
        //TODO: squad list cmd
        sender.sendMessage("§6Squad commands: create, invite, accept, decline, kick, disband, view, leave");
        return true;
    }
}

