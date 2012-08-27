package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class playerListener implements Listener {

    public playerListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerJoin(final PlayerLoginEvent event) {
        MCMEPVP.setPlayerStatus(event.getPlayer(), "spectator", ChatColor.WHITE);
        if (MCMEPVP.GameStatus == 0) {
            Vector vec = MCMEPVP.Spawns.get("spectator");
            Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
            event.getPlayer().teleport(loc);
        } else {
            MCMEPVP.CurrentGame.onPlayerjoinServer(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerLeave(final PlayerQuitEvent event) {
        if (MCMEPVP.GameStatus == 0) {
            //TODO General code when no Game is running
        } else {
            MCMEPVP.CurrentGame.onPlayerleaveServer(event);
            MCMEPVP.setPlayerStatus(event.getPlayer(), "spectator", ChatColor.WHITE);
        }
    }
}