package ru.sooslick.royale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandProcessor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
        switch (RoyaleCommand.valueOf(cmd.getName())) {
            case ROYALE:
                //todo:
                //  /royale (/rl)
                //  /royale config (cfg)
                //  /royale config reload
                //  /royale config save
                //  /royale config defaults
                //  /royale config set
                //  /royale config view
                //  /royale config help
                //  /royale help
                //  /royale rules
                //  /royale start
                //  /royale debug
                //  /royale stop
                //  /royale join
                //  /royale reset (csd) - cancel shutdown, reset inventories, timers and containers
                return true;
            case SQUAD:
                //todo:
                //  /squad (/sq)
                //  /squad create [name] [invites...]
                //  /squad invite
                //  /squad accept
                //  /squad decline
                //  /squad request
                //  /squad kick
                //  /squad setopen - enable autobalancer - rename plz
                //  /squad leave
                //  /squad disband
                //  /squad view
                //  /squad list
                //  /squad say (msg, etc...)
                //  /squad rename
                return true;
            case ZONE:
                //todo:
                //  /zone (/z)
                //  /zone map
                //  /zone compass
                return true;
            case VOTESTART:
                //todo:
                //  /votestart (/v, /vs)
                return true;
            //todo: stat cmd?
        }
        return false;
    }

    private enum RoyaleCommand {
        ROYALE("royale"),
        SQUAD("squad"),
        ZONE("zone"),
        VOTESTART("votestart");

        private final String command;

        private RoyaleCommand(String s) {
            command = s;
        }

        public String toString() {
            return command;
        }
    }

}
