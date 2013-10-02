package co.mcme.pvp.commands.methods;

import static co.mcme.pvp.MCMEPVP.CurrentGame;
import static co.mcme.pvp.MCMEPVP.CurrentLobby;
import static co.mcme.pvp.MCMEPVP.GameStatus;
import static co.mcme.pvp.MCMEPVP.GameTypes;
import static co.mcme.pvp.MCMEPVP.PVPGT;
import static co.mcme.pvp.MCMEPVP.Participants;
import static co.mcme.pvp.MCMEPVP.locked;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gametypes.teamConquestGame;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.util;

public class staffCmdMethods {

	Plugin plugin;

	public staffCmdMethods(Plugin plug) {
		this.plugin = plug;
	}

	static ChatColor scd = ChatColor.GRAY;

	// ----------------//
	// PUBLIC METHODS
	// ----------------//

	// UNLOCK
	public static void pvpUnlock(Player p) {
		if (p.hasPermission("mcmepvp.unlock")) {
			locked = false;
			util.notifyAdmin(p.getName(), 9, null);
			return;
		} else {
			nope(p);
			return;
		}
	}

	// LOCK
	public static void pvpLock(Player p) {
		if (p.hasPermission("mcmepvp.lock")) {
			for (Player q : Bukkit.getOnlinePlayers()) {
				if (!q.hasPermission("mcmepvp.ignorelock")) {
					q.kickPlayer("PVP is over! Come Back Soon!");
				}
			}
			locked = true;
			util.notifyAdmin(p.getName(), 10, null);
			return;
		} else {
			nope(p);
			return;
		}
	}

	// AUTO
	public static void pvpAuto(Player p) {
		if (p.hasPermission("mcmepvp.autorun")) {
			if (MCMEPVP.GameStatus == 0) {
				CurrentLobby.clearBoard();
				String[] msg = new String[1];
				if (MCMEPVP.autorun) {
					MCMEPVP.autorun = false;
					MCMEPVP.resetGame();
					msg[0] = "disabled";
				} else {
					MCMEPVP.autorun = true;
					MCMEPVP.resetGame();
					msg[0] = "enabled";
				}
				util.notifyAdmin(p.getName(), 14, msg);
				return;
			}
		} else {
			nope(p);
			return;
		}
	}

