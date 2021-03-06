package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import static co.mcme.pvp.util.config.statusOpen;
import static co.mcme.pvp.util.config.statusClosed;
import static co.mcme.pvp.util.config.statusBeta;

public class pingListener implements Listener {

    @EventHandler
    public void onListPing(ServerListPingEvent event) {
        int maxp;
        String status;
        String motd = ChatColor.DARK_AQUA + "MCMEPVP ";
        if (MCMEPVP.locked) {
            status = ChatColor.RED + statusClosed;
            maxp = 130;
        } else {
            status = ChatColor.GREEN + statusOpen;
            if (MCMEPVP.gameDebug) {
            	status = ChatColor.GOLD + statusBeta;
            }
            maxp = 130;
        }
        event.setMaxPlayers(maxp);
        event.setMotd(motd + status);
    }
}
