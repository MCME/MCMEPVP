package co.mcme.pvp.gametypes;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.teamUtil;
import static co.mcme.pvp.MCMEPVP.Maps;
import static co.mcme.pvp.MCMEPVP.GameTypes;

public class lobbyGame extends gameType {
	
	int m = 30;
	int mm = 120;
	
	static Float threshHold = (float) 0.75;
	static Float ratio = (float) 0;
	static int minPlayers = 6;
	static int lobbyTaskId = 0;
	
	ScoreboardManager manager;
    Scoreboard board;
    Team greenteam;
    Team whiteteam;
    Objective objective;
    OfflinePlayer dummygreen = Bukkit.getOfflinePlayer(ChatColor.GREEN + "Participants:");
    OfflinePlayer dummywhite = Bukkit.getOfflinePlayer(ChatColor.WHITE + "Spectators:");
    Score greenscore;
    Score whitescore;
    
    public lobbyGame() {
		MCMEPVP.GameStatus = 0;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        
        objective = board.registerNewObjective("Players", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        greenteam = board.registerNewTeam("Green Team");
        whiteteam = board.registerNewTeam("White Team");
        
        greenteam.setPrefix(ChatColor.GREEN.toString());
        whiteteam.setPrefix(ChatColor.WHITE.toString());
        
        greenscore = objective.getScore(dummygreen);
        whitescore = objective.getScore(dummywhite);
        
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
        		Bukkit.getPluginManager().getPlugin("MCMEPVP"),new Runnable() {
        	@Override
			public void run() {
        		Bukkit.broadcastMessage(ChatColor.GOLD + "Starting Lobby Mode");
				for (Player p : Bukkit.getOnlinePlayers()) {
    	        	addTeam(p, "spectator");
    	        	p.setScoreboard(board);
    	        }
			}
		}, 10L);
        displayBoard();
        if (MCMEPVP.autorun) {
        	AutoRunTimer();
        }
    }
    
    public void autoRun(){
    	stopLobby();
    	randomMap();
    	randomGameType();
    	gameScore();
    	
    	MCMEPVP.lastMap = MCMEPVP.PVPMap;
    	MCMEPVP.lastGT = MCMEPVP.PVPGT;
    	MCMEPVP.startGame();
    }
    
    @Override
	public void onPlayerJoin(PlayerJoinEvent event) {
    	Player p = event.getPlayer();
		whiteteam.addPlayer(p);
		
		displayBoard();
	}
    
    @Override
	public void onPlayerleaveServer(PlayerQuitEvent event) {
    	Player p = event.getPlayer();
		String status = teamUtil.getPlayerTeam(p);
		
		if (status.equals("spectator")) {
			whiteteam.removePlayer(p);
		}
		if (status.equals("participant")) {
			greenteam.removePlayer(p);
		}
		
		displayBoard();
	}

    @Override
	public void addTeam(Player p, String Team) {
		if (Team.equals("spectator")) {
			whiteteam.addPlayer(p);
		}
		if (Team.equals("participant")) {
			String status = teamUtil.getPlayerTeam(p);
			if (status.equals("participant")) {
				greenteam.removePlayer(p);
				whiteteam.addPlayer(p);
			}
			if (status.equals("spectator")) {
				whiteteam.removePlayer(p);
				greenteam.addPlayer(p);
			}
		}
		
		displayBoard();
	}
    
    public void removeTeam(Player p, String Team) {
		if (Team.equals("spectator")) {
			whiteteam.removePlayer(p);
		}
		if (Team.equals("participant")) {
			greenteam.removePlayer(p);
		}
		
		displayBoard();
	}
    
	
    private void randomMap() {
    	String map = "Tharbad";
    	
    	int max = Maps.size() - 1;
    	int i = 0;
    	
    	while (i < 1) {
    		map = Maps.get(getRandom(0, max));
    		if (!map.equals(MCMEPVP.lastMap)) {
    			i++;
    		}
    	}
    	MCMEPVP.PVPMap = map;
    }
    
    private boolean checkFlags() {
    	Plugin instance = MCMEPVP.inst();
        if (instance.getConfig().contains(MCMEPVP.PVPMap.toLowerCase() + ".Flag0")) {
        	return true;
        }
        return false;
    }
    
    private void randomGameType() {
    	String gt = "TSL";
    	
    	boolean hasFlags = checkFlags();
    	int max = GameTypes.size() -1;
    	int i = 0;
    	
    	while (i < 1) {
    		gt = GameTypes.get(getRandom(0, max));
    		if (!gt.equals(MCMEPVP.lastGT)) {
    			if (gt.equals("TCQ")) {
        			if (hasFlags) {
        				i ++;
        			}
        		} else {
        			i ++;
        		}
    		}
    	}
    	
    	MCMEPVP.PVPGT = gt;
    }
    
