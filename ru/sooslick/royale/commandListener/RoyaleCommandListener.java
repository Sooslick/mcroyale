package ru.sooslick.royale.commandListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.sooslick.royale.GameState;
import ru.sooslick.royale.Royale;
import ru.sooslick.royale.RoyaleMessages;
import ru.sooslick.royale.RoyalePermissions;

public class RoyaleCommandListener implements CommandExecutor {
    private static final String FORCE = "f";

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

            case "start":
                boolean force = (args.length > 1) && args[1].toLowerCase().startsWith(FORCE);
                String permission = force ? RoyalePermissions.START_FORCE : RoyalePermissions.START;
                if (!sender.hasPermission(permission)) {
                    sender.sendMessage(RoyaleMessages.NO_PERMISSION);
                    return true;
                }
                if (!Royale.R.prestart(force))
                    sender.sendMessage(RoyaleMessages.ROYALE_IS_RUNNING);
                return true;
        }
        return true;
    }
}
