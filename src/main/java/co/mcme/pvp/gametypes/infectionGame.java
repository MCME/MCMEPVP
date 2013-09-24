package co.mcme.pvp.gametypes;

import static co.mcme.pvp.MCMEPVP.extraSpawns;
import static co.mcme.pvp.util.config.ZombieHealth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.util.armorColor;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.gearGiver;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.textureSwitcher;
import co.mcme.pvp.util.util;

public class infectionGame extends gameType {

    public static int taskId;
    int m = config.INFTimeLimit;
    int s = 60; //last 60 seconds of game
    int targetzombiecount;
    int targetsurvivorcount;
    int zombiecount;
    int survivorcount;
    private HashMap<String, String> playing = new HashMap<String, String>();
    ScoreboardManager manager;
    Scoreboard board;
    Team zombieteam;
    Team survivorteam;
    Team specteam;
    Objective objective;
    OfflinePlayer dummyzombie = Bukkit.getOfflinePlayer(ChatColor.DARK_PURPLE
            + "Infected:");
    OfflinePlayer dummysurvivor = Bukkit.getOfflinePlayer(ChatColor.AQUA
            + "Survivors:");
    Score zombiescore;
    Score survivorscore;
    boolean isTharbad = MCMEPVP.PVPMap.equalsIgnoreCase("tharbad");

    public infectionGame() {
        MCMEPVP.GameStatus = 1;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        zombieteam = board.registerNewTeam("Red Team");
        zombieteam.setPrefix(ChatColor.DARK_PURPLE.toString());
        survivorteam = board.registerNewTeam("Blue Team");
        survivorteam.setPrefix(ChatColor.AQUA.toString());
        objective = board.registerNewObjective("Score", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        zombiescore = objective.getScore(dummyzombie);
        survivorscore = objective.getScore(dummysurvivor);
        
        specteam = board.registerNewTeam("Spectator Team");
        specteam.setAllowFriendlyFire(false);
        specteam.setCanSeeFriendlyInvisibles(true);
        // Broadcast
        Bukkit.getServer().broadcastMessage(
                MCMEPVP.primarycolor
                + "The next Game starts in a few seconds!");
        Bukkit.getServer().broadcastMessage(
                MCMEPVP.primarycolor + "GameType is " + MCMEPVP.highlightcolor
                + "Infection" + MCMEPVP.primarycolor + " on Map "
                + MCMEPVP.highlightcolor + MCMEPVP.PVPMap + "!");
        Bukkit.getServer()
                .broadcastMessage(
                MCMEPVP.positivecolor
                + "All Participants will be assigned to a team and teleported to their spawn!");
        Bukkit.getServer()
                .getScheduler()
                .scheduleSyncDelayedTask(
                Bukkit.getPluginManager().getPlugin("MCMEPVP"),
                new Runnable() {
            public void run() {
                determineTeamCounts();
                ArrayList<Player> queued = new ArrayList<Player>();
                MCMEPVP.queue.drainTo(queued);
                Collections.shuffle(queued);
                for (Player user : queued) {
                    textureSwitcher.switchTP(user);
                    if (user.isOnline()) {
                        if (teamUtil.getPlayerTeam(user).equals(
                                "participant")) {
                            if (zombiecount < targetzombiecount) {
                                addTeam(user, "zombie");
                            } else {
                                if (survivorcount < targetsurvivorcount) {
                                    addTeam(user, "survivor");
                                } else {
                                    boolean random = (Math
                                            .random() < 0.5);
                                    if (random == true) {
                                        addTeam(user, "zombie");
                                    } else {
                                        addTeam(user,
                                                "survivor");
                                    }
                                }
                            }
                            // heal
                            user.setHealth(20);
                            user.setFoodLevel(20);
                            user.setSaturation((float) 20);
                        }
                    } else {
                        MCMEPVP.queue.remove(user);
                        util.debug("Player `" + user.getName()
                                + "` is not online!");
                    }
                }
                // Broadcast
                Bukkit.getServer().broadcastMessage(
                        MCMEPVP.positivecolor
                        + "The Fight begins!");
                
                MCMEPVP.setWeather();
                spectatorUtil.startingSpectators();
                displayBoard();
                m--;
                objective.setDisplayName("Time: " + m + ":" + s);
                
                MCMEPVP.canJoin = true;
            }
        }, 100L);
        CountdownTimer();
    }

    public void determineTeamCounts() {
        int playercount = MCMEPVP.queue.size();
        targetzombiecount = (int) (playercount * 0.1);
        if (targetzombiecount == 0) {
            targetzombiecount = 1;
        }
        targetsurvivorcount = playercount - targetzombiecount;
    }

    @Override
    public void addTeam(Player player, String Team) {
    	if (specteam.hasPlayer(player)) {
    		specteam.removePlayer(player);
    		if(player.getActivePotionEffects() != null){
            	for(PotionEffect pe : player.getActivePotionEffects()){
            		player.removePotionEffect(pe.getType());
            	}
            }
    	}
        Color col = armorColor.WHITE;
        switch (Team) {
            case "zombie":
                player.getInventory().clear();
                player.sendMessage(MCMEPVP.primarycolor + "You're now a "
                        + ChatColor.DARK_PURPLE + "ZOMBIE" + MCMEPVP.primarycolor
                        + "!");
                teamUtil.setPlayerTeam(player, "red");
                zombieteam.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
                col = armorColor.LIME;
                teamCount();
                break;
            case "survivor":
                player.getInventory().clear();
                player.sendMessage(MCMEPVP.primarycolor + "You're now a "
                        + ChatColor.AQUA + "SURVIVOR" + MCMEPVP.primarycolor + "!");
                teamUtil.setPlayerTeam(player, "blue");
                survivorteam.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
                col = armorColor.AQUA;
                teamCount();
                break;
        }
        if (MCMEPVP.PVPMap.equalsIgnoreCase("tharbad")) {
            gearGiver.giveExtras(player, Team, "boating");
        }
        gearGiver.loadout(player, true, isTharbad, true, "warrior", col,
                "boating", Team);
        playing.put(player.getName(), Team);

        Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5,
                vec.getZ());
        player.teleport(loc);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation((float) 20);
        if (Team.contains("zombie")) {
            World w = player.getWorld();
            w.playSound(loc, Sound.ZOMBIE_IDLE, 1, 1);
        }
        teamCount();
    }

