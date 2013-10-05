package co.mcme.pvp.gametypes;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.stats.PlayerStat;
import co.mcme.pvp.stats.StatisticManager;
import co.mcme.pvp.util.armorColor;
import co.mcme.pvp.util.gearGiver;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.teamUtil;
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
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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

public class ringBearerGame extends gameType {

    public static HashMap<Player, String> ringBearers = new HashMap<Player, String>();
    private int blueSize = 0;
    private int redSize = 0;
    private int m = 5;
    private int lm = 9;
    public static int taskId = 0;
    boolean isJoinable = true;
    boolean redHasBearer = false;
    boolean blueHasBearer = false;
    boolean lastMan = false;
    boolean spawnSwitch = false;
    ScoreboardManager manager;
    Scoreboard board;
    Team redteam;
    Team blueteam;
    Team specteam;
    Objective objective;
    OfflinePlayer dummyred = Bukkit.getOfflinePlayer(ChatColor.RED + "Red:");
    OfflinePlayer dummyblue = Bukkit.getOfflinePlayer(ChatColor.BLUE + "Blue:");
    Score redscore;
    Score bluescore;
    private static String gameId;
    private static long startTime = System.currentTimeMillis();
    private static long endTime;
    private HashMap<String, PlayerStat> playerStats = new HashMap();
    String winner = "";

    public ringBearerGame() {
        try {
            gameId = MessageDigest.getInstance("MD5").digest(String.valueOf(startTime).getBytes()).toString();
        } catch (NoSuchAlgorithmException ex) {
            MCMEPVP.resetGame();
        }
        MCMEPVP.GameStatus = 1;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();

        objective = board.registerNewObjective("Players Left", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        redteam = board.registerNewTeam("Red Team");
        blueteam = board.registerNewTeam("Blue Team");
        specteam = board.registerNewTeam("Spectator Team");

        redteam.setPrefix(ChatColor.RED.toString());
        blueteam.setPrefix(ChatColor.BLUE.toString());

        redteam.setAllowFriendlyFire(false);
        blueteam.setAllowFriendlyFire(false);
        specteam.setAllowFriendlyFire(false);

        specteam.setCanSeeFriendlyInvisibles(true);

        redscore = objective.getScore(dummyred);
        bluescore = objective.getScore(dummyblue);

        // Announce
        announceGame();

        // Run after delay
        Bukkit.getServer()
                .getScheduler()
                .scheduleSyncDelayedTask(
                Bukkit.getPluginManager().getPlugin("MCMEPVP"),
                new Runnable() {
            @Override
            public void run() {
                chooseTeams();
                spectatorUtil.startingSpectators();
                displayBoard();

                MCMEPVP.canJoin = true;
            }
        }, 100L);
        CountdownTimer();
    }

    // HANDLE ADDING/REMOVING OF PLAYERS
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
        boolean isTharbad = false;
        playerStats.put(p.getName(), new PlayerStat(p));
        Color col = armorColor.WHITE;

        switch (Team) {
            case "red":
                p.sendMessage(MCMEPVP.primarycolor + "You're now in Team "
                        + ChatColor.RED + "RED" + MCMEPVP.primarycolor + "!");
                if (!redteam.hasPlayer(p)) {
                    redteam.addPlayer(p);
                }
                col = armorColor.RED;
                break;
            case "blue":
                p.sendMessage(MCMEPVP.primarycolor + "You're now in Team "
                        + ChatColor.BLUE + "BLUE" + MCMEPVP.primarycolor + "!");
                if (!blueteam.hasPlayer(p)) {
                    blueteam.addPlayer(p);
                }
                col = armorColor.BLUE;
                break;
        }
        teamCount();
        teamUtil.setPlayerTeam(p, Team);

        p.getInventory().clear();
        p.setGameMode(GameMode.ADVENTURE);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation((float) 20);

        gearGiver.loadout(p, true, isTharbad, true, "warrior", col, "boating",
                Team);
        if (ringBearers.containsKey(p)) {
            p.getInventory().setItem(4, gearGiver.magicItem(false, 0, 1));
        }
        Location loc = getSpawn(p, Team);
        p.teleport(loc);
    }