	// GAMETYPE
	public static void pvpGameType(Player p, String gt) {
		if (p.hasPermission("mcmepvp.gt")) {
			if (GameStatus == 0) {
				boolean isValid = false;
				String newGt = PVPGT;

				for (String s : GameTypes) {
					if (s.equalsIgnoreCase(gt)) {
						newGt = s;
						isValid = true;
					}
				}

				if (isValid) {
					if (!PVPGT.equals(newGt)) {
						PVPGT = newGt;
						util.notifyAdmin(p.getName(), 4, null);
					} else {
						p.sendMessage(MCMEPVP.positivecolor + newGt + scd
								+ " is already selected!");
					}
					return;
				} else {
					p.sendMessage(MCMEPVP.negativecolor + "'" + gt
							+ "' is not a valid GameType!");
					return;
				}
			} else {
				p.sendMessage(MCMEPVP.negativecolor
						+ "Can't change gametypes while a game is running!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}

	// SETSCORE
	public static void pvpSetScore(Player p, String score) {
		if (p.hasPermission("mcmepvp.setscore")) {
			if (isInt(score)) {
				boolean gameon = false;
				int i = Integer.valueOf(score);
				String gt = PVPGT;

				if (GameStatus == 1) {
					gameon = true;
				}
				if (gt.equals("TSL")) {
					if (gameon) {
						MCMEPVP.CurrentGame.getObjective().setDisplayName(
								"Score: " + i);
						Bukkit.broadcastMessage(MCMEPVP.positivecolor
								+ "To win, you must now score "
								+ MCMEPVP.highlightcolor + score
								+ MCMEPVP.positivecolor + " points.");
					}
					String[] msg = new String[2];
					msg[0] = "TSL";
					msg[1] = score;
					config.TSLscore = i;
					MCMEPVP.inst().getConfig().set("score.TSL", i);
					MCMEPVP.inst().saveConfig();

					util.notifyAdmin(p.getName(), 5, msg);
				}
				if (gt.equals("TCQ")) {
					if (gameon) {
						teamConquestGame.setNewScore(i);
						Bukkit.broadcastMessage(MCMEPVP.positivecolor
								+ "Team lives updated!");
					}
					String[] msg = new String[2];
					msg[0] = "TCQ";
					msg[1] = score;
					config.TCQscore = i;
					MCMEPVP.inst().getConfig().set("score.TCQ", i);
					MCMEPVP.inst().saveConfig();

					util.notifyAdmin(p.getName(), 5, msg);
				}
			} else {
				p.sendMessage(MCMEPVP.negativecolor + "/pvp setscore #score");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}

	// START
	public static void pvpStart(Player p) {
		if (p.hasPermission("mcmepvp.start")) {
			if (GameStatus == 0) {
				if (Participants >= 2) {
					if (MCMEPVP.autorun) {
						CurrentLobby.autoRun();
						return;
					} else {
						MCMEPVP.startGame();
						return;
					}
				} else {
					p.sendMessage(MCMEPVP.negativecolor
							+ "There need to be at least two participants!");
					return;
				}
			} else {
				p.sendMessage(MCMEPVP.negativecolor + "Game already running!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}

	// STOP
	public static void pvpStop(Player p) {
		if (p.hasPermission("mcmepvp.stop")) {
			Bukkit.getServer().broadcastMessage(
					MCMEPVP.highlightcolor
							+ "The PVP Event has been aborted by an admin!");
			MCMEPVP.resetGame();
			return;
		} else {
			nope(p);
			return;
		}
	}

	// FORCEJOIN
	public static void pvpForceJoin(Player p, String a) {
		if (p.hasPermission("mcmepvp.forcejoin")) {
			if (GameStatus == 1) {
				if (a.equalsIgnoreCase("blue") || a.equalsIgnoreCase("red")) {
					CurrentGame.addTeam(p, a.toLowerCase());
					util.notifyAdmin(p.getName(), 11, new String[] { a });
					return;
				} else {
					p.sendMessage(MCMEPVP.negativecolor + a
							+ " team not recognised!");
					return;
				}
			} else {
				p.sendMessage(MCMEPVP.negativecolor
						+ "No game running, no team to join.");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}

	// HORSEMODE
	public static void pvpHorse(Player p) {
		if (p.hasPermission("mcmepvp.horsemode")) {
			if (MCMEPVP.horseMode) {
				MCMEPVP.horseMode = false;
				p.sendMessage(ChatColor.GRAY + "Horse mode Disabled!");
				return;
			} else {
				MCMEPVP.horseMode = true;
				p.sendMessage(ChatColor.GRAY + "Horse mode Enabled!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}
	
	// REMIND
	public static void pvpRemind(Player p, String a) {
		if (p.hasPermission("mcmepvp.remind")) {
			if (a.equalsIgnoreCase("stats")) {
				for (Player q : Bukkit.getOnlinePlayers()) {
                    q.sendMessage(MCMEPVP.primarycolor + "Don't forget to check your stats at " 
				+ MCMEPVP.highlightcolor + "mcme.co/pvp/stats/" + q.getName());
                }
				return;
			}
			if (a.equalsIgnoreCase("join")) {
				ArrayList<String> notjoined = new ArrayList<String>();
                for (Player q : Bukkit.getOnlinePlayers()) {
                    if (!MCMEPVP.isQueued(p) && !teamUtil.isOnTeam(q)) {
                        q.playSound(q.getLocation(), Sound.ZOMBIE_WOODBREAK, 100, 100);
                        q.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "You have not joined the game yet!");
                        notjoined.add(q.getName());
                    }
                }
                p.playSound(p.getLocation(), Sound.BURP, 100, 100);
                p.sendMessage(prettyPrint(notjoined, "Not Joined:"));
                return;
			} else {
				p.sendMessage(scd + "/pvp remind <Stats | Join>");
				return;
			}
		} else {
			nope(p);
			return;
		}
		
	}
	
	// DEBUG
	public static void pvpDebug(Player p) {
		if (p.hasPermission("mcmepvp.setdebug")) {
            if (MCMEPVP.gameDebug) {
                MCMEPVP.gameDebug = false;
                util.notifyAdmin(p.getName(), 13, null);
                return;
            } else {
                MCMEPVP.gameDebug = true;
                util.notifyAdmin(p.getName(), 12, null);
                return;
            }
        }
	}

	// ----------------//
	// UTILITIES
	// ----------------//
	
	// Pretty print
	private static String prettyPrint(List<String> list, String title) {
		StringBuilder out = new StringBuilder();
		out.append(MCMEPVP.primarycolor).append(title);
		for (String item : list) {
			out.append("\n").append(MCMEPVP.primarycolor).append("- ")
					.append(MCMEPVP.highlightcolor).append(item);
		}
		return out.toString();
	}

	// Check if string is integer
	private static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// No perms message
	private static void nope(Player p) {
		p.sendMessage(MCMEPVP.negativecolor
				+ "Sorry, you don't have permissions to use that!");
		return;
	}

}
