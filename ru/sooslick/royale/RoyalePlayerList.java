package ru.sooslick.royale;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoyalePlayerList {
    private static final Map<Player, RoyalePlayer> PLAYERS = new HashMap<>();

    private RoyalePlayerList() {}

    public static RoyalePlayer update(Player p) {
        RoyalePlayer rp = get(p);
        if (rp == null) {
            // check by name. Player instance might be changed after rejoin
            RoyalePlayer rpOld = get(p.getName());
            if (rpOld == null) {
                // not exists, just create new instance
                rp = new RoyalePlayer(p);
                PLAYERS.put(p, rp);
            } else {
                //exists; replace player in rp and replace map entry
                PLAYERS.remove(rpOld.getPlayer());
                PLAYERS.put(p, rpOld);
                rpOld.setPlayer(p);
                rp = rpOld;
            }
        }
        return rp;
    }

    public static Collection<RoyalePlayer> getList() {
        return PLAYERS.values();
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
