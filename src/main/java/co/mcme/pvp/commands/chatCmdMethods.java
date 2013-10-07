package co.mcme.pvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.teamUtil;

public class chatCmdMethods {
	
	// ----------------//
	// PUBLIC METHODS
	// ----------------//

	//SHOUT
	public static void pvpShout(Player p, String[] a) {
		if (p.hasPermission("mcmepvp.shout")) {
			String msg = a[0];
			if (a.length > 1) {
				for (int i = 1; i < a.length; i++) {
					msg += " " + a[i];
				}
			}
			if (p.hasPermission("mcmepvp.adminshout")) {
				Bukkit.getServer().broadcastMessage(
						MCMEPVP.admincolor + "Admin " + p.getName()
								+ MCMEPVP.shoutcolor + " shouts: "
								+ MCMEPVP.primarycolor + msg);
				return;
			} else {
				if (teamUtil.getPlayerTeam(p).equals("spectator")) {
					p.sendMessage(MCMEPVP.negativecolor
							+ "Spectators aren't allowed to shout!");
					return;
				} else {
					Bukkit.getServer().broadcastMessage(
							MCMEPVP.shoutcolor + p.getName() + " shouts: "
									+ msg);
					return;
				}
			}
		} else {
			nope(p);
			return;
		}
	}
	
	// ADMIN CHAT
	public static void pvpAdminChat(Player p, String[] a) {
		if (p.hasPermission("mcmepvp.adminchat")) {
			if (a.length != 0) {
				String msg = a[0];
				if (a.length > 1) {
					for (int i = 1; i < a.length; i++) {
						msg += " " + a[i];
					}
				}
				for (Player currentplayer : Bukkit.getOnlinePlayers()) {
					if (currentplayer.hasPermission("mcmepvp.adminchat")) {
						currentplayer.sendMessage(ChatColor.WHITE + "["
								+ MCMEPVP.admincolor + "A" + ChatColor.WHITE
								+ "] " + MCMEPVP.admincolor + p.getName()
								+ ": " + msg);
					}
				}
			}
			return;
		} else {
			nope(p);
			return;
		}
	}
	
	// ----------------//
	// UTILITIES
	// ----------------//

	// No perms message
	private static void nope(Player p) {
		p.sendMessage(MCMEPVP.negativecolor
				+ "Sorry, you don't have permissions to use that!");
		return;
	}

}
