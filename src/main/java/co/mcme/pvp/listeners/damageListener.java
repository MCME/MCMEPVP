package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.entity.Entity;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class damageListener implements Listener {

    public damageListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        if (MCMEPVP.GameStatus == 0) {
            //TODO General code when no Game is running
        } else {
            MCMEPVP.CurrentGame.onPlayerdie(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void arrowDetect(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile) {
            arrowDamage(event);
            return;
        }
        Entity attacker = event.getDamager();
        if (attacker instanceof Player) {
            playerDamage(event);
        }
    }

    void arrowDamage(EntityDamageByEntityEvent event) {
        if (MCMEPVP.GameStatus == 0) {
            event.setCancelled(true);
        }
        Entity defender = event.getEntity();
        if (defender instanceof Player) {
            boolean isSpectatorDamage = (MCMEPVP.getPlayerStatus((Player) defender).equals("spectator") || MCMEPVP.getPlayerStatus((Player) defender).equals("participant"));
            if (isSpectatorDamage) {
                event.setDamage(0);
            } else {
                MCMEPVP.CurrentGame.onPlayerShoot(event);
            }
        }
    }

    void playerDamage(EntityDamageByEntityEvent event) {
        if (MCMEPVP.GameStatus == 0) {
            event.setCancelled(true);
        }
        Player defender = (Player) event.getEntity();
        boolean isSpectatorDamage = (MCMEPVP.getPlayerStatus(defender).equals("spectator") || MCMEPVP.getPlayerStatus(defender).equals("participant"));
        if (isSpectatorDamage) {
            event.setCancelled(true);
        } else {
            MCMEPVP.CurrentGame.onPlayerhit(event);
        }

    }
}
