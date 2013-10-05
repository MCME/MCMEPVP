package co.mcme.pvp.gametypes;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.stats.PlayerStat;
import co.mcme.pvp.stats.StatisticManager;
import co.mcme.pvp.util.armorColor;
import co.mcme.pvp.util.gearGiver;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.textureSwitcher;
import co.mcme.pvp.util.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class teamDeathMatchGame extends gameType {

    private int RedMates = 0;
    private int BlueMates = 0;
    public Plugin plugin;
    boolean isTharbad = MCMEPVP.PVPMap.equalsIgnoreCase("tharbad");
    private HashMap<String, String> playing = new HashMap<String, String>();
    ScoreboardManager manager;
    Scoreboard board;
    Team redteam;
    Team blueteam;
    Team specteam;
    Objective objective;
    OfflinePlayer dummyred = Bukkit.getOfflinePlayer(ChatColor.RED + "Red Players:");
    OfflinePlayer dummyblue = Bukkit.getOfflinePlayer(ChatColor.BLUE + "Blue Players:");
    Score redscore;
    Score bluescore;
    private static String gameId;
    private static long startTime = System.currentTimeMillis();
    private static long endTime;
    private HashMap<String, PlayerStat> playerStats = new HashMap();
    String winner = "";

    public teamDeathMatchGame() {
        try {
            gameId = MessageDigest.getInstance("MD5").digest(String.valueOf(startTime).getBytes()).toString();
        } catch (NoSuchAlgorithmException ex) {
            MCMEPVP.resetGame();
        }
        MCMEPVP.GameStatus = 1;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        redteam = board.registerNewTeam("Red Team");
        redteam.setPrefix(ChatColor.RED.toString());
        blueteam = board.registerNewTeam("Blue Team");
        blueteam.setPrefix(ChatColor.BLUE.toString());
        objective = board.registerNewObjective("Score", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        redscore = objective.getScore(dummyred);
        bluescore = objective.getScore(dummyblue);

        specteam = board.registerNewTeam("Spectator Team");
        specteam.setAllowFriendlyFire(false);
        specteam.setCanSeeFriendlyInvisibles(true);

        //Broadcast
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "The next Game starts in a few seconds!");
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "GameType is " + MCMEPVP.highlightcolor + "Team Deathmatch" + MCMEPVP.primarycolor + " on Map " + MCMEPVP.highlightcolor + MCMEPVP.PVPMap + "!");
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
                        //heal
                        p.setHealth(20);
                        p.setFoodLevel(20);
                        p.setSaturation((float) 20);
                        MCMEPVP.queue.remove(p);
                    } else {
                        MCMEPVP.queue.remove(p);
                        util.debug("Player `" + p.getName() + "` is not online!");
                    }
                }
                spectatorUtil.startingSpectators();
                //Broadcast
                Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "The Fight begins!");
                redscore.setScore(RedMates);
                bluescore.setScore(BlueMates);

                MCMEPVP.setWeather();
                displayBoard();

                MCMEPVP.canJoin = true;
            }
        }, 100L);
    }

    @Override
    public void addPlayerDuringGame(Player player) {
        player.sendMessage(MCMEPVP.negativecolor + "This gametype does not allow joining during matches!");
    }

    @Override
    public void addTeam(Player player, String Team) {
        if (specteam.hasPlayer(player)) {
            specteam.removePlayer(player);
            if (player.getActivePotionEffects() != null) {
                for (PotionEffect pe : player.getActivePotionEffects()) {
                    player.removePotionEffect(pe.getType());
                }
            }
        }
        playerStats.put(player.getName(), new PlayerStat(player));
        Color col;
        if (Team.equals("red")) {
            player.sendMessage(MCMEPVP.primarycolor + "You're now in Team " + ChatColor.RED + "RED" + MCMEPVP.primarycolor + "!");
            RedMates++;
            teamUtil.setPlayerTeam(player, Team);
            redteam.addPlayer(player);
            col = armorColor.RED;
            gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Team);
            player.setGameMode(GameMode.ADVENTURE);
        } else if (Team.equals("blue")) {
            player.sendMessage(MCMEPVP.primarycolor + "You're now in Team " + ChatColor.BLUE + "BLUE" + MCMEPVP.primarycolor + "!");
            BlueMates++;
            teamUtil.setPlayerTeam(player, Team);
            blueteam.addPlayer(player);
            col = armorColor.BLUE;
            gearGiver.loadout(player, true, isTharbad, true, "warrior", col, "boating", Team);
            player.setGameMode(GameMode.ADVENTURE);
        }
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
            if (Team.equals("spectator")) {
                spectatorUtil.setSpectator(player);
            }
            Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
            Location loc = vec.toLocation(MCMEPVP.PVPWorld);
            player.teleport(loc);
            displayBoard();
        }
    }

    @Override
    public void onPlayerleaveServer(PlayerQuitEvent event) {
        String OldTeam = teamUtil.getPlayerTeam(event.getPlayer());
        if (OldTeam.equals("red")) {
            RedMates--;
            redteam.removePlayer(event.getPlayer());
        }
        if (OldTeam.equals("blue")) {
            BlueMates--;
            blueteam.removePlayer(event.getPlayer());
        } else {
        }
        checkGameEnd();
    }

    @Override
    public void onPlayerdie(PlayerDeathEvent event) {
        StatisticManager.storePlayerDeath(event);
        Player player = event.getEntity();
        String Status = teamUtil.getPlayerTeam(player);

        String victim = player.getName();
        String deathMessage = MCMEPVP.primarycolor + " was lost in battle!";
        //TODO Log deaths
        if (Status.equals("spectator")) {
            event.setDeathMessage(MCMEPVP.primarycolor + "Spectator " + player.getName() + " was tired watching this fight!");
        }
        if (Status.equals("red")) {
            RedMates--;
            redscore.setScore(RedMates);
            victim = ChatColor.RED + player.getName();
            event.getDrops().add(new ItemStack(364, 1));
            redteam.removePlayer(player);
        }
        if (Status.equals("blue")) {
            BlueMates--;
            bluescore.setScore(BlueMates);
            victim = ChatColor.BLUE + player.getName();
            event.getDrops().add(new ItemStack(364, 1));
            blueteam.removePlayer(player);
        }
        if (player.getKiller() instanceof Player) {
            Player killerP = player.getKiller();
            String killer = player.getKiller().getName();

            if (teamUtil.getPlayerTeam(killerP).equals("red")) {
                killer = ChatColor.RED + killerP.getName();
            }
            if (teamUtil.getPlayerTeam(killerP).equals("blue")) {
                killer = ChatColor.BLUE + killerP.getName();
            }
            deathMessage = victim + MCMEPVP.positivecolor + " was killed by " + killer;
        }
        event.setDeathMessage(deathMessage);
        teamUtil.setPlayerTeam(event.getEntity(), "spectator");
        checkGameEnd();
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        spectatorUtil.setSpectator(player);
        Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(player));
        Location spawnloc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5, vec.getZ());
        event.setRespawnLocation(spawnloc);
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

    private void checkGameEnd() {
        if (BlueMates <= 0) {
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.RED + "Red" + MCMEPVP.positivecolor + " wins "
                    + RedMates + ":" + BlueMates + "!");
            winner = "red";
            endTime = System.currentTimeMillis();
            MCMEPVP.resetGame();
        } else if (RedMates <= 0) {
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.BLUE + "Blue" + MCMEPVP.positivecolor + " wins "
                    + BlueMates + ":" + RedMates + "!");
            winner = "blue";
            endTime = System.currentTimeMillis();
            MCMEPVP.resetGame();
        }
    }

    @Override
    public void claimLootSign(Sign sign) {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearBoard() {
        board.clearSlot(DisplaySlot.SIDEBAR);
        blueteam.unregister();
        redteam.unregister();
        specteam.unregister();
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
        return false;
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
        return true;
    }

    @Override
    public void addSpectatorTeam(Player p) {
        specteam.addPlayer(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1));
    }

    @Override
    public String getGameId() {
        return gameId;
    }

    @Override
    public HashMap<String, PlayerStat> getPlayerStats() {
        return playerStats;
    }

    @Override
    public Long getStartTime() {
        return startTime;
    }

    @Override
    public Long getEndTime() {
        return endTime;
    }

    @Override
    public String getWinner() {
        return winner;
    }
}