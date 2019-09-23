package ru.sooslick.royale;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static ru.sooslick.royale.Royale.players;

import static ru.sooslick.royale.Royale.R;      //todo - implement events

public class EventProcessor implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        RoyalePlayer rp = players.getPlayerByName(p.getName());

        //check if player reconnected
        if (rp != null) {
            rp.setPlayer(p);

            //check if player reconnected while GAME state
            if (Royale.gameState.equals(GameState.GAME)) {
                //todo check isPlaying state
                // if isPlaying -> restore inv
                // else -> prepare();
                //todo messages
            }

            //otherwise just join as spectator
            else {
                rp.prepare();
                //todo messages
            }
        }

        //RoyalePlayer not found - just join as spectator
        rp = new RoyalePlayer(p);
        players.add(rp);
        rp.prepare();
        //todo: messages
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        players.getPlayerByName(e.getPlayer().getName()).disconnect();
        //todo Messages
        //todo: check if alive -> royale event req
    }

}
