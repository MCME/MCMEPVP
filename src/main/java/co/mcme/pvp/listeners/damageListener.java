package co.mcme.pvp.listeners;

import static co.mcme.pvp.listeners.statsListener.playerStats;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.teamUtil;

public class damageListener implements Listener {

	public damageListener(MCMEPVP instance) {
	}

	@EventHandler(priority = EventPriority.HIGH)
	void onPlayerDeath(PlayerDeathEvent event) {
		event.getDrops().clear();
		event.setDroppedExp(0);

		if (MCMEPVP.GameStatus == 0) {
			// TODO General code when no Game is running
		} else {
			MCMEPVP.CurrentGame.onPlayerdie(event);
			if (event.getEntity().isInsideVehicle()) {
				event.getEntity().getVehicle().remove();
			}
			event.setKeepLevel(true);
			event.setDroppedExp(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	void onRespawn(PlayerRespawnEvent event) {
		if (MCMEPVP.GameStatus == 0) {
			MCMEPVP.CurrentLobby.onRespawn(event);
		} else {
			MCMEPVP.CurrentGame.onRespawn(event);
			if (!MCMEPVP.PVPGT.equals("RBR")) {
				event.getPlayer().setNoDamageTicks(140);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	void entityDetect(EntityDamageByEntityEvent event) {
		if (MCMEPVP.GameStatus == 0) {
			event.setCancelled(true);
		} else {
			Entity attacker = event.getDamager();
			if (attacker instanceof Arrow) {
				if (event.getEntity() instanceof Player
						&& ((Arrow) attacker).getShooter() instanceof Player) {
					arrowDamage(event);
				}
				return;
			}
			if (attacker instanceof Player) {
				if (event.getEntity() instanceof Player) {
					playerDamage(event);
				}
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	void arrowVelocityModifier(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (teamUtil.getPlayerTeam(p).equals("spectator")) {
				event.setCancelled(true);
			} else {
				if (playerStats.containsKey(p.getName())) {
					Entity a = event.getProjectile();
					Double mod = 1 + playerStats.get(p.getName()).get(1);
					a.setVelocity(a.getVelocity().multiply(mod));
				}
			}
		}
	}

	void arrowDamage(EntityDamageByEntityEvent event) {
		Arrow arrow = (Arrow) (Projectile) event.getDamager();
		Player att = (Player) arrow.getShooter();
		Player def = (Player) event.getEntity();
		if (teamUtil.getPlayerTeam(att).equals("spectator")) {
			event.setCancelled(true);
		}
		if (teamUtil.getPlayerTeam(def).equals("spectator")) {
			Vector velocity = arrow.getVelocity();

			Vector vec = MCMEPVP.Spawns.get("spectator");
			Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(),
					vec.getY() + getRandom(1, 10), vec.getZ());

			def.teleport(loc);
			def.sendMessage(ChatColor.LIGHT_PURPLE
					+ "Sorry, you were in the way!");

			Arrow newArrow = att.launchProjectile(Arrow.class);
			newArrow.setShooter(att);
			newArrow.setVelocity(velocity);
			newArrow.setBounce(false);

			event.setCancelled(true);
		} else {
			if (playerStats.containsKey(att.getName())) {
				Double mod = playerStats.get(att.getName()).get(1)
						- playerStats.get(def.getName()).get(2);

				Double damage = event.getDamage() + (event.getDamage() * mod);
				event.setDamage(damage);
			}
			MCMEPVP.CurrentGame.onPlayerShoot(event);
		}
	}

	void playerDamage(EntityDamageByEntityEvent event) {
		Player def = (Player) event.getEntity();
		Player att = (Player) event.getDamager();

		if (teamUtil.getPlayerTeam(def).equals("spectator")
				|| teamUtil.getPlayerTeam(att).equals("spectator")) {
			event.setCancelled(true);
		} else {
			if (playerStats.containsKey(att.getName())) {
				Double mod = playerStats.get(att.getName()).get(0)
						- playerStats.get(def.getName()).get(2);

				Double damage = event.getDamage() + (event.getDamage() * mod);
				event.setDamage(damage);
			}
			MCMEPVP.CurrentGame.onPlayerhit(event);
		}
	}

	private static int getRandom(int lower, int upper) {
		Random random = new Random();
		return random.nextInt((upper - lower) + 1) + lower;
	}
}
