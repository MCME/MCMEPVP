package co.mcme.pvp.gametypes;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.util.armorColor;
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
    Objective objective;
    OfflinePlayer dummyred = Bukkit.getOfflinePlayer(ChatColor.RED + "Red Players:");
    OfflinePlayer dummyblue = Bukkit.getOfflinePlayer(ChatColor.BLUE + "Blue Players:");
    Score redscore;
    Score bluescore;

    public teamDeathMatchGame() {
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
            }
        }, 100L);
        MCMEPVP.setWeather();
        displayBoard();
    }

    @Override
    public void addPlayerDuringGame(Player player) {
        player.sendMessage(MCMEPVP.negativecolor + "This gametype does not allow joining during matches!");
    }

    @Override
    public void addTeam(Player player, String Team) {
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
        MCMEPVP.logKill(event);
        Player player = event.getEntity();
        String Status = teamUtil.getPlayerTeam(player);
        //TODO Log deaths
        if (Status.equals("spectator")) {
            event.setDeathMessage(MCMEPVP.primarycolor + "Spectator " + player.getName() + " was tired watching this fight!");
        }
        if (Status.equals("red")) {
            RedMates--;
            redscore.setScore(RedMates);
            event.setDeathMessage(ChatColor.RED + player.getName() + MCMEPVP.primarycolor + " was killed by " + ChatColor.BLUE + player.getKiller().getName());
            event.getDrops().add(new ItemStack(364, 1));
            redteam.removePlayer(player);
        }
        if (Status.equals("blue")) {
            BlueMates--;
            bluescore.setScore(BlueMates);
            event.setDeathMessage(ChatColor.BLUE + player.getName() + MCMEPVP.primarycolor + " was killed by " + ChatColor.RED + player.getKiller().getName());
            event.getDrops().add(new ItemStack(364, 1));
            blueteam.removePlayer(player);
        }
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
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.RED + "Red" + MCMEPVP.positivecolor + " wins "
                    + RedMates + ":" + BlueMates + "!");
            MCMEPVP.resetGame();
        } else if (RedMates <= 0) {
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
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.BLUE + "Blue" + MCMEPVP.positivecolor + " wins "
                    + BlueMates + ":" + RedMates + "!");
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
		return true;
	}
}