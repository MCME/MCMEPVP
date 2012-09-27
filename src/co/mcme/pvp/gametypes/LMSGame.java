package co.mcme.pvp.gametypes;

import co.mcme.pvp.MCMEPVP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import co.mcme.pvp.Game;
import java.util.ArrayList;

public class LMSGame extends Game{
        public ArrayList<String> Fighters;
        public Plugin plugin;
        public int Protection;

    public LMSGame() {
        MCMEPVP.GameStatus = 1;
        Fighters = new ArrayList();
        Protection = 1;
        //Broadcast
	Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The next Game starts in a few seconds!");
	Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "GameType is "+ChatColor.DARK_PURPLE+"Last Man Standing"+ChatColor.GREEN+" on Map "+ChatColor.DARK_PURPLE+MCMEPVP.PVPMap+"!");
	Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "All Participants will be teleported to the Map!");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {
        public void run() {
            for (Player user : Bukkit.getOnlinePlayers()) {
                if (MCMEPVP.getPlayerStatus(user).equals("participant")){
                    MCMEPVP.setPlayerStatus(user, "fighter", ChatColor.DARK_GREEN);
                    Fighters.add(user.getName());
                    //heal
                    user.setHealth(20);
                    user.setFoodLevel(20);
                    //Give Weapons and Armour
                    PlayerInventory inv = user.getInventory();
                    inv.setItemInHand(new ItemStack(276));//Sword
                    inv.setChestplate(new ItemStack(311));//Armour
                    inv.setLeggings(new ItemStack(312));//Leggins
                    inv.setBoots(new ItemStack(313));//Boots
                    inv.setHelmet(new ItemStack(310));//Helmet
                    inv.addItem(new ItemStack(261),new ItemStack(262, 32));//Bow + Arrows
                }
                //Teleport User
                if (!MCMEPVP.getPlayerStatus(user).equals("builder")){
                    Vector vec = MCMEPVP.Spawns.get(MCMEPVP.getPlayerStatus(user));
                    Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
                    user.teleport(loc);
                }
            }
            //Broadcast
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {
            public void run() {
                Protection = 0;
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Protection is now off! The Fight begins!");
            }}, 300L);
        }}, 100L);
    }

	public void onPlayerjoinServer(PlayerLoginEvent event) {
                Vector vec = MCMEPVP.Spawns.get("spectator");
                Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
                event.getPlayer().teleport(loc);		
	}

	public void onPlayerleaveServer(PlayerQuitEvent event) {
		Fighters.remove(event.getPlayer().getName());
                checkGameEnd();
	}

	public void onPlayerdie(PlayerDeathEvent event) {
            Player player = event.getEntity();
            Fighters.remove(player.getName());
            event.setDeathMessage(ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + " is out of the Game!");
            event.getDrops().add(new ItemStack(364, 1));
            MCMEPVP.setPlayerStatus(event.getEntity(),"spectator", ChatColor.WHITE);
            checkGameEnd();		
	}

	public void onPlayerhit(EntityDamageByEntityEvent event) {
            if(Protection == 1){
                event.setCancelled(true);
                Player Damager = (Player) event.getDamager();
                Damager.sendMessage(ChatColor.DARK_RED + "Protection is still active!");
            }
	} 

    private void checkGameEnd() {
        if(Fighters.size() == 1){
            Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + Fighters.get(0)
                    + ChatColor.YELLOW + " won the fight as last man standing!");
            MCMEPVP.resetGame();
        }
    }
}