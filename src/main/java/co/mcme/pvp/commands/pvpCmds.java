package co.mcme.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.config;

public class pvpCmds implements CommandExecutor {

	Plugin plugin;

	public pvpCmds(Plugin plug) {
		this.plugin = plug;
	}

	static ChatColor err = ChatColor.GRAY;
	static ChatColor prim = ChatColor.DARK_AQUA;
	static ChatColor scd = ChatColor.AQUA;

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String c, String[] a) {
		if (cs instanceof Player) {
			Player p = (Player) cs;
			if (c.equalsIgnoreCase("pvp")) {
				if (a.length == 0) {
					userCmdMethods.pvpInfo(p);
					return true;
				} else if (a.length > 0) {
					// USER COMMANDS
					if (a[0].equalsIgnoreCase("join")) {
						userCmdMethods.pvpJoin(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("leave")) {
						userCmdMethods.pvpLeave(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("version")) {
						userCmdMethods.pvpVersion(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("list")) {
						userCmdMethods.pvpList(p);
						return true;
					}
					// STAFF COMMANDS
					if (a[0].equalsIgnoreCase("unlock")) {
						staffCmdMethods.pvpUnlock(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("lock")) {
						staffCmdMethods.pvpLock(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("auto")) {
						staffCmdMethods.pvpAuto(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("map")) {
						if (a.length == 1) {
							p.sendMessage(prim + "Current Map: " + scd
									+ MCMEPVP.PVPMap);
							return true;
						}
						if (a.length >= 2) {
							String map = a[1];
							staffCmdMethods.pvpMap(p, map);
							return true;
						} else {
							p.sendMessage(err + "/pvp map <MapName>");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("gt")) {
						if (a.length == 1) {
							p.sendMessage(prim + "Current GameType: " + scd
									+ MCMEPVP.PVPGT);
							return true;
						}
						if (a.length >= 2) {
							String gt = a[1];
							staffCmdMethods.pvpGameType(p, gt);
							return true;
						} else {
							p.sendMessage(err
									+ "/pvp gt <TDM | TSL | TCQ | INF | RBR | FFA>");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("setscore")) {
						if (a.length == 1) {
							int i = 0;
							if (MCMEPVP.PVPGT.equals("TSL")) {
								i = config.TSLscore;
							}
							if (MCMEPVP.PVPGT.equals("TCQ")) {
								i = config.TCQscore;
							}
							p.sendMessage(prim + "Current Score: " + scd + i);
							return true;
						}
						if (a.length == 2) {
							String score = a[1];
							staffCmdMethods.pvpSetScore(p, score);
							return true;
						} else {
							p.sendMessage(scd + "/pvp setscore #ScoreValue");
							p.sendMessage(ChatColor.ITALIC
									+ ""
									+ err
									+ "Must have TSL or TCQ game already selected!");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("start")) {
						staffCmdMethods.pvpStart(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("stop")) {
						staffCmdMethods.pvpStop(p);
						return true;
					}
					if (a[0].equalsIgnoreCase("forecjoin")) {
						if (a.length == 2) {
							staffCmdMethods.pvpForceJoin(p, a[1]);
							return true;
						} else {
							p.sendMessage(err + "/pvp forcejoin <TeamColor>");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("horse")) {
						staffCmdMethods.pvpHorse(p);
						return true;
					}
					// DEV COMMANDS
					if (a[0].equalsIgnoreCase("add")) {
						if (a.length == 2) {
							devCmdMethods.pvpAdd(p, a[1]);
							return true;

						} else {
							p.sendMessage(err + "/pvp add <NewMapName>");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("remove")) {
						if (a.length == 2) {
							devCmdMethods.pvpRemove(p, a[1]);
							return true;

						} else {
							p.sendMessage(err + "/pvp remove <NewMapName>");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("set")) {
						if (a[1].equalsIgnoreCase("flag")) {
							if (a.length == 3) {
								devCmdMethods.pvpSetFlag(p, a[3]);
								return true;
							} else {
								p.sendMessage(err + "/pvp set flag #FlagNumber");
								return true;
							}
						}
						if (a[1].equalsIgnoreCase("region")) {
							if (a.length == 3) {
								devCmdMethods.pvpSetRegion(p, a[3]);
								return true;
							} else {
								p.sendMessage(err
										+ "/pvp set region <Eriador | Rohan | Gondor | Lothlorien>");
								return true;
							}
						} else {
							if (a.length == 2) {
								devCmdMethods.pvpSetSpawn(p, a[1]);
								return true;
							} else {
								p.sendMessage(err
										+ "/pvp set <red | blue | flag #>");
								return true;
							}
						}
					}
					if (a[0].equalsIgnoreCase("debug")) {
						devCmdMethods.pvpDebug(p);
						return true;
					}
				}
			}
			if (c.equalsIgnoreCase("vote")) {
				if (MCMEPVP.GameStatus == 0 && MCMEPVP.voteMap
						&& MCMEPVP.canJoin) {
					if (a.length == 0) {
						voteCmdMethods.pvpVoteInfo(p);
						return true;
					}
					if (a.length == 1) {
						voteCmdMethods.pvpVote(p, a[0]);
						return true;
					} else {
						p.sendMessage(err + "/vote <1 | 2>");
						return true;
					}
				} else {
					p.sendMessage(err + "Cannot run that command during a game!");
					return true;
				}
			}
			if (c.equalsIgnoreCase("shout")) {
				if (a.length > 0) {
					chatCmdMethods.pvpShout(p, a);
					return true;
				} else {
					p.sendMessage(err + "/shout <Your message here>");
					return true;
				}
			}
			if (c.equalsIgnoreCase("a")) {
				if (a.length > 0) {
					chatCmdMethods.pvpAdminChat(p, a);
					return true;
				} else {
					p.sendMessage(err + "/a <Your message here>");
					return true;
				}
			}
		}
		return false;
	}

}
