package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.ChatColor;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class damageListener implements Listener {

    public damageListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDeath(final PlayerDeathEvent event) {
        event.getDrops().clear();
        if (MCMEPVP.GameStatus == 0) {
            //TODO General code when no Game is running
        } else {
            MCMEPVP.CurrentGame.onPlayerdie(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player Victim = (Player) event.getEntity();
            if (MCMEPVP.getPlayerStatus(Victim).equals("spectator") || MCMEPVP.getPlayerStatus(Victim).equals("participant")) {
                event.setCancelled(true);
            }
            if (event.getDamager().getType().equals(EntityType.PLAYER)) {
                if (MCMEPVP.GameStatus == 0) {
                    event.setCancelled(true);
                } else {
                    MCMEPVP.CurrentGame.onPlayerhit(event);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDamage(final EntityDamageEvent event) {
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player Victim = (Player) event.getEntity();
            if (MCMEPVP.getPlayerStatus(Victim).equals("spectator") || MCMEPVP.getPlayerStatus(Victim).equals("participant")) {
                event.setCancelled(true);
            }
        }
    }
}
