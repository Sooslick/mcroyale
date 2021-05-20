package ru.sooslick.royale.commandListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.sooslick.royale.GameState;
import ru.sooslick.royale.Royale;
import ru.sooslick.royale.RoyaleMessages;
import ru.sooslick.royale.RoyalePermissions;

public class RoyaleCommandListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(RoyaleMessages.ROYALE_OVERALL);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                sender.sendMessage(RoyaleMessages.ROYALE_SUBCOMMANDS);
                return true;
            case "reload":
                if (!sender.hasPermission(RoyalePermissions.RELOAD)) {
                    sender.sendMessage(RoyaleMessages.NO_PERMISSION);
                    return true;
                }
                if (Royale.getGameState() == GameState.GAME) {
                    sender.sendMessage(RoyaleMessages.ROYALE_IS_RUNNING);
                }
                Royale.reload();
                return true;
            case "votestart":
            case "vs":
                VotestartCommandListener.execute(sender);
                return true;
        }
        return true;
    }
}
