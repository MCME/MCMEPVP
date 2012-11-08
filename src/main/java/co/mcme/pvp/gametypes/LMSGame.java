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
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import co.mcme.pvp.Game;
import co.mcme.pvp.util.GearGiver;

public class LMSGame extends Game {

    private int Fighters = 0;
    public Plugin plugin;
    public int Protection;

    public LMSGame() {
        MCMEPVP.GameStatus = 1;
        Protection = 1;
        //Broadcast
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The next Game starts in a few seconds!");
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "GameType is " + ChatColor.DARK_PURPLE + "Last Man Standing" + ChatColor.GREEN + " on Map " + ChatColor.DARK_PURPLE + MCMEPVP.PVPMap + "!");
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "All Participants will be teleported to the Map!");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {

            public void run() {
                for (Player user : Bukkit.getOnlinePlayers()) {
                    Fighters++;
                    if (MCMEPVP.getPlayerStatus(user).equals("participant")) {
                        MCMEPVP.setPlayerStatus(user, "fighter", ChatColor.DARK_GREEN);
                        //heal
                        user.setHealth(20);
                        user.setFoodLevel(20);
                        //Give Weapons and Armour
                        giveGear(user);
                    }
                    //Teleport User
                    Vector vec = MCMEPVP.Spawns.get("fighter");
                    Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
                    user.teleport(loc);
                }
                //Broadcast
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {

                    public void run() {
                        Protection = 0;
                        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Protection is now off! The Fight begins!");
                    }
                }, 100L);
            }
        }, 100L);
    }

    protected void giveGear(Player player) {
        GearGiver.giveArmor(player, "uncolored");
        GearGiver.giveWeapons(player, "uncolored", "warrior");
    }

    @Override
    public void onPlayerjoinServer(PlayerLoginEvent event) {
        Vector vec = MCMEPVP.Spawns.get("spectator");
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
        event.getPlayer().teleport(loc);
    }

    @Override
    public void onPlayerleaveServer(PlayerQuitEvent event) {
        Fighters--;
        checkGameEnd();
    }

    @Override
    public void onPlayerdie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String Status = MCMEPVP.getPlayerStatus(player);
        if (Status.equals("fighter")) {
            event.setDeathMessage(ChatColor.DARK_GREEN + player.getName() + " is out of the Game!");
            event.getDrops().add(new ItemStack(364, 1));
            Fighters--;
            MCMEPVP.setPlayerStatus(player, "spectator", ChatColor.WHITE);
            checkGameEnd();
        }
    }

    @Override
    public void onPlayerhit(EntityDamageByEntityEvent event) {
    }

    @Override
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
    }

    private void checkGameEnd() {
        if (Fighters == 1) {
            for (Player user : Bukkit.getOnlinePlayers()) {
                if (MCMEPVP.getPlayerStatus(user).equals("fighter")) {
                    Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + user.getName() + ChatColor.GREEN + " wins the Fight as last man standing!");
                }
            }
            MCMEPVP.resetGame();
        }
    }
}