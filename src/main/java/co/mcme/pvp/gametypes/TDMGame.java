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
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;

public class TDMGame extends Game {

    private int RedMates = 0;
    private int BlueMates = 0;
    public Plugin plugin;

    public TDMGame() {
        MCMEPVP.GameStatus = 1;
        //Broadcast
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The next Game starts in a few seconds!");
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "GameType is " + ChatColor.DARK_PURPLE + "Team Deathmatch" + ChatColor.GREEN + " on Map " + ChatColor.DARK_PURPLE + MCMEPVP.PVPMap + "!");
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "All Participants will be assigned to a team and teleported to their spawn!");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {
            public void run() {
                for (Player user : Bukkit.getOnlinePlayers()) {
                    //TODO team assigning more random
                    //Assign Teams
                    if (MCMEPVP.getPlayerStatus(user).equals("participant")) {
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
                    }
                    //Teleport User
                    Vector vec = MCMEPVP.Spawns.get(MCMEPVP.getPlayerStatus(user));
                    Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
                    user.teleport(loc);
                }
                //Broadcast
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "The Fight begins!");
            }
        }, 100L);
    }

    protected void addTeam(Player player, String Team) {
        if (Team.equals("red")) {
            player.sendMessage(ChatColor.YELLOW + "You're now in Team " + ChatColor.RED + "RED" + ChatColor.YELLOW + "!");
            RedMates++;
            MCMEPVP.setPlayerStatus(player, Team, ChatColor.RED);
            GearGiver.giveArmor(player, "red");
            GearGiver.giveWeapons(player, "red", "warrior");
            if (MCMEPVP.PVPMap.equalsIgnoreCase("tharbad")) {
                GearGiver.giveExtras(player, Team, "boating");
            }
            player.setGameMode(GameMode.ADVENTURE);
        } else if (Team.equals("blue")) {
            player.sendMessage(ChatColor.YELLOW + "You're now in Team " + ChatColor.BLUE + "BLUE" + ChatColor.YELLOW + "!");
            BlueMates++;
            MCMEPVP.setPlayerStatus(player, Team, ChatColor.BLUE);
            GearGiver.giveArmor(player, "blue");
            GearGiver.giveWeapons(player, "blue", "warrior");
            if (MCMEPVP.PVPMap.equalsIgnoreCase("tharbad")) {
                GearGiver.giveExtras(player, Team, "boating");
            }
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    @Override
    public void onPlayerjoinServer(PlayerLoginEvent event) {
        Vector vec = MCMEPVP.Spawns.get("spectator");
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
        event.getPlayer().teleport(MCMEPVP.Spawn);

    }

    @Override
    public void onPlayerleaveServer(PlayerQuitEvent event) {
        String OldTeam = MCMEPVP.getPlayerStatus(event.getPlayer());
        if (OldTeam.equals("red")) {
            RedMates--;
        }
        if (OldTeam.equals("blue")) {
            BlueMates--;
        } else {
            //Error
        }
        checkGameEnd();
    }

    @Override
    public void onPlayerdie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String Status = MCMEPVP.getPlayerStatus(player);
        //TODO Log deaths
        if (Status.equals("spectator")) {
            event.setDeathMessage(ChatColor.YELLOW + "Spectator " + player.getName() + " was tired watching this fight!");
        }
        if (Status.equals("red")) {
            RedMates--;
            event.setDeathMessage(ChatColor.RED + "Team Red " + ChatColor.YELLOW + "lost " + player.getName());
            event.getDrops().add(new ItemStack(364, 1));
        }
        if (Status.equals("blue")) {
            BlueMates--;
            event.setDeathMessage(ChatColor.BLUE + "Team Blue " + ChatColor.YELLOW + "lost " + player.getName());
            event.getDrops().add(new ItemStack(364, 1));
        }
        MCMEPVP.setPlayerStatus(event.getEntity(), "spectator", ChatColor.WHITE);
        checkGameEnd();
    }

    @Override
    public void onPlayerhit(EntityDamageByEntityEvent event) {
        Player defender = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        if (MCMEPVP.getPlayerStatus(attacker).equals(MCMEPVP.getPlayerStatus(defender))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        Player defender = (Player) event.getEntity();
        Player attacker = (Player) ((Projectile) event.getDamager()).getShooter();
        if (MCMEPVP.getPlayerStatus(defender).equals(MCMEPVP.getPlayerStatus(attacker))) {
            event.setCancelled(true);
            attacker.playSound(attacker.getLocation(), Sound.ZOMBIE_REMEDY, (float) 10, (float) 50);
            attacker.sendMessage(ChatColor.DARK_RED + "You can't shoot your teammates!");
        }
    }

    private void checkGameEnd() {
        if (BlueMates == 0) {
            Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Team "
                    + ChatColor.RED + "Red" + ChatColor.GREEN + " wins!");
            MCMEPVP.resetGame();
        } else if (RedMates == 0) {
            Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Team "
                    + ChatColor.BLUE + "Blue" + ChatColor.GREEN + " wins!");
            MCMEPVP.resetGame();
        }
    }
}