package ru.sooslick.royale.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.sooslick.royale.Royale;
import ru.sooslick.royale.RoyaleMessages;
import ru.sooslick.royale.RoyalePlayerList;

public class LobbyEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        RoyalePlayerList.update(p);

        p.setGameMode(GameMode.SPECTATOR);
        p.sendMessage(RoyaleMessages.ROYALE_OVERALL);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Royale.unvote(e.getPlayer());
    }

}
