package ru.sooslick.royale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
            sender.sendMessage("§cTry §6/royale help §cto get more info about commands!");
            return true;
        }
        if (args[0].equals("help"))
        {
            sender.sendMessage("=-=-§eBATTLE ROYALE§f-=-=");
            sender.sendMessage("§6Battle without rules. Your squad spawns at randomly chosen location with bare hands. Now you must craft weapon and be ready to further fighting!");
            sender.sendMessage("§6Game zone will shrink all the time. If you get outside, you will die. To avoid this, check your coordinates on map or §e/zone§6 command.");
            sender.sendMessage("§6Text blah blah blah");
            return true;
        }
        if (!sender.hasPermission("royale.admin"))
        {
            sender.sendMessage("§c[Royale] req royale.admin permission");
            return true;
        }
        if ((args[0].equals("start"))||(args[0].equals("startgame")))
        {
            plugin.onStartgameCmd(false);
            return true;
        }
        if ((args[0].equals("debug"))||(args[0].equals("startdebug")))
        {
            plugin.onStartgameCmd(true);
            return true;
        }
        if ((args[0].equals("stop"))||(args[0].equals("stopgame")))
        {
            plugin.onStopgameCmd();
            return true;
        }
        if ((args[0].equals("join")) || (args[0].equals("joingame")) )
        {
            if (!plugin.GameZone.GameActive) {
                sender.sendMessage("§cGame is not started.");
                return true;
            }
            //TODO check if player has perms or player is playing right now
            if (!plugin.GameZone.FirstZone) {
                sender.sendMessage("§cYou are too late...");
                return true;
            }
            //join self
            if (args.length==1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (p.getGameMode().equals(GameMode.SURVIVAL))
                        sender.sendMessage("§cYou are playing now. Can't rejoin!");
                    else {
                        plugin.respawnPlayer(p);
                        p.teleport(plugin.RandomLocation(plugin.CFG.getInt("StartZoneSize", 2048) - 100));
                        Bukkit.broadcastMessage("§aPlayer " + p.getName() + " joined to game and teleported to random location!");
                        return true;
                    }
                } else
                    sender.sendMessage("§cConsole can't play royale :c");
            }
            //join smth else
            else {
                String pn = args[1];
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().equals(pn)) {
                        if (p.getGameMode().equals(GameMode.SURVIVAL))
                            sender.sendMessage("§cPlayer are playing now. Can't rejoin him!");
                        else {
                            plugin.respawnPlayer(p);
                            p.teleport(plugin.RandomLocation(plugin.CFG.getInt("StartZoneSize", 2048) - 100));
                            Bukkit.broadcastMessage("§aPlayer " + p.getName() + " joined to game and teleported to random location!");
                            return true;
                        }
                    }
                }
                sender.sendMessage("§cCan't find player " + pn);
            }
            return true;
        }

        if (args[0].equals("csd")) {
            plugin.cancelShutDown();
            return true;
        }
        sender.sendMessage("§cAvailable commands: startgame, stopgame, join, csd, debug");
        return true;
    }
}
