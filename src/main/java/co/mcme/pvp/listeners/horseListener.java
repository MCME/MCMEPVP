package co.mcme.pvp.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.teamUtil;

public class horseListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void leadClick(PlayerInteractEvent event) {
		if((event.getAction().equals(Action.RIGHT_CLICK_AIR)
				|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) ){
			if(!teamUtil.getPlayerTeam(event.getPlayer()).equals("spectator") && event.getPlayer().getItemInHand().getTypeId()==420){
				event.setCancelled(true);
				Player p = event.getPlayer();
				
				removeLeash(event);
				
				Location l = p.getLocation();
				World w = p.getWorld();
				
				Horse horsey = w.spawn(l, Horse.class);
				horsey.setAdult();
				
				Variant var = Horse.Variant.HORSE;

				//disabled (just give horses)
				boolean disabled = true;
				if(disabled){
					if(MCMEPVP.PVPGT.equals("FFA")){
						var.equals(randomVariant());
					}else{
						if(teamUtil.getPlayerTeam(p).equals("red")){
							var = Horse.Variant.SKELETON_HORSE;
						}
						if(teamUtil.getPlayerTeam(p).equals("blue")){
							var.equals(Horse.Variant.HORSE);
						} else {
							var.equals(randomVariant());
						}
					}
				}
				//disabled
				
				horsey.setVariant(var);
				
				if(var.equals(Horse.Variant.HORSE)){
					horsey.setColor(randomColor());
					horsey.setStyle(randomStyle());
				}
				
				horsey.setTamed(true);
				horsey.getInventory().setSaddle(new ItemStack(Material.SADDLE));
				horsey.setHealth(40);
				horsey.setOwner(p);
				horsey.setPassenger(p);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void teamDamage(EntityDamageByEntityEvent event){
		Entity victim = event.getEntity();
		Entity damager = event.getDamager();
		if(victim.getType().equals(EntityType.HORSE) && !MCMEPVP.PVPGT.equals("FFA")){
			if(damager instanceof Player && victim.getPassenger() instanceof Player){
				Player att = ((Player) damager).getPlayer();
				Player rider = (Player) victim.getPassenger();
				if(teamUtil.getPlayerTeam(att).equals(teamUtil.getPlayerTeam(rider))
						|| teamUtil.getPlayerTeam(att).equals("spectator")){
					event.setCancelled(true);
				}
			}
			if(damager instanceof Arrow && victim.getPassenger() instanceof Player){
				Arrow arrow = (Arrow) damager; 
				Player att = (Player) arrow.getShooter();
				Player rider = (Player) victim.getPassenger();
				if(teamUtil.getPlayerTeam(att).equals(teamUtil.getPlayerTeam(rider))
						|| teamUtil.getPlayerTeam(att).equals("spectator")){
					event.setCancelled(true);
					arrow.setBounce(false);
				}
			}
		}	
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void horseDrops(EntityDeathEvent event){
		if(event.getEntity() instanceof Horse){
			Horse h = (Horse) event.getEntity();
			h.getInventory().clear();
			event.getDrops().clear();
		}
	}
	
	private void removeLeash(PlayerInteractEvent event) {
		if(MCMEPVP.GameStatus==1){
			Player p = (Player) event.getPlayer();
			ItemStack is = p.getInventory().getItemInHand();	
			int amnt = is.getAmount()-1;
			if(amnt>0){
				is.setAmount(amnt);
			}else{
				p.getInventory().remove(is);
			}
		}
	} 
	
	@EventHandler(priority = EventPriority.HIGH)
	public void horseDismount(VehicleExitEvent event){
		event.getVehicle().remove();
	}
	
	public Variant randomVariant(){
		List<Variant> variants = new ArrayList<Variant>();
		for(Variant var : Horse.Variant.values()){
			variants.add(var);
		}
		Collections.shuffle(variants);
		return variants.get(0);
	}
	
	public Color randomColor(){
		List<Color> colors = new ArrayList<Color>();
		for(Color col : Horse.Color.values()){
			colors.add(col);
		}
		Collections.shuffle(colors);
		return colors.get(0);
	}
	
	public Style randomStyle(){
		List<Style> styles = new ArrayList<Style>();
		for(Style st : Horse.Style.values()){
			styles.add(st);
		}
		Collections.shuffle(styles);
		return styles.get(0);
	}

}
