package co.mcme.pvp.lobby;

import static co.mcme.pvp.MCMEPVP.GameTypes;
import static co.mcme.pvp.MCMEPVP.Maps;
import static co.mcme.pvp.MCMEPVP.voteMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
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
import co.mcme.pvp.commands.methods.voteCmdMethods;
import co.mcme.pvp.maps.pvpMapLoader;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.util;

public class lobbyMode extends lobbyType {

	int m = 30;
	int mm = 120;
	
	int map1votes = 0;
	int map2votes = 0;

	static Float threshHold = (float) config.startThreshHold;
	static Float ratio = (float) 0;
	static int minPlayers = config.minOnlinePlayers;
	static int lobbyTaskId = 0;
	static HashMap<Integer, List<String>> games = new HashMap<Integer, List<String>>();

	ScoreboardManager manager;
	Scoreboard board;
	Team greenteam;
	Team whiteteam;
	Objective objective;
	OfflinePlayer dummygreen = Bukkit.getOfflinePlayer(ChatColor.GREEN
			+ "Participants:");
	OfflinePlayer dummywhite = Bukkit.getOfflinePlayer(ChatColor.WHITE
			+ "Spectators:");
	OfflinePlayer dummymap1 = Bukkit.getOfflinePlayer(ChatColor.DARK_AQUA
			+ "Map 1:");
	OfflinePlayer dummymap2 = Bukkit.getOfflinePlayer(ChatColor.DARK_AQUA
			+ "Map 2:");
	Score greenscore;
	Score whitescore;
	Score map1score;
	Score map2score;

	public lobbyMode() {
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

		Bukkit.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(
						Bukkit.getPluginManager().getPlugin("MCMEPVP"),
						new Runnable() {
							@Override
							public void run() {
								Bukkit.broadcastMessage(ChatColor.GOLD
										+ "Starting new lobby");
								for (Player p : Bukkit.getOnlinePlayers()) {
									setTeam(p, "spectator");
									p.setScoreboard(board);
								}
								displayBoard();
								MCMEPVP.canJoin= true; 
								if (MCMEPVP.autorun) {
									if (voteMap) {
										setMapVote();
									}
								}
							}
						}, 10L);
		if (MCMEPVP.autorun) {
			voteCmdMethods.hasVoted.clear();
			autoRunTimer();
		}
	}

	public void autoRun() {
		String map = randomMap();
		String gt = randomGameType();
		
		if (MCMEPVP.autorun) {
			if (voteMap) {
				if (map1votes > map2votes) {
					map = games.get(1).get(0);
					gt = games.get(1).get(1);
					Bukkit.broadcastMessage(MCMEPVP.positivecolor + "Map 1 wins!");
				}
				if (map1votes < map2votes) {
					map = games.get(2).get(0);
					gt = games.get(2).get(1);
					Bukkit.broadcastMessage(MCMEPVP.positivecolor + "Map 2 wins!");
				}
				if (map1votes == map2votes) {
					Bukkit.broadcastMessage(MCMEPVP.positivecolor + "Voting is tied. Choosing random a game instead!");
				}
			}
		}
		
		games.clear();
		voteCmdMethods.hasVoted.clear();
		
		//TODO load map - 'map'
		pvpMapLoader.loadMap(map);
		
		
		MCMEPVP.PVPGT = gt;
		
		gameScore();

		MCMEPVP.lastMap = MCMEPVP.CurrentMap.getName();
		MCMEPVP.lastGT = MCMEPVP.PVPGT;
		
		MCMEPVP.startGame();
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		whiteteam.addPlayer(p);

		displayBoard();
		
		if (MCMEPVP.autorun) {
			final Player q = p;
			
			Bukkit.getServer()
			.getScheduler()
			.scheduleSyncDelayedTask(
					Bukkit.getPluginManager().getPlugin("MCMEPVP"),
					new Runnable() {

						@Override
						public void run() {
							getVoteMaps(q);
							playersUntilStart(q);
						}
						
					}, 20L);
		}
	}

	@Override
	public void onPlayerleaveServer(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		String status = teamUtil.getPlayerTeam(p);

		if (status.equals("spectator")) {
			if (whiteteam.hasPlayer(p)) {
				whiteteam.removePlayer(p);
			}
		}
		if (status.equals("participant")) {
			if (greenteam.hasPlayer(p)) {
				greenteam.removePlayer(p);
			}
		}

		displayBoard();
	}

