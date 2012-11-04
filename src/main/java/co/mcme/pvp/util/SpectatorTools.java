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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author meggawatts
 */
public class SpectatorTools implements Listener {

    public static void hide(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 0));
    }

    public static void show(Player p) {
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
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