    @Override
    public void addPlayerDuringGame(Player player) {
        if (m > 5) {
            Player p = player;
            addTeam(p, "survivor");
            teamCount();
        } else {
            Player p = player;
            addTeam(p, "zombie");
            teamCount();
        }
    }

    @Override
    public void claimLootSign(Sign sign) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (MCMEPVP.GameStatus == 1) {
            Player player = event.getPlayer();
            String Team = teamUtil.getPlayerTeam(player);
            if (Team.equals("red")) {
                addTeam(player, "zombie");
            }
            if (Team.equals("blue")) {
                if (m > 5) {
                    addTeam(player, "survivor");
                    teamCount();
                } else {
                    teamUtil.setPlayerTeam(player, "red");
                    addTeam(player, "zommbie");
                    teamCount();
                }
            }
            if (Team.equals("spectator")) {
                spectatorUtil.setSpectator(player);
            }
            Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
            Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(),
                    vec.getY() + 0.5, vec.getZ());
            player.teleport(loc);
        }
        teamCount();
        displayBoard();
    }

    @Override
    public void onPlayerleaveServer(PlayerQuitEvent event) {
        String Team = teamUtil.getPlayerTeam(event.getPlayer());
        if (Team.equals("red")) {
            zombieteam.removePlayer(event.getPlayer());
            teamCount();
        }
        if (Team.equals("blue")) {
            survivorteam.removePlayer(event.getPlayer());
            teamCount();
        }
        checkGameEnd();
    }

    @Override
    public void onPlayerdie(PlayerDeathEvent event) {
        Player p = event.getEntity();
        World w = p.getWorld();
        Location l = p.getLocation();
        String Status = teamUtil.getPlayerTeam(p);
        if (p.getKiller() instanceof Player) {
            if (Status.equals("spectator")) {
                event.setDeathMessage(MCMEPVP.primarycolor + "Spectator "
                        + p.getName() + " was tired watching this fight!");
            }
            if (Status.equals("blue")) {
                event.setDeathMessage(ChatColor.BLUE + p.getName()
                        + MCMEPVP.primarycolor + " was infected by "
                        + ChatColor.RED + p.getKiller().getName());
                survivorteam.removePlayer(p);
                zombieteam.addPlayer(p);
                event.getDrops().add(new ItemStack(364, 1));
                event.getDrops().add(new ItemStack(262, 8));
            } else if (Status.equals("red")) {
                event.setDeathMessage(ChatColor.RED + p.getName()
                        + MCMEPVP.primarycolor + " was killed by "
                        + ChatColor.BLUE + p.getKiller().getName());
                event.getDrops().add(new ItemStack(367, 2));
                event.getDrops().add(new ItemStack(262, 8));
                w.playSound(l, Sound.ZOMBIE_DEATH, 1, (float) 1);
                w.playEffect(l, Effect.MOBSPAWNER_FLAMES, 9);
            }
            teamCount();
            checkGameEnd();
        } else {
            if (Status.equals("spectator")) {
                event.setDeathMessage(MCMEPVP.primarycolor + "Spectator "
                        + p.getName() + " was tired watching this fight!");
            }
            if (Status.equals("blue")) {
                event.setDeathMessage(ChatColor.BLUE + p.getName()
                        + MCMEPVP.primarycolor + " was lost in battle!");
                event.getDrops().add(new ItemStack(364, 1));
                event.getDrops().add(new ItemStack(262, 8));
                survivorteam.removePlayer(p);
                zombieteam.addPlayer(p);
            } else if (Status.equals("red")) {
                event.setDeathMessage(ChatColor.RED + p.getName()
                        + MCMEPVP.primarycolor + " was lost in battle!");
                event.getDrops().add(new ItemStack(367, 2));
                event.getDrops().add(new ItemStack(262, 8));
            }
            teamCount();
            checkGameEnd();
        }
    }

    @Override
    public void onPlayerhit(EntityDamageByEntityEvent event) {
        Player defender = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        String attackerteam = teamUtil.getPlayerTeam(attacker);
        String defenderteam = teamUtil.getPlayerTeam(defender);
        if (attackerteam.equals(defenderteam)) {
            event.setCancelled(true);
        } else if (defenderteam.equals("red")) {
            if (defender.getHealth() > ZombieHealth) {
                defender.setHealth(ZombieHealth);
            }
            World w = defender.getWorld();
            Location l = defender.getLocation();
            w.playSound(l, Sound.ZOMBIE_HURT, 1, (float) 1);
        }
    }

    @Override
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        Player defender = (Player) event.getEntity();
        Player attacker = (Player) ((Projectile) event.getDamager())
                .getShooter();
        String attackerteam = teamUtil.getPlayerTeam(attacker);
        String defenderteam = teamUtil.getPlayerTeam(defender);
        if (attackerteam.equals(defenderteam)) {
            event.setCancelled(true);
        } else if (defenderteam.equals("red")) {
            attacker.playSound(attacker.getLocation(), Sound.ORB_PICKUP,
                    (float) 20, (float) 50);
            defender.getWorld().playSound(defender.getLocation(), Sound.ZOMBIE_HURT, 1, (float) 1);
        }
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        teamUtil.setPlayerTeam(player, "red");
        addTeam(player, "zombie");
        Vector vec = extraSpawns();
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5,
                vec.getZ());
        event.setRespawnLocation(loc);
        teamCount();
    }

    public void checkGameEnd() {
        if (survivorscore.getScore() == 0) {
            MCMEPVP.logGame("red", MCMEPVP.PVPMap, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                util.debug("player: " + key + " Team: " + value);
                if (value.equalsIgnoreCase("red")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, true);
                } else {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "The " + ChatColor.DARK_PURPLE + "Infected"
                    + MCMEPVP.positivecolor + " win with " + ChatColor.DARK_PURPLE + m + "m" + s + "s" + MCMEPVP.positivecolor + " remaining!");
            MCMEPVP.resetGame();
        }
        if (zombiescore.getScore() <= 0) {
            MCMEPVP.logGame("blue", MCMEPVP.PVPMap, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                util.debug("player: " + key + " Team: " + value);
                if (value.equalsIgnoreCase("blue")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, true);
                } else {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "The " + ChatColor.BLUE + "Survivors" + MCMEPVP.positivecolor + " win by default!");
            MCMEPVP.resetGame();
        }
        if (m == 0 && s == 0) {
            MCMEPVP.logGame("blue", MCMEPVP.PVPMap, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                util.debug("player: " + key + " Team: " + value);
                if (value.equalsIgnoreCase("blue")) {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, true);
                } else {
                    MCMEPVP.logJoin(key, MCMEPVP.PVPMap, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "The " + ChatColor.BLUE + "Survivors"
                    + MCMEPVP.positivecolor + " win with " + ChatColor.BLUE + survivorcount + " survivors" + MCMEPVP.positivecolor + " remaining!");
            MCMEPVP.resetGame();
        }
    }

    public void CountdownTimer() {
        infectionGame.taskId = Bukkit
                .getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(
                Bukkit.getPluginManager().getPlugin("MCMEPVP"),
                new Runnable() {
            @Override
            public void run() {
                if (s > 0) {
                    s--;
                    if (s < 10) {
                        objective.setDisplayName("Time: " + m + ":0" + s);
                    } else {
                        objective.setDisplayName("Time: " + m + ":" + s);
                    }
                } else {
                    if (m == 0 && s == 0) {
                        checkGameEnd();
                    }
                    if (m > 0 && s == 0) {
                        m--;
                        s = 59;
                        objective.setDisplayName("Time: " + m + ":" + s);
                        if (m < 8 && ZombieHealth <= 20) {
                            ZombieHealth++;
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    public static void stopTimer() {
        Bukkit.getScheduler().cancelTask(infectionGame.taskId);
    }

    private void teamCount() {
        zombiecount = zombieteam.getSize();
        survivorcount = survivorteam.getSize();
        zombiescore.setScore(zombiecount);
        survivorscore.setScore(survivorcount);
    }

    @Override
    public int team1count() {
        return survivorcount;
    }

    @Override
    public int team2count() {
        return zombiecount;
    }

    @Override
    public String team1() {
        return "survivor";
    }

    @Override
    public String team2() {
        return "zombie";
    }

    @Override
    public Scoreboard getBoard() {
        return board;
    }

    public void updateBoard() {
        survivorscore.setScore(survivorcount);
        zombiescore.setScore(zombiecount);
    }

    @Override
    public void clearBoard() {
        board.clearSlot(DisplaySlot.SIDEBAR);
        zombieteam.unregister();
        survivorteam.unregister();
        specteam.unregister();
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Vector extraSpawns() {
        int max = extraSpawns.size() - 1;
        int min = 0;
        return extraSpawns.get(getRandom(min, max));
    }

    public int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }

    @Override
    public void onPlayerLogin(PlayerLoginEvent event) {
        // Do nothing
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

	@Override
	public boolean allowCustomAttributes() {
		return false;
	}

	@Override
	public void addSpectatorTeam(Player p) {
		specteam.addPlayer(p);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,999999,1));
	}
}
