package co.mcme.pvp.lobby;

import static co.mcme.pvp.MCMEPVP.GameTypes;
import static co.mcme.pvp.MCMEPVP.Maps;

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
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.teamUtil;

public class lobbyMode extends lobbyType {

	int m = 30;
	int mm = 120;

	static Float threshHold = (float) config.startThreshHold;
	static Float ratio = (float) 0;
	static int minPlayers = config.minOnlinePlayers;
	static int lobbyTaskId = 0;

	ScoreboardManager manager;
	Scoreboard board;
	Team greenteam;
	Team whiteteam;
	Objective objective;
	OfflinePlayer dummygreen = Bukkit.getOfflinePlayer(ChatColor.GREEN
			+ "Participants:");
	OfflinePlayer dummywhite = Bukkit.getOfflinePlayer(ChatColor.WHITE
			+ "Spectators:");
	Score greenscore;
	Score whitescore;

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
							}
						}, 10L);
		displayBoard();
		if (config.autorun) {
			autoRunTimer();
		}
	}

	public void autoRun() {
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
			}
		}

		displayBoard();
	}

	@Override
	public void randomMap() {
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

	@Override
	public boolean checkFlags() {
		Plugin instance = MCMEPVP.inst();
		if (instance.getConfig().contains(
				MCMEPVP.PVPMap.toLowerCase() + ".Flag0")) {
			return true;
		}
		return false;
	}

	@Override
	public void randomGameType() {
		String gt = "TSL";

		boolean hasFlags = checkFlags();
		int max = GameTypes.size() - 1;
		int i = 0;

		while (i < 1) {
			gt = GameTypes.get(getRandom(0, max));
			if (!gt.equals(MCMEPVP.lastGT)) {
				if (gt.equals("TCQ")) {
					if (hasFlags) {
						i++;
					}
				} else {
					i++;
				}
			}
		}

		MCMEPVP.PVPGT = gt;
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
											System.out.print("Ratio: " + ratio);
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
											m = 30;
										}
									}
								} else {
									if (mm > 0) {
										mm--;
									}
									if (mm == 0) {
										Bukkit.broadcastMessage(MCMEPVP.positivecolor
												+ "AutoRun mode is enabled!");
										Bukkit.broadcastMessage(MCMEPVP.negativecolor
												+ "Minimum of "
												+ minPlayers
												+ " players required to run a game!");
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
				p.playSound(p.getLocation(), Sound.ANVIL_LAND, 100, 100);
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED
						+ "You have not joined the game yet!");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN
						+ "/pvp join");
			}
		}
	}

	@Override
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(MCMEPVP.Spawn);
	}

}
