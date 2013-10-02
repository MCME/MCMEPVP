package co.mcme.pvp.commands.methods;

import static co.mcme.pvp.MCMEPVP.CurrentMap;
import static co.mcme.pvp.MCMEPVP.GameStatus;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.maps.Spawn;
import co.mcme.pvp.maps.pvpMap;
import co.mcme.pvp.maps.pvpMapLoader;
import co.mcme.pvp.maps.pvpMapMeta;
import co.mcme.pvp.util.util;

import com.google.gson.JsonArray;

public class mapCmdMethods {
	
	static ChatColor err = ChatColor.GRAY;
	static ChatColor prim = ChatColor.DARK_AQUA;
	static ChatColor scd = ChatColor.AQUA;
	public static JavaPlugin instance = (JavaPlugin) MCMEPVP.inst();
	
	// Set Map
	public static void setMap(Player p, String a) {
		if (p.hasPermission("mcmepvp.set")) {
			if (GameStatus == 0) {
				String map = "null";

				for (String s : MCMEPVP.Maps) {
					if (s.toLowerCase().contains(a.toLowerCase())) {
						map = s;
						break;
					}
				}
				if (!map.equals("null")) {
					if (!CurrentMap.getName().equals(map)) {
						pvpMapLoader.loadMap(map);
						util.notifyAdmin(p.getName(), 3, null);
					} else {
						p.sendMessage(MCMEPVP.positivecolor + map + scd
								+ " is already selected!");
						return;
					}
				} else {
					p.sendMessage(MCMEPVP.negativecolor + "'" + a
							+ "' is not a valid Map!");
					return;
				}
			} else {
				p.sendMessage(MCMEPVP.negativecolor
						+ "Can't change maps while a game is running!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}
	// TP To Map
	public static void tpMap(Player p, String a) {
		if (p.hasPermission("mcmepvp.tp")) {
			if (a.equalsIgnoreCase("spectator")
					|| a.equalsIgnoreCase("blue")
					|| a.equalsIgnoreCase("red")) {
				pvpMapMeta meta = CurrentMap.getMapMeta();
				Location loc = meta.getSpawn(a.toLowerCase()).toLocation();
				p.teleport(loc);
				p.sendMessage(prim + "Teleported!");
				return;
			} else {
				p.sendMessage(err + a + " is not a valid spawn");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}
	// Create New Map
	public static void addMap(Player p, String a) {
		if (p.hasPermission("mcmepvp.add")) {
			if (instance.getConfig().getStringList("maps").contains(a)) {
				p.sendMessage(scd + "Map already exists!");
				return;
			} else {
				//Add map to Maps list
				List<String> newMaps = MCMEPVP.Maps;
				newMaps.add(a);
				Collections.sort(newMaps);
				instance.getConfig().set("maps", newMaps);
				instance.saveConfig();
				
				//Generate new map file + set CurrentMap
				if (genMapFile(a)) {
					util.notifyAdmin(p.getName(), 6, new String[] { a });
					util.notifyAdmin(p.getName(), 3, null);
					return;
				} else {
					p.sendMessage(err + "Something went wrong!");
					return;
				}
			}
		} else {
			nope(p);
			return;
		}
	}
	public static boolean genMapFile(String name) {
		File file = new File(instance.getDataFolder() 
				+ File.separator + "maps" + File.separator + name + ".map");
		if (file.exists()) {
			return false;
		} else {
			try {
				file.createNewFile();
				JsonArray data = pvpMapLoader.getDummyMap();
				pvpMapMeta meta = new pvpMapMeta(data, file);
				meta.setName(name);
				meta.writeToFile();
				
				CurrentMap = new pvpMap(meta);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	// Remove
	public static void removeMap(Player p, String a) {
		if (p.hasPermission("mcmepvp.remove")) {
			String map = "null";
			for (String s : MCMEPVP.Maps) {
				if (s.equals(a)) {
					map = s;
					break;
				}
			}
			if (!map.equals("null")) {
				MCMEPVP.Maps.remove(map);
				Configuration conf = instance.getConfig();
				List<String> maps = conf.getStringList("maps");
				maps.remove(map);
				conf.set("maps", maps);
				
				instance.saveConfig();
				String[] msg = new String[1];
				msg[0] = map;
				util.notifyAdmin(p.getName(), 7, msg);
				return;
			} else {
				p.sendMessage(err + a + " is not a PVP map!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}
	// Set Flag
	public static void setFlag(Player p, String a) {
		if (p.hasPermission("mcmepvp.set")) {
			if (isInt(a)) {
				int i = Integer.valueOf(a);
				if (i >= 0 || i <= 5) {
					Vector vec = p.getLocation().toVector();
					String[] msg = new String[2];
					msg[0] = CurrentMap.getName();
					msg[1] = String.valueOf(i);
					vec.setY(vec.getY() - 2);
					
					pvpMapMeta meta = CurrentMap.getMapMeta();
					meta.setFlag(i, vec);
					meta.writeToFile();
					
					util.notifyAdmin(p.getName(), 8, msg);
					return;
				} else {
					p.sendMessage(scd + "Flags must range between 0 and 5");
					return;
				}
			} else {
				p.sendMessage(scd + a + " is not an Integer!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}
	// Set Region
	public static void setRegion(Player p, String a) {
		if (p.hasPermission("mcmepvp.set")) {
			if (a.equalsIgnoreCase("Eriador")
                    || a.equalsIgnoreCase("Rohan")
                    || a.equalsIgnoreCase("Lothlorien")
                    || a.equalsIgnoreCase("Gondor")) {
				
				pvpMapMeta meta = CurrentMap.getMapMeta();
				meta.setRegion(a);
				meta.writeToFile();
				
				p.sendMessage(MCMEPVP.primarycolor + "Saved region " + a + " for " + CurrentMap.getName() + "'!");
                return;
			} else {
				p.sendMessage(scd + a + " is not a valid region!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}
	// Set Spawn
	public static void setSpawn(Player p, String a) {
		if (p.hasPermission("mcmepvp.set")) {
			
			String team = a.toLowerCase();
			if (team.equals("blue") || team.equals("red")
					|| team.equals("green") || team.equals("purple")
					|| team.equals("spectator")) {
				
				Vector vec = p.getLocation().toVector();
				double sx = vec.getX();
				double sy = vec.getY();
				double sz = vec.getZ();
				float spitch = p.getEyeLocation().getPitch();
				float syaw = p.getEyeLocation().getYaw();
				
				Spawn newSpawn = new Spawn(team, sx, sy, sz, spitch, syaw);
				
				pvpMapMeta meta = CurrentMap.getMapMeta();
				meta.setSpawn(team, newSpawn);
				meta.writeToFile();
				
				p.sendMessage(MCMEPVP.primarycolor
						+ "Saved your Location as Team " + team
						+ "'s Spawn for Map '" + CurrentMap.getName() + "'!");
			} else {
				p.sendMessage(scd + "That team name is not supported!");
				return;
			}
		} else {
			nope(p);
			return;
		}
	}
	
	
	// ----------------//
	// UTILITIES
	// ----------------//
	
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
