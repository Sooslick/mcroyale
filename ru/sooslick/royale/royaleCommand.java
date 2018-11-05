package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class royaleCommand implements CommandExecutor {
    public royale plugin;
    public royaleCommand(royale p) {plugin = p;}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage("§cRoyale commands: /royale, /squad, /votestart, /zone");
            return true;
        }
        if (!sender.hasPermission("royale.admin"))
        {
            sender.sendMessage("§c[Royale] req royale.admin permission");
            return true;
        }
        if ((args[0].equals("start"))||(args[0].equals("startgame")))
        {
            plugin.onStartgameCmd(); //TODO: return int: switch int to msg
            return true;
        }
        if ((args[0].equals("stop"))||(args[0].equals("stopgame")))
        {
            plugin.onStopgameCmd();
            return true;
        }
        if ((args[0].equals("pause"))||(args[0].equals("pausegame")))
        {
            plugin.onPausegameCmd();
            plugin.alertEveryone("TODO CMD: onPause");
            return true;
        }
        if ((args[0].equals("continue"))||(args[0].equals("continuegame")))
        {
            plugin.onContinuegameCmd();
            plugin.alertEveryone("TODO CMD: onContinue");
            return true;
        }

        if ((args[0].equals("join")) || (args[0].equals("joingame")) )
        {
            if (plugin.GameZone.GameActive)
            {
                //TODO check if player has perms or player is playing right now
                if (plugin.GameZone.FirstZone)
                {
                    if (sender instanceof Player)
                    {
                        Player p = (Player) sender;
                        squad s = plugin.onSquadCreate(p);
                        plugin.GameZone.addTeam(s);
                        p.getInventory().clear();
                        plugin.clearArmor(p);
                        p.teleport(plugin.RandomLocation(plugin.CFG.getInt("StartZoneSize", 2048) - 100));
                        Bukkit.broadcastMessage("§aPlayer " + p.getName() + " joined to game and teleported to random location!");
                    }
                    else
                        sender.sendMessage("§cConsole can't play royale :c");
                }
                else
                    sender.sendMessage("§cYou are too late...");
            }
            sender.sendMessage("§cGame is not started.");
            return true;
        }
        sender.sendMessage("§cAvailable commands: startgame, stopgame, join");
        return true;
    }
}
