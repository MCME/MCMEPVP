package co.mcme.pvp.gametypes;

import static co.mcme.pvp.MCMEPVP.CurrentMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
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

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.util.armorColor;
import co.mcme.pvp.util.gearGiver;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.textureSwitcher;
import co.mcme.pvp.util.util;

public class teamDeathMatchGame extends gameType {

    private String map = CurrentMap.getName();
    
    private int RedMates = 0;
    private int BlueMates = 0;
    public Plugin plugin;
    boolean isTharbad = map.equalsIgnoreCase("tharbad");
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

    public teamDeathMatchGame() {    	
        MCMEPVP.GameStatus = 1;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        
        redteam = board.registerNewTeam("Red Team");
        redteam.setPrefix(ChatColor.RED.toString());
        redteam.setAllowFriendlyFire(false);
        
        blueteam = board.registerNewTeam("Blue Team");
        blueteam.setPrefix(ChatColor.BLUE.toString());
        blueteam.setAllowFriendlyFire(false);
        
        objective = board.registerNewObjective("Score", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        redscore = objective.getScore(dummyred);
        bluescore = objective.getScore(dummyblue);
        
        specteam = board.registerNewTeam("Spectator Team");
        specteam.setAllowFriendlyFire(false);
        specteam.setCanSeeFriendlyInvisibles(true);
        
        //Broadcast
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "The next Game starts in a few seconds!");
        Bukkit.getServer().broadcastMessage(MCMEPVP.primarycolor + "GameType is " + MCMEPVP.highlightcolor + "Team Deathmatch" + MCMEPVP.primarycolor + " on Map " + MCMEPVP.highlightcolor + map + "!");
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
    		if(player.getActivePotionEffects() != null){
            	for(PotionEffect pe : player.getActivePotionEffects()){
            		player.removePotionEffect(pe.getType());
            	}
            }
    	}
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
        Location loc = CurrentMap.getMapMeta().getSpawn(teamUtil.getPlayerTeam(player)).toLocation();
        player.teleport(loc);
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (MCMEPVP.GameStatus == 1) {
            Player player = event.getPlayer();
            String Team = teamUtil.getPlayerTeam(player);
            if (Team.equals("spectator")) {
                spectatorUtil.setSpectator(player);
                addSpectatorTeam(player);
            }
            Location loc = CurrentMap.getMapMeta().getSpawn(teamUtil.getPlayerTeam(player)).toLocation();
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
            event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 1));
            redteam.removePlayer(player);
        }
        if (Status.equals("blue")) {
            BlueMates--;
            bluescore.setScore(BlueMates);
            victim = ChatColor.BLUE + player.getName();
            event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 1));
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
        	deathMessage = victim + MCMEPVP.primarycolor + " was killed by " + killer;
        }
        event.setDeathMessage(deathMessage);
        teamUtil.setPlayerTeam(player, "spectator");
        checkGameEnd();
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        spectatorUtil.setSpectator(player);
        addSpectatorTeam(player);
        Location loc = CurrentMap.getMapMeta().getSpawn(teamUtil.getPlayerTeam(player)).toLocation();
        event.setRespawnLocation(loc);
    }

    @Override
    public void onPlayerhit(EntityDamageByEntityEvent event) {
    	// Unused
    }

    @Override
    public void onPlayerShoot(EntityDamageByEntityEvent event) {
    	// Unused
    }

    private void checkGameEnd() {
        if (BlueMates <= 0) {
            MCMEPVP.logGame("red", map, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                util.debug("player: " + key + " Team: " + value);
                if (value.equalsIgnoreCase("red")) {
                    MCMEPVP.logJoin(key, map, MCMEPVP.PVPGT, true);
                } else {
                    MCMEPVP.logJoin(key, map, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.RED + "Red" + MCMEPVP.positivecolor + " wins "
                    + RedMates + ":" + BlueMates + "!");
            MCMEPVP.winFireworks("red");
            MCMEPVP.resetGame();
        } else if (RedMates <= 0) {
            MCMEPVP.logGame("blue", map, MCMEPVP.PVPGT);
            for (Map.Entry<String, String> entry : MCMEPVP.PlayerStatus.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                util.debug("player: " + key + " Team: " + value);
                if (value.equalsIgnoreCase("blue")) {
                    MCMEPVP.logJoin(key, map, MCMEPVP.PVPGT, true);
                } else {
                    MCMEPVP.logJoin(key, map, MCMEPVP.PVPGT, false);
                }
            }
            Bukkit.getServer().broadcastMessage(MCMEPVP.positivecolor + "Team " + ChatColor.BLUE + "Blue" + MCMEPVP.positivecolor + " wins "
                    + BlueMates + ":" + RedMates + "!");
            MCMEPVP.winFireworks("blue");
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

	@Override
	public void addSpectatorTeam(Player p) {
		if (!specteam.hasPlayer(p)) {
			specteam.addPlayer(p);
			
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,999999,1));
	}
}