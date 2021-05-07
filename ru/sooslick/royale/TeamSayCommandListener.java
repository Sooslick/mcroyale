package ru.sooslick.royale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamSayCommandListener implements CommandExecutor {
    private static TeamSayCommandListener instance;

    public static void execute(CommandSender sender, String[] args) {
        if (instance != null) {
            instance.onCommand(sender, null, null, args);
        } else {
            sender.sendMessage(RoyaleMessages.TEAMSAY_ERROR);
            RoyaleLogger.warn("TeamSayCommandListener is not registered");
        }
    }

    public TeamSayCommandListener() {
        instance = this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
            return true;
        }
        Player player = (Player) sender;
        RoyaleSquad squad = RoyaleSquadList.instance.getSquadByPlayer(player);
        if (squad == null) {
            player.sendMessage(RoyaleMessages.SQUAD_NOT_MEMBER);
            return true;
        }
        squad.sendMessage(squad.getRoyalePlayer(player), String.join(" ", strings));
        return true;
    }
}
