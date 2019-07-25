package ru.sooslick.royale.legacy;

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
        if (args.length == 0)
        {
            sender.sendMessage("§6Squad commands: create, invite, accept, decline, request, kick, setopen, leave, disband, view, list, say, msg");
            return true;
        }
        if (args[0].equals("list"))
        {
            plugin.sendSquadList(sender);
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
                return true;
            }
            for (int i=1; i<args.length; i++)
                plugin.onSquadInvite((Player)sender, args[i]);
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
        if (args[0].equals("request")) {
            if (args.length == 1) {
                sender.sendMessage("§6/squad request <squad>");
                return true;
            }
            squad s = plugin.getSquad(args[1]);
            if (s == null) {
                sender.sendMessage("§cThis squad not exists!");
                return true;
            }
            //todo flood control
            if (!s.getOpen()) {
                sender.sendMessage("§cThis squad is closed!");
                return true;
            }
            plugin.getServer().getPlayer( s.getLeader() ).sendMessage("§cPlayer " + sender.getName() + " wants to join to your squad!");
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
        if (args[0].equals("setopen"))
        {
            Player p = (Player) sender;
            squad s = plugin.getSquad(p);
            if (s == null) {
                sender.sendMessage("§cYou are not in squad!");
                return true;
            }
            if (!s.getLeader().equals(p.getName())) {
                sender.sendMessage("§cYou must be a leader of squad!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage("§6Change accessiblity to your squad. If false, no one can join or send join requests to you.");
                if (s.getOpen())
                    sender.sendMessage("§6Now your squad is §aopened");
                else
                    sender.sendMessage("§6Now your squad is §cclosed");
                sender.sendMessage("§6Usage: /squad setopen true|false");
                return true;
            }
            s.setOpen(Boolean.parseBoolean(args[2]));
            if (s.getOpen())
                sender.sendMessage("§6Accessiblity changed! Now your squad is §aopened");
            else
                sender.sendMessage("§6Accessiblity changed! Now your squad is §cclosed");
            return true;
        }
        if (args[0].equals("disband"))
        {
            plugin.onSquadDisband((Player)sender);
            return true;
        }
        if (args[0].equals("leave"))
        {
            plugin.onSquadLeave((Player)sender);
            return true;
        }
        if (args[0].equals("rename"))
        {
            //check if name param passed
            if (args.length == 1) {
                sender.sendMessage("§cTry /squad rename <SquadName>");
                return true;
            }
            //check if player in squad
            squad s = plugin.getSquad((Player)sender);
            if (s == null) {
                sender.sendMessage("§c[Royale] You are not in squad!");
                return true;
            }
            //check if player have permission to manage squad
            if (!s.getLeader().equals(sender.getName())) {
                sender.sendMessage("§c[Royale] You are not the squad leader!");
                return true;
            }
            //check if same squadname exists
            squad s1 = plugin.getSquad(args[1]);
            if (s1 != null) {
                sender.sendMessage("§c[Royale] Squad with same name is exists!");
                return true;
            }
            //rename
            Bukkit.broadcastMessage("§c[Royale] Now the squad \"" + s.getName() + "\" have new name \"" + args[1] + "\"!");
            s.setName(args[1]);
            sender.sendMessage("§a[Royale] Your squad renamed.");
            return true;
        }
        if (args[0].equals("msg") || args[0].equals("say")) {
            if (args.length == 1) {
                sender.sendMessage("§cTry /ts <message>");      //todo: command. Refactor method; set executor; desc in plugin.yml
                return true;
            }
            else {
                String msg = "§7[Squad] <" + sender.getName() + ">: ";
                for (int i = 1; i < args.length; i++)
                    msg += args[1] + " ";
                for (String pln : plugin.getSquad((Player) sender).getPlayers())
                    Bukkit.getPlayer(pln).sendMessage(msg);
            }
            return true;
        }
        sender.sendMessage("§6Squad commands: create, rename invite, accept, decline, kick, disband, view, leave");
        return true;
    }
}

