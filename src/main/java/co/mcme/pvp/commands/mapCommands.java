package co.mcme.pvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.commands.methods.mapCmdMethods;

public class mapCommands implements CommandExecutor {
	
	Plugin plugin;

	public mapCommands(Plugin plug) {
		this.plugin = plug;
	}
	
	static ChatColor err = ChatColor.GRAY;
	static ChatColor prim = ChatColor.DARK_AQUA;
	static ChatColor scd = ChatColor.AQUA;

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String c, String[] a) {
		if (cs instanceof Player) {
			Player p = (Player) cs;
			
			if (c.equalsIgnoreCase("map")) {
				if (a.length == 0) {
					p.sendMessage(prim + "Current Map: " + scd
							+ MCMEPVP.CurrentMap.getName());
					return true;
				}
				if (a.length == 1) {
					if (a[0].equalsIgnoreCase("tp")) {
						String s = "spectator";
						mapCmdMethods.tpMap(p, s);
						return true;
					} else {
						mapCmdMethods.setMap(p, a[0]);
						return true;
					}
				}
				if (a.length >= 2) {
					if (a[0].equalsIgnoreCase("tp")) {
						mapCmdMethods.tpMap(p, a[1]);
						return true;
					}
					if (a[0].equalsIgnoreCase("add")) {
						if (a.length == 2) {
							mapCmdMethods.addMap(p, a[1]);
							return true;
						} else {
							p.sendMessage(err + "/pvp add <NewMapName>");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("remove")) {
						if (a.length == 2) {
							mapCmdMethods.removeMap(p, a[1]);
							return true;
						} else {
							p.sendMessage(err + "/pvp remove <MapName>");
							return true;
						}
					}
					if (a[0].equalsIgnoreCase("set")) {
						if (a[1].equalsIgnoreCase("flag")) {
							if (a.length == 3) {
								mapCmdMethods.setFlag(p, a[2]);
								return true;
							} else {
								p.sendMessage(err + "/pvp set flag #FlagNumber");
								return true;
							}
						}
						if (a[1].equalsIgnoreCase("region")) {
							if (a.length == 3) {
								mapCmdMethods.setRegion(p, a[2]);
								return true;
							} else {
								p.sendMessage(err
										+ "/pvp set region <Eriador | Rohan | Gondor | Lothlorien>");
								return true;
							}
						} else {
							if (a.length == 2) {
								mapCmdMethods.setSpawn(p, a[1]);
								return true;
							} else {
								p.sendMessage(err
										+ "/pvp set <red | blue | flag #>");
								return true;
							}
						}
					}
				} else {
					p.sendMessage(err + "herpderp");
					return true;
				}
			}
		}
		return false;
	}

}
