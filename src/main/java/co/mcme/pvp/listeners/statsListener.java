package co.mcme.pvp.listeners;

import static co.mcme.pvp.MCMEPVP.CurrentMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.itemUtils;

public class statsListener implements Listener{
	
	public static HashMap<String, List<Double>> playerStats = new HashMap<String, List<Double>>();
	
	@EventHandler
	public static void openStats(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(MCMEPVP.GameStatus == 1){
			if((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
					|| event.getAction().equals(Action.RIGHT_CLICK_AIR))
					&& event.hasItem()
					&& p.getItemInHand().getType().equals(Material.BOOK) ){
				if(MCMEPVP.CurrentGame.allowCustomAttributes()){
					Location red = CurrentMap.getMapMeta().getSpawn("red").toLocation();
					Location blue = CurrentMap.getMapMeta().getSpawn("blue").toLocation();
					if(event.getPlayer().getLocation().distance(red) <= (double)25
							|| event.getPlayer().getLocation().distance(blue) <= (double)25){
						event.setCancelled(true);
						statsScreen(p);
					} else {
						p.sendMessage(MCMEPVP.negativecolor + "You are too far from your team's spawn to use that!");
					}
				} else {
					event.setCancelled(true);
					p.sendMessage(MCMEPVP.primarycolor+"Custom attributes are disabled for this GameType!");
					stripStats(p);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void statsChange(InventoryClickEvent event){
		if(MCMEPVP.GameStatus == 1){
			if(event.getInventory().getTitle().equals(ChatColor.GOLD+"PVP Attributes")){
				if(event.getAction().equals(InventoryAction.PICKUP_ALL)
						|| event.getAction().equals(InventoryAction.PICKUP_HALF) 
						|| event.getAction().equals(InventoryAction.PICKUP_ONE)
						|| event.getAction().equals(InventoryAction.PICKUP_SOME)){
					if(event.getSlot()==0 || event.getSlot() == 8
							|| event.getSlot() == 9 || event.getSlot() == 17){
						event.setCancelled(true);
					}
				} else if(event.getAction().equals(InventoryAction.PLACE_SOME)
						|| event.getAction().equals(InventoryAction.PLACE_ALL)
						|| event.getAction().equals(InventoryAction.PLACE_ONE)){
					DecimalFormat df = new DecimalFormat("#.##");
					if(event.getCursor().getType().equals(Material.EYE_OF_ENDER)){
						if(event.getSlot()<9 && !event.getSlotType().equals(SlotType.QUICKBAR)){
							Player p = (Player) event.getWhoClicked();
							
							Double exp = Double.valueOf(df.format(Double.valueOf(event.getSlot())/8));
							
							// Bow Modifier
							Double bm = (Double) (exp - 0.5);
							
							// Sword Modifier
							Double sm = (Double) (1 - exp - 0.5);
							
							List<Double> modifiers = playerStats.get(p.getName());
							modifiers.set(0, sm);
							modifiers.set(1, bm);
							playerStats.put(p.getName(), modifiers);
							
							p.sendMessage(ChatColor.AQUA+"Sword Modifier: " + ChatColor.LIGHT_PURPLE + df.format(sm));
							p.sendMessage(ChatColor.AQUA+"Bow Modifier: " + ChatColor.LIGHT_PURPLE + df.format(bm));
						} else {
							event.setCancelled(true);
						}
					}
					// Armor
					if(event.getCursor().getType().equals(Material.EYE_OF_ENDER)){
						if((event.getSlot()>9 && event.getSlot()<17) && !event.getSlotType().equals(SlotType.QUICKBAR)){
							Player p = (Player) event.getWhoClicked();
							
							Float slot = Float.valueOf(event.getSlot());
							Double arm = (double) (((slot - 9)/8) - 0.5);
							
							Float speed = (float) (0.2*(13/slot));
							if(slot > 13){
								speed = (float) (0.2*((13/slot)*(13/slot)));
							} 
							if (slot < 13) {
								arm = arm + 0.06;
								speed = (float) (speed + 0.1);
							}
							
							List<Double> mods = playerStats.get(p.getName());
							mods.set(2, arm);
							playerStats.put(p.getName(), mods);
							
							p.setWalkSpeed(speed);
							
							p.sendMessage(ChatColor.AQUA+"Speed Modifier: " + ChatColor.LIGHT_PURPLE + df.format(speed));
							p.sendMessage(ChatColor.AQUA+"Armor Modifier: " + ChatColor.LIGHT_PURPLE + df.format(arm));
						} else {
							event.setCancelled(true);
						}
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void statsClose(InventoryCloseEvent event){
		if(MCMEPVP.GameStatus==1){
			if(event.getInventory().getTitle().equals(ChatColor.GOLD+"PVP Attributes")){
				if(event.getPlayer().getInventory().contains(Material.EYE_OF_ENDER)){
					event.getPlayer().getInventory().remove(Material.EYE_OF_ENDER);
				}
			}
		}
	}
	
	@EventHandler
	public void dropItem(PlayerDropItemEvent event){
		if(event.getItemDrop().getType().equals(Material.EYE_OF_ENDER)
				|| event.getItemDrop().getType().equals(Material.EYE_OF_ENDER)){
			event.setCancelled(true);
		}
	}
	
	public static void statsScreen(Player p){
		stripStats(p);
		p.setExp((float) 0.5);
		
		Inventory stats = p.getServer().createInventory(p, (18), ChatColor.GOLD+"PVP Attributes");
		
		stats.setItem(0, itemUtils.nameItem(new ItemStack(Material.IRON_SWORD), "Increases sword damage", "Reduces bow damage", ChatColor.DARK_PURPLE));
		stats.setItem(4, itemUtils.nameItem(new ItemStack(Material.EYE_OF_ENDER), "Sword vs Bow", "none", ChatColor.DARK_AQUA));
		stats.setItem(8, itemUtils.nameItem(new ItemStack(Material.BOW), "Increases bow damage", "Reduces sword damage", ChatColor.DARK_PURPLE));
		
		stats.setItem(9, itemUtils.nameItem(new ItemStack(Material.LEATHER_BOOTS), "Increases sprint speed", "Reduces armor strength", ChatColor.DARK_PURPLE));
		stats.setItem(13, itemUtils.nameItem(new ItemStack(Material.EYE_OF_ENDER), "Speed vs Armor", "none", ChatColor.DARK_AQUA));
		stats.setItem(17, itemUtils.nameItem(new ItemStack(Material.IRON_CHESTPLATE), "Increases armor strength", "Reduces sprint speed", ChatColor.DARK_PURPLE));

		p.openInventory(stats);	
	}
	
	public static void stripStats(Player p){
		String pn = p.getName();
		p.setExp((float) 0);
		p.setWalkSpeed((float) 0.2);
		if (playerStats.containsKey(pn)) {
			List<Double> defaults = playerStats.get(pn);
			defaults.clear();
			defaults.add(0, (double) 0);
			defaults.add(1, (double) 0);
			defaults.add(2, (double) 0);
			playerStats.put(pn, defaults);
		} else {
			List<Double> defaults = new ArrayList<Double>();
			defaults.add(0, (double) 0);
			defaults.add(1, (double) 0);
			defaults.add(2, (double) 0);
			playerStats.put(pn, defaults);
		}
		p.sendMessage(ChatColor.AQUA + "Stats reset to defaults!");
	}
	
}