    private void gameScore(){
    	if (MCMEPVP.PVPGT.equals("TSL")) {
    		int i = setScore(MCMEPVP.queue.size());
            config.TSLscore = i;
            MCMEPVP.inst().saveConfig();
    	}
    	if (MCMEPVP.PVPGT.equals("TCQ")) {
    		int i = setScore(MCMEPVP.queue.size());
            config.TCQscore = i * 2;
            MCMEPVP.inst().saveConfig();
    	}
    }

    public int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }
    
    public int setScore(int size) {
        int i = 10;
        if (size <= 10) {
            i = 25;
        }
        if (size > 10 && size <= 15) {
            i = 30;
        }
        if (size > 15 && size <= 20) {
            i = 40;
            return i;
        }
        if (size > 20 && size <= 30) {
            i = 50;
            return i;
        }
        if (size > 30) {
            i = 75;
        }
        return i;
    }
    
    
    public void AutoRunTimer() {
    	objective.setDisplayName("Auto-Lobby Mode!");
        lobbyTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
        		Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {
        			
        			@Override
                    public void run() {
        				int onlinePlayers = Bukkit.getOnlinePlayers().length;
        				if (onlinePlayers >= minPlayers) {
                            if (m > 0) {
                                m--;
                                objective.setDisplayName("Starting in: " + m + "s");
                            }
                            if (m == 10) {               				
                            	if (ratio >= threshHold) {
                                	Bukkit.broadcastMessage(MCMEPVP.positivecolor + "Game starting in 10 seconds!");
                                } else {
                                	Bukkit.broadcastMessage(MCMEPVP.negativecolor + "Waiting for more players to join!");
                                	System.out.print("Ratio: " + ratio);
                                	remindJoin();
                                }
                            }
                            if (m == 0) {
                            	if (ratio >= threshHold) {
                        			Bukkit.broadcastMessage(MCMEPVP.positivecolor + "Game starting!");
                                	autoRun();
                                	Bukkit.getScheduler().cancelTask(lobbyTaskId);
                                } else {
                                	Bukkit.broadcastMessage(MCMEPVP.negativecolor + "Timer reset. Need more players to join!");
                                	remindJoin();
                                	m = 30;
                                }
                            }
        				} else {
        					if (mm > 0) {
        						mm --;
        					}
        					if (mm == 0) {
        						Bukkit.broadcastMessage(MCMEPVP.positivecolor + "AutoRun mode is enabled!");
        						Bukkit.broadcastMessage(MCMEPVP.negativecolor + "Minimum of " + minPlayers + " players required to run a game!");
        						mm = 120;
        					}
        				}
                    }
        			
        }, 20L, 20L);
    }
    
    public static void stopLobby(){
    	if (lobbyTaskId != 0) {
    		Bukkit.getScheduler().cancelTask(lobbyTaskId);
    	}
    }
    
    
    
    //UNUSED
	@Override
	public void displayBoard() {
		teamCount();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(board);
		}
	}
	
	private void teamCount(){
		greenscore.setScore(greenteam.getSize());
		whitescore.setScore(whiteteam.getSize());
		Float p = (float) greenteam.getSize();
		Float o = (float) Bukkit.getOnlinePlayers().length;
		ratio = p/o;
	}
	
	@Override
	public void clearBoard() {
		board.clearSlot(DisplaySlot.SIDEBAR);
        greenteam.unregister();
        whiteteam.unregister();
        objective.unregister();
	}
	
	public void remindJoin() {
		for (Player p : Bukkit.getOnlinePlayers()) {
            if (!MCMEPVP.isQueued(p) && !teamUtil.isOnTeam(p)) {
                p.playSound(p.getLocation(), Sound.ANVIL_LAND, 100, 100);
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "You have not joined the game yet!");
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "/pvp join");
            }
        }
	}

	@Override
	public void addPlayerDuringGame(Player p) {
		
	}
	
	@Override
	public void claimLootSign(Sign sign) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Deprecated
	public void onPlayerLogin(PlayerLoginEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerdie(PlayerDeathEvent event) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void onPlayerhit(EntityDamageByEntityEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerShoot(EntityDamageByEntityEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRespawn(PlayerRespawnEvent event) {
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
		return false;
	}

	@Override
	public void addSpectatorTeam(Player player) {
		// TODO Auto-generated method stub
		
	}

}
