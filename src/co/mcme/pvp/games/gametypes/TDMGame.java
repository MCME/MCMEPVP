package co.mcme.pvp.games.gametypes;

import java.util.HashMap;

import co.mcme.pvp.games.GameType;
import co.mcme.pvp.MCMEPVP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import co.mcme.pvp.games.Game;

public class TDMGame extends Game {

    public GameType GameType;
    private int RedMates = 0;
    private int BlueMates = 0;
    private HashMap<String, Vector> Spawns;

    public TDMGame(GameType gt) {
        super(gt);
        MCMEPVP.GameStatus = 1;
        //Broadcast
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The next Game starts in a few seconds! GameType is TDM!");
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "All Participants will be assigned to a team and teleported to their spawn!");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin) this, new Runnable() {

            public void run() {
                for (Player user : Bukkit.getOnlinePlayers()) {
                    //TODO team assigning more random
                    //Assign Teams
                    if (MCMEPVP.PlayerStatus.get(user.getName()) == "participant") {
                        if (BlueMates > RedMates) {
                            addTeam(user, "red");
                        } else {
                            if (BlueMates < RedMates) {
                                addTeam(user, "blue");
                            } else {
                                boolean random = (Math.random() < 0.5);
                                if (random == true) {
                                    addTeam(user, "red");
                                } else {
                                    addTeam(user, "blue");
                                }
                            }
                        }
                        //heal
                        user.setHealth(20);
                        user.setFoodLevel(20);
                        user.setSaturation(20); //Make sure they are at full saturation!
                        //Give Weapons and Armour
                        PlayerInventory inv = user.getInventory();
                        inv.setItemInHand(new ItemStack(276));//Sword
                        inv.setChestplate(new ItemStack(311));//Armour
                        inv.setLeggings(new ItemStack(312));//Leggins
                        inv.setBoots(new ItemStack(313));//Boots
                        inv.addItem(new ItemStack(261), new ItemStack(262, 32));//Bow + Arrows
                    }
                    //Teleport User
                    Vector vec = Spawns.get(MCMEPVP.PlayerStatus.get(user.getName()));
                    World world = Bukkit.getWorld("pvp");
                    Location loc = new Location(world, vec.getX(), vec.getY(), vec.getZ());
                    user.teleport(loc);
                }
                //Broadcast
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The Fight begins!");
            }
        }, 100L);
    }

    protected void addTeam(Player player, String Team) {
        player.sendMessage(ChatColor.YELLOW + "You're now in Team " + Team.toUpperCase() + "!");
        if (Team == "red") {
            RedMates++;
            MCMEPVP.setPlayerStatus(player, Team, ChatColor.RED);
            player.getInventory().setHelmet(new ItemStack(35, 1, (short) 0, (byte) 14));
        } else {
            if (Team == "blue") {
                BlueMates++;
                MCMEPVP.setPlayerStatus(player, Team, ChatColor.BLUE);
                player.getInventory().setHelmet(new ItemStack(35, 1, (short) 0, (byte) 11));
            }
        }
    }

    public void onPlayerjoinServer(PlayerLoginEvent event) {
        // TODO Auto-generated method stub
    }

    public void onPlayerleaveServer(PlayerQuitEvent event) {
        String OldTeam = MCMEPVP.getPlayerStatus(event.getPlayer());
        if (OldTeam == "red") {
            RedMates--;
        } else {
            if (OldTeam == "blue") {
                BlueMates--;
            } else {
                //Error
            }
        }
    }

    public void onPlayerdie(PlayerDeathEvent event) {
        // TODO Auto-generated method stub
    }

    public void onPlayerhit(EntityDamageByEntityEvent event) {
        // TODO Auto-generated method stub
    }

    public void onAdminset(String[] args) {
        // TODO Auto-generated method stub
    }
}