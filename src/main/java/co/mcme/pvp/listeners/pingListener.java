package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class pingListener implements Listener {

    @EventHandler
    public void onListPing(ServerListPingEvent event) {
        int maxp;
        String status;
        String motd = ChatColor.DARK_AQUA + "MCMEPVP ";
        if (MCMEPVP.locked) {
            status = ChatColor.RED + "CLOSED";
            maxp = 130;
        } else {
            status = ChatColor.GREEN + "OPEN";
            if (MCMEPVP.debug) {
                status = ChatColor.GOLD + "OPEN BETA TESTING";
            }
            maxp = 130;
        }
        event.setMaxPlayers(maxp);
        event.setMotd(motd + status);
    }
}