    @Override
    public void addPlayerDuringGame(Player p) {
        if (redHasBearer && blueHasBearer) {
            if (redSize == blueSize) {
                boolean random = (Math.random() < 0.5);
                if (random == true) {
                    addTeam(p, "red");
                } else {
                    addTeam(p, "blue");
                }
            } else {
                if (redSize > blueSize) {
                    if (!blueHasBearer) {
                        addBearer(p, "blue");
                    }
                    addTeam(p, "blue");
                }
                if (redSize < blueSize) {
                    if (!redHasBearer) {
                        addBearer(p, "red");
                    }
                    addTeam(p, "red");
                }
            }
        } else {
            p.sendMessage(MCMEPVP.negativecolor
                    + "This game is no-longer joinable!");
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String team = teamUtil.getPlayerTeam(p);

        p.getInventory().clear();
        if (redHasBearer && blueHasBearer) {
            if (team.equals("red")) {
                addTeam(p, team);
            }
            if (team.equals("blue")) {
                addTeam(p, team);
            }
            if (team.equals("spectator")) {
                spectatorUtil.setSpectator(p);
            }
            Location l = MCMEPVP.Spawns.get(teamUtil.getPlayerTeam(p))
                    .toLocation(MCMEPVP.PVPWorld);
            p.teleport(l);
        } else {
            teamUtil.setPlayerTeam(p, "spectator");
            spectatorUtil.setSpectator(p);
        }
        displayBoard();
        teamCount();
    }

    @Override
    public void onPlayerleaveServer(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        String team = teamUtil.getPlayerTeam(p);

        if (team.equals("red")) {
            redteam.removePlayer(p);
            if (ringBearers.containsKey(p)) {
                removeBearer(p, team);
                switchBearer(team);
            }
        }
        if (team.equals("blue")) {
            blueteam.removePlayer(p);
            if (ringBearers.containsKey(p)) {
                removeBearer(p, team);
                switchBearer(team);
            }
        }

        teamCount();
        checkEndGame();
    }

    // HANDLE DAMAGE/KILLS
    @Override
    public void onPlayerhit(EntityDamageByEntityEvent event) {
        // Unused - friendly fire already disabled
    }

    @Override
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
        // Unused - friendly fire already disabled
    }

    @Override
    public void onPlayerdie(PlayerDeathEvent event) {
        StatisticManager.storePlayerDeath(event);
        Player victim = event.getEntity();
        String team = teamUtil.getPlayerTeam(victim);

        if (team.equals("spectator")) {
            event.setDeathMessage(MCMEPVP.primarycolor + "Spectator "
                    + victim.getName() + " was tired watching this fight!");
        } else {
            if (ringBearers.containsKey(victim)) {
                String s = ringBearers.get(victim);
                ChatColor msg = ChatColor.WHITE;

                if (s.equals("red")) {
                    msg = ChatColor.RED;
                }
                if (s.equals("blue")) {
                    msg = ChatColor.BLUE;
                }
                Bukkit.broadcastMessage(msg + s.toUpperCase() + " Team "
                        + ChatColor.DARK_RED + "lost their RingBearer!");

                removeBearer(victim, s);
            }

            String deathMessage = "!";

            if (team.equals("red")) {
                if (!redHasBearer) {
                    teamUtil.setPlayerTeam(victim, "spectator");
                    redteam.removePlayer(victim);
                }
                deathMessage = ChatColor.RED + victim.getName()
                        + MCMEPVP.primarycolor + " was lost in battle!";
            }

            if (team.equals("blue")) {
                if (!blueHasBearer) {
                    teamUtil.setPlayerTeam(victim, "spectator");
                    blueteam.removePlayer(victim);
                }
                deathMessage = ChatColor.BLUE + victim.getName()
                        + MCMEPVP.primarycolor + " was lost in battle!";
            }

            if (victim.getKiller() instanceof Player) {
                Player killer = victim.getKiller();
                String killTeam = teamUtil.getPlayerTeam(killer);
                if (killTeam.equals("red")) {
                    deathMessage = ChatColor.BLUE + victim.getName()
                            + MCMEPVP.primarycolor + " was killed by "
                            + ChatColor.RED + killer.getName();
                }
                if (killTeam.equals("blue")) {
                    deathMessage = ChatColor.RED + victim.getName()
                            + MCMEPVP.primarycolor + " was killed by "
                            + ChatColor.BLUE + killer.getName();
                }
                if (ringBearers.containsKey(killer)) {
                    event.getDrops().add(gearGiver.magicItem(false, 0, 1));
                }
            }
            event.getDrops().add(new ItemStack(364, 1));
            event.getDrops().add(new ItemStack(262, 8));
            event.setDeathMessage(deathMessage);

            teamCount();
            checkEndGame();
        }
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        String team = teamUtil.getPlayerTeam(p);
        Location l = MCMEPVP.Spawns.get("spectator").toLocation(
                MCMEPVP.PVPWorld);

        if ((team.equals("red") && redHasBearer)
                || (team.equals("blue") && blueHasBearer)) {
            l = getSpawn(p, team);
            event.setRespawnLocation(l);
            addTeam(p, team);
        } else {
            if (team.equals("red")) {
                if (redteam.hasPlayer(p)) {
                    redteam.removePlayer(p);
                }
            }
            if (team.equals("blue")) {
                if (blueteam.hasPlayer(p)) {
                    blueteam.removePlayer(p);
                }
            }
            event.setRespawnLocation(l);
            teamUtil.setPlayerTeam(p, "spectator");
        }

        teamCount();
        checkEndGame();
    }

