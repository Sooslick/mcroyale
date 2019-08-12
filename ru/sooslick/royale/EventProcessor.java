package ru.sooslick.royale;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventProcessor implements Listener {

    private static Royale R;

    public EventProcessor(Royale royale) {
        R = royale;
        //todo get RoyalePlayerList
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        //todo: check if joined player presents in RoyalePlayerList
        //todo: prepare if killed or game !started
        //otherwise
        RoyalePlayer rp = new RoyalePlayer(p);
        //todo add rp to rplist
        rp.prepare();
        //todo: messages
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        //todo: get rp by player
        //todo: set disconnected state if playing
        //todo: move inv to chest and clear
        //todo: set player null
        //todo: rm from squad if !playing
        //todo: messages
    }

}
