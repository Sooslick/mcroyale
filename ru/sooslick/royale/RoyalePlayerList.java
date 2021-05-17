package ru.sooslick.royale;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoyalePlayerList {
    private static final Map<Player, RoyalePlayer> PLAYERS = new HashMap<>();

    private RoyalePlayerList() {}

    public static void initPlayer(Player p) {
        RoyalePlayer rp = PLAYERS.get(p);
        if (rp == null) {
            rp = new RoyalePlayer(p);
            PLAYERS.put(p, rp);
        }
    }

    public static RoyalePlayer get(Player p) {
        return PLAYERS.get(p);
    }

    public static RoyalePlayer get(String name) {
        for (RoyalePlayer rp : PLAYERS.values()) {
            if (rp.getName().equals(name)) {
                return rp;
            }
        }
        return null;
    }

}
