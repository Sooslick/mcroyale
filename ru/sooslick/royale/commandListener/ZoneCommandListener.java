package ru.sooslick.royale.commandListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.sooslick.royale.GameState;
import ru.sooslick.royale.Royale;
import ru.sooslick.royale.RoyaleMessages;
import ru.sooslick.royale.RoyalePlayerList;

public class ZoneCommandListener implements CommandExecutor {
    private static final String ZONE_USAGE = "/zone config <map | compass> <on | off>";
    private static final String ON = "on";
    private static final String OFF = "off";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //main zone command
        if (args.length == 0) {
            if (Royale.getGameState() == GameState.GAME) {
                //todo provide zone info
                return true;
            } else {
                sender.sendMessage(RoyaleMessages.ROYALE_IS_NOT_RUNNING);
                return true;
            }
        }

        //zone subcommands
        if ("help".equals(args[0].toLowerCase())) {
            sender.sendMessage(RoyaleMessages.ZONE_COMMAND_HELP);
            return true;
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(RoyaleMessages.CONSOLE_CANNOT);
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ZONE_USAGE);
                return true;
            }
            Boolean value = parseBoolean(args[2]);
            if (value == null) {
                sender.sendMessage(ZONE_USAGE);
                return true;
            }
            if (!RoyalePlayerList.get((Player) sender).setSquadParam(args[1], value)) {
                sender.sendMessage(ZONE_USAGE);
                return true;
            }
            sender.sendMessage(RoyaleMessages.PLAYER_CONFIG_SET);
        }
        return true;
    }

    private Boolean parseBoolean(String parse) {
        switch (parse.toLowerCase()) {
            case ON: return Boolean.TRUE;
            case OFF: return Boolean.FALSE;
            default: return null;
        }
    }
}