    private Location getSpawn(Player p, String s) {
        String team = s;
        if (spawnSwitch) {
            if (s.equals("red")) {
                team = "blue";
            }
            if (s.equals("blue")) {
                team = "red";
            }
        }
        Vector vec = MCMEPVP.Spawns.get(team);
        Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(),
                vec.getY() + 0.5, vec.getZ());
        return loc;
    }

    // CHECK GAME-END SCENARIO
    private void checkEndGame() {
        if (!lastMan && (redSize == 1 || blueSize == 1)) {
            lastMan = true;
            if (redSize == 1) {
                Bukkit.broadcastMessage(ChatColor.RED + "Reds "
                        + MCMEPVP.positivecolor + "are down to their last man!");
            }
            if (blueSize == 1) {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Blues "
                        + MCMEPVP.positivecolor + "are down to their last man!");
            }
            Bukkit.broadcastMessage(MCMEPVP.positivecolor
                    + "Game ending in 10 minutes!");
        }

        if (redSize <= 0) {
            Bukkit.getServer().broadcastMessage(
                    MCMEPVP.positivecolor + "Team " + ChatColor.BLUE + "Blue"
                    + MCMEPVP.positivecolor + " wins!");
            stopTimer();
            winner = "blue";
            endTime = System.currentTimeMillis();
            MCMEPVP.resetGame();
        }
        if (blueSize <= 0) {
            Bukkit.getServer().broadcastMessage(
                    MCMEPVP.positivecolor + "Team " + ChatColor.RED + "Red"
                    + MCMEPVP.positivecolor + " wins!");
            stopTimer();
            winner = "red";
            endTime = System.currentTimeMillis();
            MCMEPVP.resetGame();
        }
        if (lastMan && lm == 0) {
            Bukkit.getServer().broadcastMessage(
                    MCMEPVP.positivecolor + "Game Over - Stalemate!");
            stopTimer();
            endTime = System.currentTimeMillis();
            MCMEPVP.resetGame();
        }
    }

    // SCOREBOARD STUFF
    @Override
    public void displayBoard() {
        redscore.setScore(redSize);
        bluescore.setScore(blueSize);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(board);
        }
    }

    @Override
    public Scoreboard getBoard() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Objective getObjective() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearBoard() {
        board.clearSlot(DisplaySlot.SIDEBAR);
        blueteam.unregister();
        redteam.unregister();
        specteam.unregister();
        objective.unregister();
    }

    public void CountdownTimer() {
        ringBearerGame.taskId = Bukkit
                .getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(
                Bukkit.getPluginManager().getPlugin("MCMEPVP"),
                new Runnable() {
            @Override
            public void run() {
                if (m > 0) {
                    m--;
                }
                if (m == 1) {
                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE
                            + "Team spawns switching in 1 minute!");
                }
                if (m == 0) {
                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE
                            + "Team spawns have now switched!");
                    if (spawnSwitch) {
                        spawnSwitch = false;
                    } else {
                        spawnSwitch = true;
                    }
                    m = 5;
                }
                if (lastMan) {
                    lm--;
                }
                if (lm == 1) {
                    Bukkit.broadcastMessage(MCMEPVP.positivecolor
                            + "Game ending in 1 minute!");
                }
                if (lm == 0) {
                    checkEndGame();
                }
            }
        }, 1200L, 1200L);
    }

    public static void stopTimer() {
        if (taskId != 0) {
            Bukkit.getScheduler().cancelTask(ringBearerGame.taskId);
            taskId = 0;
        }
    }

    // ANNOUNCE
    private void announceGame() {
        Bukkit.getServer()
                .broadcastMessage(
                MCMEPVP.primarycolor
                + "The next Game starts in a few seconds!");

        Bukkit.getServer().broadcastMessage(
                MCMEPVP.primarycolor + "GameType is " + MCMEPVP.highlightcolor
                + "Ring Bearer" + MCMEPVP.primarycolor + " on Map "
                + MCMEPVP.highlightcolor + MCMEPVP.PVPMap + "!");

        Bukkit.getServer()
                .broadcastMessage(
                MCMEPVP.primarycolor
                + "Hunt down the enemy's Ring Bearer to prevent them from respawning!");

        Bukkit.getServer()
                .broadcastMessage(
                MCMEPVP.primarycolor
                + "All Participants will be assigned to a team and teleported to their spawn!");
    }

    private void chooseTeams() {
        ArrayList<Player> queued = new ArrayList<Player>();
        MCMEPVP.queue.drainTo(queued);
        Collections.shuffle(queued);

        for (Player p : queued) {
            if (p.isOnline()) {
                if (redSize == blueSize) {
                    boolean random = (Math.random() < 0.5);
                    if (random == true) {
                        if (!redHasBearer) {
                            addBearer(p, "red");
                        }
                        addTeam(p, "red");
                    } else {
                        if (!blueHasBearer) {
                            addBearer(p, "blue");
                        }
                        addTeam(p, "blue");
                    }
                } else {
                    if (redSize > blueSize) {
                        if (!blueHasBearer) {
                            addBearer(p, "blue");
                        }
                        addTeam(p, "blue");
                    }
                    if (redSize < blueSize) {
                        if (!redHasBearer) {
                            addBearer(p, "red");
                        }
                        addTeam(p, "red");
                    }
                }
            }
            MCMEPVP.queue.remove(p);
        }
    }

    private void addBearer(Player p, String team) {
        ringBearers.put(p, team);
        if (team.equals("red")) {
            redHasBearer = true;
        }
        if (team.equals("blue")) {
            blueHasBearer = true;
        }
        p.sendMessage(ChatColor.LIGHT_PURPLE
                + "You are the Ring Bearer! Stay alive for as long as possible!");
    }

    private void removeBearer(Player p, String team) {
        ringBearers.remove(p);
        if (team.equals("red")) {
            redHasBearer = false;
        }
        if (team.equals("blue")) {
            blueHasBearer = false;
        }
    }

    private void switchBearer(String team) {
        if (team.equals("red")) {
            if (redteam.getSize() > 0) {
                for (OfflinePlayer p : redteam.getPlayers()) {
                    if (p.isOnline()) {
                        Player newP = (Player) p;
                        addBearer(newP, team);
                        addTeam(newP, team);
                        newP.sendMessage(MCMEPVP.positivecolor
                                + "Your are now the RingBearer!");
                        break;
                    }
                }
            }
        }
        if (team.equals("blue")) {
            if (blueteam.getSize() > 0) {
                for (OfflinePlayer p : blueteam.getPlayers()) {
                    if (p.isOnline()) {
                        Player newP = (Player) p;
                        addBearer(newP, team);
                        addTeam(newP, team);
                        newP.sendMessage(MCMEPVP.positivecolor
                                + "Your are now the RingBearer!");
                        break;
                    }
                }
            }
        }
    }

    private void teamCount() {
        redSize = redteam.getSize();
        blueSize = blueteam.getSize();
        redscore.setScore(redSize);
        bluescore.setScore(blueSize);
    }

    // BOOLEANS ETC
    @Override
    public void claimLootSign(Sign sign) {
        // TODO Auto-generated method stub
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
    public HashMap<?, ?> getPlaying() {
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
    public boolean allowCustomAttributes() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void addSpectatorTeam(Player p) {
        specteam.addPlayer(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
                999999, 1));
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
