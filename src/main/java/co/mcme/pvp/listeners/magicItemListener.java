package co.mcme.pvp.listeners;

import static co.mcme.pvp.gametypes.ringBearerGame.ringBearers;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.armorColor;
import co.mcme.pvp.util.gearGiver;
import co.mcme.pvp.util.teamUtil;

public class magicItemListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void oneRing(PlayerInteractEvent event) {
		if (MCMEPVP.GameStatus == 1
                && (MCMEPVP.PVPGT.equals("RBR")
                || MCMEPVP.PVPGT.equals("FFA"))) {
			if ((event.hasItem())
					&& (event.getItem().getType().equals(Material.GOLD_NUGGET))) {
				if (MCMEPVP.PVPGT.equals("RBR")
						&& ringBearers.containsValue(event.getPlayer().getName())) {
					if (!(event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
							&& (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) 
									|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))))) {
						event.setCancelled(true);
						oneRingEffects(event);
					}
				} else {
					if (!MCMEPVP.PVPGT.equals("RBR")) {
						if (!(event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
								&& (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) 
										|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))))) {
							event.setCancelled(true);
							oneRingEffects(event);
						}
					}
				}
			}
			if (event.hasItem()
					&& event.getItem().getType().equals(Material.SUGAR)) {
				if (!(event.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
						&& (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event
								.getAction().equals(Action.RIGHT_CLICK_BLOCK))))) {
					event.setCancelled(true);
					sugarEffects(event);
				}
			}
            if (event.hasItem()
                    && event.getItem().getType().equals(Material.GHAST_TEAR)) {
                if (!(event.getPlayer()
                        .hasPotionEffect(PotionEffectType.REGENERATION))
                        && (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event
                        .getAction().equals(Action.RIGHT_CLICK_BLOCK))))) {
					event.setCancelled(true);
                    pipeEffects(event);
				}
			}
			if (event.hasItem()
					&& event.getItem().getType().equals(Material.NETHER_STAR)) {
				if (!(event.getPlayer()
						.hasPotionEffect(PotionEffectType.NIGHT_VISION))
						&& (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event
								.getAction().equals(Action.RIGHT_CLICK_BLOCK))))) {
					event.setCancelled(true);
					starEffects(event);
				}
			}
			if (event.hasItem()
					&& event.getItem().getType().equals(Material.RAW_FISH)) {
				if (!(event.getPlayer().hasPotionEffect(PotionEffectType.HEAL))
						&& (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event
								.getAction().equals(Action.RIGHT_CLICK_BLOCK))))) {
					event.setCancelled(true);
					fishEffects(event);
				}
			}
			if (event.hasItem()
					&& event.getItem().getType().equals(Material.COOKIE)) {
				if (!(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))
						&& (((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event
								.getAction().equals(Action.RIGHT_CLICK_BLOCK))))) {
					event.setCancelled(true);
					cookieEffects(event);
				}
			}
			if (event.hasItem()
					&& event.getItem().getType().equals(Material.FEATHER)) {
				event.setCancelled(true);
				featherEffects(event.getPlayer());
			}
		}
	}

	private void oneRingEffects(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		PlayerInventory i = p.getInventory();
		ItemStack is = i.getItemInHand();	
		int amnt = is.getAmount()-1;
		if(amnt>0){
			is.setAmount(amnt);
		}else{
			i.remove(is);
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300, 2));
		i.setBoots(new ItemStack(Material.AIR));
		i.setLeggings(new ItemStack(Material.AIR));
		i.setChestplate(new ItemStack(Material.AIR));
		i.setHelmet(new ItemStack(Material.AIR));
		p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
		p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1, 1);

		if (teamUtil.getPlayerTeam(p).equals("red")) {
			Color c = armorColor.RED;
			derp(p, c);
		}
		if (teamUtil.getPlayerTeam(p).equals("blue")) {
			Color c = armorColor.BLUE;
			derp(p, c);
		}
	}

	private void sugarEffects(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack is = p.getInventory().getItemInHand();	
		int amnt = is.getAmount()-1;
		if(amnt>0){
			is.setAmount(amnt);
		}else{
			p.getInventory().remove(is);
		}
		Location l = p.getLocation();
		p.playEffect(l, Effect.ENDER_SIGNAL, null);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
	}

	private void pipeEffects(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack is = p.getInventory().getItemInHand();	
		int amnt = is.getAmount()-1;
		if(amnt>0){
			is.setAmount(amnt);
		}else{
			p.getInventory().remove(is);
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 240, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 240, 0));
	}

	private void starEffects(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack is = p.getInventory().getItemInHand();	
		int amnt = is.getAmount()-1;
		if(amnt>0){
			is.setAmount(amnt);
		}else{
			p.getInventory().remove(is);
		}
		Location l = p.getLocation();
		p.playEffect(l, Effect.ENDER_SIGNAL, null);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000,
				0));
	}

	private void fishEffects(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack is = p.getInventory().getItemInHand();	
		int amnt = is.getAmount()-1;
		if(amnt>0){
			is.setAmount(amnt);
		}else{
			p.getInventory().remove(is);
		}
		Location l = p.getLocation();
		p.playEffect(l, Effect.ENDER_SIGNAL, null);
		p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 2, 0));
	}
	
	private void cookieEffects(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack is = p.getInventory().getItemInHand();	
		int amnt = is.getAmount()-1;
		if(amnt>0){
			is.setAmount(amnt);
		}else{
			p.getInventory().remove(is);
		}
		Location l = p.getLocation();
		p.playEffect(l, Effect.ENDER_SIGNAL, null);
		for(Entity e : p.getNearbyEntities(30, 30, 30)){
			if(e instanceof Player){
				Player pl = ((Player) e).getPlayer();
				if(!pl.equals(p)){
					pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 3));
					pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 10));
					pl.playSound(pl.getLocation(), Sound.WITHER_SPAWN, (float) 0.5, 1);
				}
			}
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 3));
		p.playSound(p.getLocation(), Sound.WITHER_SPAWN, (float) 0.5, 1);
	}
	
	public void featherEffects(Player p){
		for(Entity e : p.getNearbyEntities(30, 30, 30)){
			if (e instanceof Player) {
				if (!teamUtil.getPlayerTeam(((Player) e).getPlayer()).equals("spectator")) {
					p.teleport(e);
					e.setPassenger(p);
					p.sendMessage(MCMEPVP.positivecolor + "Saddled " + ((Player) e).getPlayer().getName());
					return;
				}
			}
		}
	}

	public void derp(final Player p, final Color c) {
		Bukkit.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(
						Bukkit.getPluginManager().getPlugin("MCMEPVP"),
						new Runnable() {
							@Override
							public void run() {
								gearGiver.giveArmor(p, c);
							}
						}, 300L);
	}
}
