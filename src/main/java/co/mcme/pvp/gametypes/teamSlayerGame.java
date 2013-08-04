package co.mcme.pvp.gametypes;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.util.armorColor;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.gearGiver;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.textureSwitcher;
import co.mcme.pvp.util.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class teamSlayerGame extends gameType {

    private int RedMates = 0;
    private int RedScore = 0;
    private int BlueMates = 0;
    private int BlueScore = 0;
    public Plugin plugin;
    private HashMap<String, String> playing = new HashMap<String, String>();
    boolean isTharbad = MCMEPVP.PVPMap.equalsIgnoreCase("tharbad");
    ScoreboardManager manager;
    Scoreboard board;
    Team redteam;
    Team blueteam;
    Objective objective;
    OfflinePlayer dummyred = Bukkit.getOfflinePlayer(ChatColor.RED + "Red:");
    OfflinePlayer dummyblue = Bukkit.getOfflinePlayer(ChatColor.BLUE + "Blue:");
    Score redscore;
    Score bluescore;

    public teamSlayerGame() {
        MCMEPVP.GameStatus = 1;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        redteam = board.registerNewTeam("Red Team");
        redteam.setPrefix(ChatColor.RED.toString());
        blueteam = board.registerNewTeam("Blue Team");
        blueteam.setPrefix(ChatColor.BLUE.toString());
        objective = board.registerNewObjective("Score: "+config.TSLscore, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        redscore = objective.getScore(dummyred);
        bluescore = objective.getScore(dummyblue);
        //Broadcast
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "The next Game starts in a few seconds!");
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "GameType is " + MCMEPVP.highlightcolor + "Team Slayer" + MCMEPVP.primarycolor + " on Map " + MCMEPVP.highlightcolor + MCMEPVP.PVPMap + "!");
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "The first team to " + MCMEPVP.highlightcolor + config.TSLscore + MCMEPVP.primarycolor + " points wins!");
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "All Participants will be assigned to a team and teleported to their spawn!");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {
            public void run() {
                ArrayList<Player> queued = new ArrayList<Player>();
                MCMEPVP.queue.drainTo(queued);
                Collections.shuffle(queued);
                for (Player p : queued) {
                	textureSwitcher.switchTP(p);
                    if (p.isOnline()) {
                        if (BlueMates > RedMates) {
                            addTeam(p, "red");
                        } else {
                            if (BlueMates < RedMates) {
                                addTeam(p, "blue");
                            } else {
                                boolean random = (Math.random() < 0.5);
                                if (random == true) {
                                    addTeam(p, "red");
                                } else {
                                    addTeam(p, "blue");
                                }
                            }
                        }
                        MCMEPVP.queue.remove(p);
                    } else {
                        MCMEPVP.queue.remove(p);
                        util.debug("Player `" + p.getName() + "` is not online!");
                    }
                }
                spectatorUtil.startingSpectators();
                //Broadcast
                Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "The Fight begins!");
            }
        }, 100L);
        redscore.setScore(RedScore);
        bluescore.setScore(BlueScore);
        displayBoard();
    }

    @Override
    public void addPlayerDuringGame(Player p) {
        if (BlueMates > RedMates) {
            addTeam(p, "red");
        } else {
            if (BlueMates < RedMates) {
                addTeam(p, "blue");
            } else {
                boolean random = (Math.random() < 0.5);
                if (random == true) {
                    addTeam(p, "red");
                } else {
                    addTeam(p, "blue");
                }
            }
        }
    }

    @Override
    public void addTeam(Player player, String Team) {
        Color col = armorColor.WHITE;
        switch (Team) {
            case "red":
                player.getInventory().clear();
                player.sendMessage(MCMEPVP.primarycolor + "You're now in Team " + ChatColor.RED + "RED" + MCMEPVP.primarycolor + "!");
                RedMates++;
                teamUtil.setPlayerTeam(player, Team);
                redteam.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
                col = armorColor.RED;
                break;
            case "blue":
                player.getInventory().clear();
                player.sendMessage(MCMEPVP.primarycolor + "You're now in Team " + ChatColor.BLUE + "BLUE" + MCMEPVP.primarycolor + "!");
                BlueMates++;
                teamUtil.setPlayerTeam(player, Team);
                blueteam.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
                col = armorColor.BLUE;
                break;
        }
        redscore.setScore(RedScore);
        bluescore.setScore(BlueScore);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation((float) 20);
        gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Team);
        playing.put(player.getName(), Team);
        Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5, vec.getZ());
        player.teleport(loc);
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (MCMEPVP.GameStatus == 1) {
            Player player = event.getPlayer();
            String Team = teamUtil.getPlayerTeam(player);
            if (Team.equals("red")) {
                addTeam(player, "red");
                spectatorUtil.setParticipant(player);
                RedMates++;
            }
            if (Team.equals("blue")) {
                addTeam(player, "blue");
                spectatorUtil.setParticipant(player);
                BlueMates++;
            }
            if (Team.equals("spectator")){
            	 spectatorUtil.setSpectator(player);
            }
            Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
            Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5, vec.getZ());
            player.teleport(loc);
        }
        displayBoard();
    }

    @Override
    public void onPlayerleaveServer(PlayerQuitEvent event) {
        String Team = teamUtil.getPlayerTeam(event.getPlayer());
        if (Team.equals("red")) {
            RedMates--;
        }
        if (Team.equals("blue")) {
            BlueMates--;
        }
        checkGameEnd();
    }

    @Override
    public void onPlayerdie(PlayerDeathEvent event) {
        MCMEPVP.logKill(event);
        Player player = event.getEntity();
        String Status = teamUtil.getPlayerTeam(player);
        Color col;
        if (player.getKiller() instanceof Player) {
            if (Status.equals("spectator")) {
                event.setDeathMessage(MCMEPVP.primarycolor + "Spectator " + player.getName() + " was tired watching this fight!");
            }
            if (Status.equals("red")) {
                BlueScore++;
                event.setDeathMessage(ChatColor.RED + player.getName() + MCMEPVP.primarycolor + " was killed by " + ChatColor.BLUE + player.getKiller().getName());
                bluescore.setScore(BlueScore);
                event.getDrops().add(new ItemStack(364, 1));
                event.getDrops().add(new ItemStack(262, 8));
                col = armorColor.RED;
                gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Status);
            }
            if (Status.equals("blue")) {
                RedScore++;
                event.setDeathMessage(ChatColor.BLUE + player.getName() + MCMEPVP.primarycolor + " was killed by " + ChatColor.RED + player.getKiller().getName());
                redscore.setScore(RedScore);
                event.getDrops().add(new ItemStack(364, 1));
                event.getDrops().add(new ItemStack(262, 8));
                col = armorColor.BLUE;
                gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Status);
            }
        } else {
            if (Status.equals("spectator")) {
                event.setDeathMessage(MCMEPVP.primarycolor + "Spectator " + player.getName() + " was tired watching this fight!");
            }
            if (Status.equals("red")) {
                BlueScore++;
                event.setDeathMessage(ChatColor.RED + player.getName() + MCMEPVP.primarycolor + " was lost in battle");
                bluescore.setScore(BlueScore);
                event.getDrops().add(new ItemStack(364, 1));
                event.getDrops().add(new ItemStack(262, 8));
                col = armorColor.RED;
                gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Status);
            }
            if (Status.equals("blue")) {
                RedScore++;
                event.setDeathMessage(ChatColor.BLUE + player.getName() + MCMEPVP.primarycolor + " was lost in battle");
                redscore.setScore(RedScore);
                event.getDrops().add(new ItemStack(364, 1));
                event.getDrops().add(new ItemStack(262, 8));
                col = armorColor.BLUE;
                gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Status);
            }
        }
        checkGameEnd();
    }

    @Override
    public void onPlayerhit(EntityDamageByEntityEvent event) {
        Player defender = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        String attackerteam = teamUtil.getPlayerTeam(attacker);
        String defenderteam = teamUtil.getPlayerTeam(defender);
        if (attackerteam.equals(defenderteam)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        Player defender = (Player) event.getEntity();
        Player attacker = (Player) ((Projectile) event.getDamager()).getShooter();
        String attackerteam = teamUtil.getPlayerTeam(attacker);
        String defenderteam = teamUtil.getPlayerTeam(defender);
        if (attackerteam.equals(defenderteam)) {
            event.setCancelled(true);
        } else if (!attackerteam.equals(defenderteam)) {
            attacker.playSound(attacker.getLocation(), Sound.ORB_PICKUP, (float) 20, (float) 50);
        }
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String Status = teamUtil.getPlayerTeam(player);
        Color col;
        if (Status.equals("red")) {
            col = armorColor.RED;
            gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Status);
            Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
            Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5, vec.getZ());
            event.setRespawnLocation(loc);
        }
        if (Status.equals("blue")) {
            col = armorColor.BLUE;
            gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Status);
            Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
            Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5, vec.getZ());
            event.setRespawnLocation(loc);
        }

    }

    private void checkGameEnd() {
    	objective.setDisplayName("Score: "+config.TSLscore);
        if (RedMates < 1) {
            MCMEPVP.logGame("blue", MCMEPVP.PVPMap, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.equalsIgnoreCase("blue")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, true);
                }
                if (value.equalsIgnoreCase("red")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Team " + ChatColor.BLUE + "Blue" + ChatColor.GREEN + " wins!");
            MCMEPVP.resetGame();
        }
        if (BlueMates < 1) {
            MCMEPVP.logGame("red", MCMEPVP.PVPMap, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.equalsIgnoreCase("blue")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, false);
                }
                if (value.equalsIgnoreCase("red")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, true);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.RED + "Red" + MCMEPVP.positivecolor + " wins!");
            MCMEPVP.resetGame();
        }
        if (RedScore == config.TSLscore) {
            MCMEPVP.logGame("red", MCMEPVP.PVPMap, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.equalsIgnoreCase("red")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, true);
                } else {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.RED + "Red" + MCMEPVP.positivecolor + " wins "
            +RedScore+":"+BlueScore+"!");
            MCMEPVP.resetGame();
        } else if (BlueScore == config.TSLscore) {
            MCMEPVP.logGame("blue", MCMEPVP.PVPMap, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.equalsIgnoreCase("blue")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, true);
                } else {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.BLUE + "Blue" + MCMEPVP.positivecolor + " wins "
            +BlueScore+":"+RedScore+"!");
            MCMEPVP.resetGame();
        }
    }

    @Override
    public int team1count() {
        return RedMates;
    }

    @Override
    public int team2count() {
        return BlueMates;
    }

    @Override
    public String team1() {
        return "red";
    }

    @Override
    public String team2() {
        return "blue";
    }

    @Override
    public Scoreboard getBoard() {
        return board;
    }

    @Override
    public void claimLootSign(Sign sign) {
    }

    @Override
    public void clearBoard() {
        board.clearSlot(DisplaySlot.SIDEBAR);
        blueteam.unregister();
        redteam.unregister();
        objective.unregister();
    }

    @Override
    public void displayBoard() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(board);
        }
    }

    @Override
    public HashMap<String, String> getPlaying() {
        return playing;
    }

    @Override
    public boolean isJoinable() {
        return true;
    }

    @Override
    public void onPlayerLogin(PlayerLoginEvent event) {
        //Do nothing
    }
    
    @Override
    public boolean allowBlockBreak() {
        return false;
    }

    @Override
    public boolean allowBlockPlace() {
        return false;
    }

    @Override
    public boolean allowContainerIteraction() {
        return false;
    }

	@Override
	public boolean allowExplosionLogging() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public Objective getObjective() {
        return objective;
    }
}