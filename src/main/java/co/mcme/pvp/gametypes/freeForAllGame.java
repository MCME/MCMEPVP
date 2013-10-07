package co.mcme.pvp.gametypes;

import co.mcme.pvp.MCMEPVP;
import static co.mcme.pvp.MCMEPVP.extraSpawns;
import co.mcme.pvp.gameType;
import co.mcme.pvp.stats.PlayerStat;
import co.mcme.pvp.stats.StatisticManager;
import co.mcme.pvp.util.armorColor;
import co.mcme.pvp.util.config;
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
import java.util.List;
import java.util.Random;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class freeForAllGame extends gameType {

    boolean isTharbad = MCMEPVP.PVPMap.equalsIgnoreCase("tharbad");
    public static int taskId;
    int m = config.FFATimeLimit;
    int s = 60;
    int killcount = 0;
    private HashMap<String, String> playing = new HashMap<String, String>();
    ScoreboardManager manager;
    Scoreboard board;
    Scoreboard board1;
    Objective objective;
    Objective objective1;
    Team reds;
    Team specteam;
    OfflinePlayer dummyp = Bukkit.getOfflinePlayer(ChatColor.RED
            + "Total Kills:");
    Score kills;
    Score timeremaining;
    private static String gameId;
    private static long startTime = System.currentTimeMillis();
    private static long endTime;
    private HashMap<String, PlayerStat> playerStats = new HashMap();
    String winner = "";

    public freeForAllGame() {
        try {
            gameId = MessageDigest.getInstance("MD5").digest(String.valueOf(startTime).getBytes()).toString();
        } catch (NoSuchAlgorithmException ex) {
            MCMEPVP.resetGame();
        }
        MCMEPVP.GameStatus = 1;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        board1 = manager.getNewScoreboard();

        objective = board.registerNewObjective("Player", "playerKillCount");
        objective1 = board.registerNewObjective("Kills", "dummy");

        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        objective1.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.setDisplayName(ChatColor.RED.toString());

        kills = objective1.getScore(dummyp);

        reds = board.registerNewTeam("Red Team");
        reds.setPrefix(ChatColor.RED.toString());
        reds.setSuffix(ChatColor.WHITE.toString());
        reds.setAllowFriendlyFire(true);
        reds.setCanSeeFriendlyInvisibles(false);

        specteam = board.registerNewTeam("Spectator Team");
        specteam.setAllowFriendlyFire(false);
        specteam.setCanSeeFriendlyInvisibles(true);

        // Broadcast
        Bukkit.getServer()
                .broadcastMessage(
                MCMEPVP.primarycolor
                + "The next Game starts in a few seconds!");
        Bukkit.getServer().broadcastMessage(
                MCMEPVP.primarycolor + "GameType is " + MCMEPVP.highlightcolor
                + "FreeForAll" + MCMEPVP.primarycolor + " on Map "
                + MCMEPVP.highlightcolor + MCMEPVP.PVPMap + "!");
        if (MCMEPVP.debug) {
            Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + "This is a debug game. Stats will not be recorded!");
        }
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
                ArrayList<Player> queued = new ArrayList<Player>();
                MCMEPVP.queue.drainTo(queued);
                Collections.shuffle(queued);
                for (Player p : queued) {
                    textureSwitcher.switchTP(p);
                    if (p.isOnline()) {
                        if (teamUtil.getPlayerTeam(p).equals(
                                "participant")) {
                            addTeam(p, "red");
                            p.setHealth(20);
                            p.setFoodLevel(20);
                            p.setSaturation((float) 20);
                            p.setNoDamageTicks(400);
                            p.addPotionEffect(new PotionEffect(
                                    PotionEffectType.DAMAGE_RESISTANCE,
                                    400, 1));
                            p.sendMessage(ChatColor.LIGHT_PURPLE + "You have roughly 20 seconds of spawn protection! RUN!");
                        }
                        MCMEPVP.queue.remove(p);
                        util.debug("Player `" + p.getName()
                                + "` is not online!");

                    }
                }
                spectatorUtil.startingSpectators();
                Bukkit.getServer().broadcastMessage(
                        MCMEPVP.positivecolor
                        + "The Fight begins!");
                m--;
                objective1.setDisplayName("Time: " + m + ":" + s);
                kills.setScore(0);
                updateBoard();

                MCMEPVP.canJoin = true;
            }
        }, 100L);
        CountdownTimer();
    }

    @Override
    public void claimLootSign(Sign sign) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPlayerleaveServer(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        reds.removePlayer(p);
        checkGameEnd();
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (teamUtil.getPlayerTeam(p).equals("red")) {
            addTeam(p, "red");
        } else {
            spectatorUtil.setSpectator(p);
        }
        Vector vec = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(p));
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(),
                vec.getY() + 0.5, vec.getZ());
        p.teleport(loc);
    }

    @Override
    public void addPlayerDuringGame(Player player) {
        addTeam(player, "red");
    }

    @Override
    public void onPlayerdie(PlayerDeathEvent event) {
        Player victim = event.getEntity().getPlayer();
        String team = teamUtil.getPlayerTeam(victim);
        if (victim.getKiller() instanceof Player) {
            if (team.equals("spectator")) {
                event.setDeathMessage(MCMEPVP.primarycolor + "Spectator "
                        + victim.getName() + " was tired watching this fight!");
            } else {
                StatisticManager.storePlayerDeath(event);
                killcount++;
                updateBoard();
                Player killer = event.getEntity().getKiller();

                event.setDeathMessage(ChatColor.RED + victim.getName()
                        + MCMEPVP.primarycolor + " was killed by "
                        + ChatColor.RED + killer.getName()
                        + MCMEPVP.primarycolor + "!");
                event.getDrops().add(new ItemStack(364, 1));
                event.getDrops().add(new ItemStack(262, 8));
                event.getDrops().add(gearGiver.magicItem(true, 0, 1));
            }
        } else {
            if (team.equals("spectator")) {
                event.setDeathMessage(MCMEPVP.primarycolor + "Spectator "
                        + victim.getName() + " was tired watching this fight!");
            } else {
                StatisticManager.storePlayerDeath(event);
                event.setDeathMessage(ChatColor.RED + victim.getName()
                        + MCMEPVP.primarycolor + " died to the elements!");
            }
        }
    }

    @Override
    public void addTeam(Player p, String Team) {
        if (specteam.hasPlayer(p)) {
            specteam.removePlayer(p);
            if (p.getActivePotionEffects() != null) {
                for (PotionEffect pe : p.getActivePotionEffects()) {
                    p.removePotionEffect(pe.getType());
                }
            }
        }
        playerStats.put(p.getName(), new PlayerStat(p));
        Color col = armorColor.WHITE;
        teamUtil.setPlayerTeam(p, Team);
        p.getInventory().clear();
        teamUtil.setPlayerTeam(p, "red");
        reds.addPlayer(p);
        p.setGameMode(GameMode.ADVENTURE);
        col = randomColor();

        gearGiver.loadout(p, true, isTharbad, true, "warrior", col, "boating",
                Team);
        playing.put(p.getName(), Team);

        Vector vec = extraSpawns();
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(),
                vec.getY() + 0.5, vec.getZ());
        p.teleport(loc);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation((float) 20);
        p.setScoreboard(board);
    }

    @Override
    public void onPlayerhit(EntityDamageByEntityEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        Player attacker = (Player) ((Projectile) event.getDamager())
                .getShooter();
        attacker.playSound(attacker.getLocation(), Sound.ORB_PICKUP,
                (float) 20, (float) 50);
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        addTeam(p, "red");
        Vector vec = extraSpawns();
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(),
                vec.getY() + 0.5, vec.getZ());
        event.setRespawnLocation(loc);
        p.setNoDamageTicks(160);
        p.setScoreboard(board);
    }

    public void checkGameEnd() {
        if ((m == 0 && s == 0) || reds.getSize() <= 1) {
            List<Integer> ordering = new ArrayList<Integer>();
            HashMap<Player, Integer> scores = new HashMap<Player, Integer>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                Score s = objective.getScore(p);
                ordering.add(s.getScore());
                scores.put(p, s.getScore());
            }
            Collections.sort(ordering);
            int topscore = ordering.get(ordering.size() - 1);

            List<String> list = new ArrayList<String>();
            for (Player p : scores.keySet()) {
                if (scores.get(p) == topscore) {
                    list.add(p.getName());
                }
            }
            String winners = list.toString();
            winners = winners.replace("[", "");
            winners = winners.replace("]", "");
            Bukkit.broadcastMessage(MCMEPVP.positivecolor + "Winner(s):");
            Bukkit.broadcastMessage(ChatColor.RED + winners);
            Bukkit.broadcastMessage(MCMEPVP.primarycolor + "With: " + MCMEPVP.positivecolor + topscore + MCMEPVP.primarycolor + " kills!");
            for (OfflinePlayer p : reds.getPlayers()) {
                if (p.isOnline()) {
                    p.getPlayer().setPlayerListName(p.getName());
                    p.getPlayer().sendMessage(MCMEPVP.positivecolor + "Your Score:");
                    p.getPlayer().sendMessage("" + MCMEPVP.positivecolor + objective.getScore(p).getScore() + MCMEPVP.primarycolor + " kills!");
                }
            }
            winner = winners.replaceAll(", ", ",");
            endTime = System.currentTimeMillis();
            MCMEPVP.resetGame();
        }
    }

    public void CountdownTimer() {
        freeForAllGame.taskId = Bukkit
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
                        objective1.setDisplayName("Time: " + m
                                + ":0" + s);
                    } else {
                        objective1.setDisplayName("Time: " + m
                                + ":" + s);
                        if (s == 30) {
                            updateBoard();
                        }
                    }
                } else {
                    if (m == 0 && s == 0) {
                        Bukkit.getScheduler()
                                .cancelTask(taskId);
                        checkGameEnd();
                    }
                    if (m > 0 && s == 0) {
                        m--;
                        s = 59;
                        objective1.setDisplayName("Time: " + m
                                + ":" + s);
                        updateBoard();
                    }
                }
            }
        }, 0L, 20L);
    }

    public static void stopTimer() {
        Bukkit.getScheduler().cancelTask(freeForAllGame.taskId);
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

    public Color randomColor() {
        int i = getRandom(0, 8);
        Color col = armorColor.RED;
        if (i == 0) {
            col = armorColor.RED;
        }
        if (i == 1) {
            col = armorColor.BLUE;
        }
        if (i == 2) {
            col = armorColor.AQUA;
        }
        if (i == 3) {
            col = armorColor.BLACK;
        }
        if (i == 4) {
            col = armorColor.GREEN;
        }
        if (i == 5) {
            col = armorColor.LIME;
        }
        if (i == 6) {
            col = armorColor.PURPLE;
        }
        if (i == 7) {
            col = armorColor.WHITE;
        }
        if (i == 8) {
            col = armorColor.YELLOW;
        }
        return col;

    }

    @Override
    public int team1count() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int team2count() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String team1() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String team2() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scoreboard getBoard() {
        // TODO Auto-generated method stub
        return null;
    }

    public void updateBoard() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(board);
            kills.setScore(killcount);
            if (p.getName().length() > 13) {
                int i = objective.getScore(p).getScore();
                String s = p.getName().substring(0, 9);
                p.setPlayerListName(ChatColor.RED + s + " " + ChatColor.YELLOW + i);
            }
        }
    }

    @Override
    public void clearBoard() {
        board.clearSlot(DisplaySlot.PLAYER_LIST);
        board1.clearSlot(DisplaySlot.SIDEBAR);
        objective.unregister();
        objective1.unregister();
        reds.unregister();
        specteam.unregister();
    }

    @Override
    public void displayBoard() {
        // TODO Auto-generated method stub
    }

    @Override
    public HashMap<String, String> getPlaying() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isJoinable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean allowBlockBreak() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean allowBlockPlace() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean allowContainerIteraction() {
        // TODO Auto-generated method stub
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
