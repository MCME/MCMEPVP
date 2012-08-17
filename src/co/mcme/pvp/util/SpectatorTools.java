package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author meggawatts
 */
public class SpectatorTools implements Listener {

    public static void hide(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(p.equals(player)) && !(MCMEPVP.PlayerStatus.get(player.getName()).equals("spectator"))) {
                player.hidePlayer(p);
            }
            p.setAllowFlight(true);
        }
    }

    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (MCMEPVP.PlayerStatus.get(player.getName()).equals("spectator")) {
            event.setCancelled(true);
        }
    }

    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (MCMEPVP.PlayerStatus.get(player.getName()).equals("spectator")) {
            event.setCancelled(true);
        }
    }

    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (MCMEPVP.PlayerStatus.get(player.getName()).equals("spectator")) {
            event.setCancelled(true);
        }
    }

    public static void show(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(p.equals(player)) && !(MCMEPVP.PlayerStatus.get(player.getName()).equals("spectator"))) {
                player.showPlayer(p);
            }
            p.setAllowFlight(false);
        }
    }
}
