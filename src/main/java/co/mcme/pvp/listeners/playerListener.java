package co.mcme.pvp.listeners;

import static co.mcme.pvp.MCMEPVP.PVPGT;
import static co.mcme.pvp.gametypes.ringBearerGame.ringBearers;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.textureSwitcher;

public class playerListener implements Listener {

    public playerListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerJoin(PlayerLoginEvent event) {
        if (MCMEPVP.locked) {
            if (event.getPlayer().hasPermission("mcmepvp.ignorelock")) {
                //Let player login
            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "MCMEPVP is only available when a game is running. Come Back Soon!");
            }
            //disconnect player and send message
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("mcmepvp.admin")){
            if (MCMEPVP.gameDebug){
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
                event.getPlayer().sendMessage(MCMEPVP.highlightcolor + "Debug mode is enabled!");
                event.getPlayer().sendMessage(MCMEPVP.highlightcolor + "Stats are not being recorded!");
            }
        }
        if (MCMEPVP.GameStatus == 0) {
            event.getPlayer().teleport(MCMEPVP.Spawn);
            textureSwitcher.switchTP(event.getPlayer());
            spectatorUtil.showAll(event.getPlayer());
            
            MCMEPVP.CurrentLobby.onPlayerJoin(event);
        } else {
            MCMEPVP.CurrentGame.onPlayerJoin(event);
            textureSwitcher.switchTP(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void zanBlock(PlayerJoinEvent event) {
    	event.getPlayer().sendMessage("§3 §6 §3 §6 §3 §6 §e");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void inventory(InventoryCloseEvent event) {
        if (PVPGT.equals("RBR") && (ringBearers.size() > 0)) {
            Player p = (Player) event.getPlayer();
            if (ringBearers.containsKey(p) && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                if (p.getInventory().contains(Material.GLOWSTONE)) {
                    p.getInventory().remove(Material.GLOWSTONE);
                }
                p.getInventory().setHelmet(new ItemStack(Material.GLOWSTONE, 1));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void dropItem(PlayerDropItemEvent event) {
        if (PVPGT.equals("RBR") && (ringBearers.size() > 0)) {
            if (event.getItemDrop().getItemStack().getType().equals(Material.GLOWSTONE)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void itemPickup(PlayerPickupItemEvent event) {
        if(teamUtil.getPlayerTeam(event.getPlayer()).equals("red")
        		|| teamUtil.getPlayerTeam(event.getPlayer()).equals("blue")
        		|| event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
        	event.setCancelled(false);
        }else{
        	event.setCancelled(true);
        } 
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerLeave(PlayerQuitEvent event) {
        if (MCMEPVP.GameStatus == 0) {
            MCMEPVP.unQueuePlayer(event.getPlayer());
            
            MCMEPVP.CurrentLobby.onPlayerleaveServer(event);
        } else {
            MCMEPVP.CurrentGame.onPlayerleaveServer(event);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    void onGamemodeChange(PlayerGameModeChangeEvent event){
        if (teamUtil.getPlayerTeam(event.getPlayer()).equalsIgnoreCase("spectator")){
            event.getPlayer().setAllowFlight(true);
        }
    }
}