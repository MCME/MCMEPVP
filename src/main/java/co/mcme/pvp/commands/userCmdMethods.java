package co.mcme.pvp.commands;

import static co.mcme.pvp.MCMEPVP.GameStatus;
import static co.mcme.pvp.MCMEPVP.queuePlayer;
import static co.mcme.pvp.MCMEPVP.unQueuePlayer;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.teamUtil;

public class userCmdMethods {

	// ----------------//
	// PUBLIC METHODS
	// ----------------//

	// INFO
	public static void pvpInfo(Player p) {
		String ll = ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
		String lb = ChatColor.DARK_GRAY + " [" + ChatColor.GRAY;
		String rb = ChatColor.DARK_GRAY + "]";
		if (p.hasPermission("mcmepvp.user")) {
			p.sendMessage(ChatColor.DARK_AQUA + "|--------[Commands]--------|");
			p.sendMessage(ChatColor.GRAY + "/pvp" + lb + "join" + ll + "leave"
					+ ll + "list" + rb);
		}
		if (p.hasPermission("mcmepvp.admin")) {
			p.sendMessage(ChatColor.GRAY + "/pvp" + lb + "map <MapName>" + ll
					+ "gt <GameType>" + rb);
			p.sendMessage(ChatColor.GRAY + "/pvp" + lb
					+ "SetScore <TSL/TCQ> #ScoreValue" + rb);
			p.sendMessage(ChatColor.GRAY + "/pvp" + lb + "start" + ll + "stop"
					+ ll + "forcejoin <TeamColor>" + rb);
			p.sendMessage(ChatColor.GRAY + "/pvp derp" + lb + "g" + ll + "m"
					+ ll + "s" + rb);
		}
		if (p.hasPermission("mcmepvp.user")) {
			p.sendMessage(ChatColor.GRAY + "/shout <YourMessage>");
		}
		return;
	}

	// JOIN
	public static void pvpJoin(Player p) {
		if (p.hasPermission("mcmepvp.join")) {
			if (MCMEPVP.canJoin) {
				queuePlayer(p);
				return;
			} else {
				p.sendMessage(MCMEPVP.negativecolor
						+ "Can't join while game is loading!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}

	// LEAVE
	public static void pvpLeave(Player p) {
		if (p.hasPermission("mcmepvp.leave")) {
			if (MCMEPVP.canJoin) {
				if (!teamUtil.isOnTeam(p) && GameStatus == 0) {
					unQueuePlayer(p);
					return;
				} else if (GameStatus == 1) {
					p.sendMessage(MCMEPVP.negativecolor
							+ "You cannot leave a game that is already running!");
					return;
				}
			} else {
				p.sendMessage(MCMEPVP.negativecolor
						+ "Can't leave while game is loading!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}

	// VERSION
	public static void pvpVersion(Player p) {
		if (p.hasPermission("mcmepvp.version")) {
			String version = Bukkit.getServer().getPluginManager()
					.getPlugin("MCMEPVP").getDescription().getVersion();
			p.sendMessage(MCMEPVP.primarycolor + "Currently running version: "
					+ MCMEPVP.highlightcolor + version);
			return;
		} else {
			nope(p);
			return;
		}
	}

	// LIST
	public static void pvpList(Player p) {
		if (p.hasPermission("mcmepvp.list")) {
			List<String> list = MCMEPVP.Maps;
			Collections.sort(list);
			p.sendMessage(prettyPrint(list, "Maps:"));
			return;
		} else {
			nope(p);
			return;
		}

	}

	// ----------------//
	// UTILITIES
	// ----------------//

	private static String prettyPrint(List<String> list, String title) {
		StringBuilder out = new StringBuilder();
		out.append(MCMEPVP.primarycolor).append(title);
		for (String item : list) {
			out.append("\n").append(MCMEPVP.primarycolor).append("- ")
					.append(MCMEPVP.highlightcolor).append(item);
		}
		return out.toString();
	}

	private static void nope(Player p) {
		p.sendMessage(MCMEPVP.negativecolor
				+ "Sorry, you don't have permissions to use that!");
		return;
	}
}
