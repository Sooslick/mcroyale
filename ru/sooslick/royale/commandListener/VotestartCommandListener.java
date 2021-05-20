package ru.sooslick.royale.commandListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.sooslick.royale.Royale;
import ru.sooslick.royale.RoyaleLogger;
import ru.sooslick.royale.RoyaleMessages;

public class VotestartCommandListener implements CommandExecutor {
    private static VotestartCommandListener instance;

    public static void execute(CommandSender sender) {
        if (instance != null) {
            instance.onCommand(sender, null, null, null);
        } else {
            sender.sendMessage(RoyaleMessages.SYSTEM_ERROR);
            RoyaleLogger.warn("VotestartCommandListener is not registered");
        }
    }

    public VotestartCommandListener() {
        instance = this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
            return true;
        }
        Royale.votestart((Player) sender);
        return true;
    }
}
