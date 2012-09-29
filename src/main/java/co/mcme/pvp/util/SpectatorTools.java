package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author meggawatts
 */
public class SpectatorTools implements Listener {

    public static void hide(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(p.equals(player)) && !MCMEPVP.getPlayerStatus(p).equals("spectator")) {
                player.hidePlayer(p);
            }
            p.setAllowFlight(true);
        }
    }

    public static void show(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(p.equals(player)) && !(MCMEPVP.getPlayerStatus(p).equals("spectator"))) {
                player.showPlayer(p);
            }
            p.setAllowFlight(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (MCMEPVP.getPlayerStatus(player).equals("spectator")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (MCMEPVP.getPlayerStatus(player).equals("spectator")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (MCMEPVP.getPlayerStatus(player).equals("spectator")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDamage(final EntityDamageEvent event) {
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player Victim = (Player) event.getEntity();
            if (MCMEPVP.getPlayerStatus(Victim).equals("spectator")) {
                event.setCancelled(true);
            }
        }
    }
}