	@Override
	public void setTeam(Player p, String Team) {
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
				playersUntilStart(p);
			}
		}
		displayBoard();
	}
	
	@Override
	public void setMapVote() {
		if (voteMap) {
			util.debug("(Lobby) Setting maps for voting");
			
			games.clear();
			int i = 1;
			
			while (i <= 2) {
				List<String> mapgt = new ArrayList<String>();
				String map = randomMap();
				String gt = randomGameType();
				
				if (games.containsKey(1)) {
					if (games.get(1).get(0).equals(map)) {
						util.debug("(Lobby) " + map + " already used!");
					} else {
						mapgt.add(0, map);
						mapgt.add(1, gt);
						
						games.put(i, mapgt);
						i ++;
					}
				} else {
					mapgt.add(0, map);
					mapgt.add(1, gt);
					
					games.put(i, mapgt);
					i ++;
				}
			}
			
			map1score = objective.getScore(dummymap1);
			map2score = objective.getScore(dummymap2);
			displayBoard();
			
			map1score.setScore(1);
			map2score.setScore(1);
			displayBoard();
			map1score.setScore(map1votes);
			map2score.setScore(map2votes);
			announceVoteMap();
		} else {
			games.clear();
			Bukkit.broadcastMessage(ChatColor.GRAY + "Map voting has been disabled!");
		}
	}
	
	public void announceVoteMap() {
		if (!games.isEmpty()) {
			ChatColor prim = ChatColor.DARK_AQUA;
			ChatColor scd = ChatColor.AQUA;
			
			Bukkit.broadcastMessage(prim + "|----[Map 1]----|");
			Bukkit.broadcastMessage(prim + "Map: " + scd + games.get(1).get(0));
			Bukkit.broadcastMessage(prim + "GameMode: " + scd + games.get(1).get(1));
			Bukkit.broadcastMessage(prim + "/vote 1");
			Bukkit.broadcastMessage(prim + "|--------------|");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(prim + "|----[Map 2]----|");
			Bukkit.broadcastMessage(prim + "Map: " + scd + games.get(2).get(0));
			Bukkit.broadcastMessage(prim + "GameMode: " + scd + games.get(2).get(1));
			Bukkit.broadcastMessage(prim + "/vote 2");
			Bukkit.broadcastMessage(prim + "|--------------|");
		}
	}

	@Override
	public String randomMap() {
		String map = "Tharbad";

		int max = Maps.size() - 1;
		int i = 0;

		while (i < 1) {
			String m = Maps.get(getRandom(0, max));
			if (!m.equals(MCMEPVP.lastMap)) {
				i++;
				map = m;
				util.debug("(Lobby) RandomMap: " + map);
			}
		}
		return map;
	}

	@Override
	public boolean checkFlags(String map) {
		Plugin instance = MCMEPVP.inst();
		if (instance.getConfig().contains(
				map.toLowerCase() + ".Flag0")) {
			return true;
		} else {
			util.debug("(Lobby) + " + map + " does not have TCQ flags. Skipping GT."); 
			return false;
		}
	}

	@Override
	public String randomGameType() {
		String gt = "TSL";

		int max = GameTypes.size() - 1;
		int i = 0;

		while (i < 1) {
			gt = GameTypes.get(getRandom(0, max));
			if (!gt.equals(MCMEPVP.lastGT)) {
				if (gt.equals("TCQ") || gt.equals("INF")) {
					//TODO Inc counter if map has flags
					if (MCMEPVP.CurrentMap.getFlagCount() != 0) {
						i++;
					}
				} else {
					i++;
				}
			}
		}

		return gt;
	}

	@Override
	public void gameScore() {
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

	private int getRandom(int lower, int upper) {
		Random random = new Random();
		return random.nextInt((upper - lower) + 1) + lower;
	}

	private int setScore(int size) {
		int i = 10;
		if (size <= 6) {
			i = 15;
		}
		if (size > 6 && size <= 10) {
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
	

	@Override
	public void autoRunTimer() {
		objective.setDisplayName("Auto-Lobby Mode!");
		lobbyTaskId = Bukkit
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(
						Bukkit.getPluginManager().getPlugin("MCMEPVP"),
						new Runnable() {

							@Override
							public void run() {
								int onlinePlayers = Bukkit.getOnlinePlayers().length;
								if (onlinePlayers >= minPlayers) {
									if (m > 0) {
										m--;
										objective
												.setDisplayName("Starting in: "
														+ m + "s");
									}
									if (m == 10) {
										if (ratio >= threshHold) {
											Bukkit.broadcastMessage(MCMEPVP.positivecolor
													+ "Game starting in 10 seconds!");
										} else {
											Bukkit.broadcastMessage(MCMEPVP.negativecolor
													+ "Waiting for more players to join!");
											util.debug("(Lobby) Ratio: " + ratio);
											remindJoin();
										}
									}
									if (m == 0) {
										if (ratio >= threshHold) {
											Bukkit.broadcastMessage(MCMEPVP.positivecolor
													+ "Game starting!");
											autoRun();
											Bukkit.getScheduler().cancelTask(
													lobbyTaskId);
										} else {
											Bukkit.broadcastMessage(MCMEPVP.negativecolor
													+ "Timer reset. Need more players to join!");
											remindJoin();
											if (voteMap) {
												Bukkit.broadcastMessage(MCMEPVP.highlightcolor
														+ "Use '/vote' to choose the next map and gametype!");
											}
											m = 30;
										}
									}
								} else {
									if (mm > 0) {
										mm--;
									}
									if (mm == 0) {
										int dif = minPlayers - onlinePlayers;
										Bukkit.broadcastMessage(MCMEPVP.highlightcolor
												+ "Waiting on "
												+ MCMEPVP.positivecolor
												+ dif
												+ MCMEPVP.highlightcolor
												+ " more player(s) to join!");
										mm = 120;
									}
								}
							}

						}, 20L, 20L);
	}

	@Override
	public void stopLobby() {
		if (lobbyTaskId != 0) {
			Bukkit.getScheduler().cancelTask(lobbyTaskId);
		}
		if (!games.isEmpty()) {
			games.clear();
		}
	}

	@Override
	public void displayBoard() {
		teamCount();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(board);
		}
	}

	@Override
	public void clearBoard() {
		board.clearSlot(DisplaySlot.SIDEBAR);
		greenteam.unregister();
		whiteteam.unregister();
		objective.unregister();
	}

	private void teamCount() {
		greenscore.setScore(greenteam.getSize());
		whitescore.setScore(whiteteam.getSize());
		Float p = (float) greenteam.getSize();
		Float o = (float) Bukkit.getOnlinePlayers().length;
		ratio = p / o;
	}

	private void remindJoin() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!MCMEPVP.isQueued(p) && !teamUtil.isOnTeam(p)) {
				p.playSound(p.getLocation(), Sound.ANVIL_LAND, 100, 1);
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED
						+ "You have not joined the game yet!");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED
						+ "/pvp join");
			}
		}
	}

	@Override
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(MCMEPVP.Spawn);
	}

	@Override
	public void voteMap(Integer i) {
		if (!games.isEmpty()) {
			if (i == 1) {
				map1votes ++;
			}
			if (i == 2) {
				map2votes ++;
			}
			map1score.setScore(map1votes);
			map2score.setScore(map2votes);
		}
	}
	
	private static void playersUntilStart(Player p) {
		if (MCMEPVP.autorun) {
			int i = Bukkit.getOnlinePlayers().length;
			if (i < minPlayers) {
				int dif = minPlayers - i;
				if (p.isOnline()) {
					p.sendMessage(MCMEPVP.highlightcolor + "Waiting on "
							+ MCMEPVP.positivecolor + dif + MCMEPVP.highlightcolor
							+ " more player(s) to join!");
				}
			}
		}
	}

	@Override
	public void getVoteMaps(Player p) {
		if (!games.isEmpty()) {
			ChatColor prim = ChatColor.DARK_AQUA;
			ChatColor scd = ChatColor.AQUA;
			
			p.sendMessage(prim + "|----[Map 1]----|");
			p.sendMessage(prim + "Map: " + scd + games.get(1).get(0));
			p.sendMessage(prim + "GameMode: " + scd + games.get(1).get(1));
			p.sendMessage(prim + "/vote 1");
			p.sendMessage(prim + "|--------------|");
			p.sendMessage("");
			p.sendMessage(prim + "|----[Map 2]----|");
			p.sendMessage(prim + "Map: " + scd + games.get(2).get(0));
			p.sendMessage(prim + "GameMode: " + scd + games.get(2).get(1));
			p.sendMessage(prim + "/vote 2");
			p.sendMessage(prim + "|--------------|");
		} else {
			p.sendMessage(ChatColor.GRAY + "There are no games to vote on!");
		}
		
	}

}
