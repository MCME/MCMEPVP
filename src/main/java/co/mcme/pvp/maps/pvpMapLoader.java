package co.mcme.pvp.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class pvpMapLoader {
	
	public pvpMapLoader() {
		
	}
	
	public static void checkMapFolder() {
		Plugin ins = MCMEPVP.inst();
		File mapFolder = new File(ins.getDataFolder() + File.separator + "maps" + File.separator);
		if (!mapFolder.exists()) {
			mapFolder.mkdir();
			util.info("Generating new maps folder.");
		} else {
			util.info("Found maps folder.");
		}
	}

	public static void loadMap(String s) {
		Plugin ins = MCMEPVP.inst();

		String map = "null";
		File mapFolder = new File(ins.getDataFolder() + File.separator + "maps");
		if (mapFolder.exists()) {
			for (String f : mapFolder.list()) {
				if (f.replace(".map", "").equalsIgnoreCase(s)) {
					map = f;
					break;
				}
			}
			if (!map.equals("null")) {
				File mapFile = new File(ins.getDataFolder() + File.separator + "maps" + File.separator + map);
				String rFile = readFile(mapFile.getPath());

				JsonParser parser = new JsonParser();
				JsonArray dat = (JsonArray) parser.parse(rFile);

				pvpMapMeta meta = new pvpMapMeta(dat, mapFile);
				MCMEPVP.CurrentMap = new pvpMap(meta);
			}
		}
	}

	private static String readFile(String file) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			reader.close();
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
		return stringBuilder.toString();
	}

	public static void getMapInfo(pvpMapMeta meta) {
		String name = meta.getName();
		Bukkit.broadcastMessage(ChatColor.GREEN + name);

		Spawn redSpawn = meta.getSpawn("red");
		Bukkit.broadcastMessage(ChatColor.RED + "Spawn:");
		Bukkit.broadcastMessage(redSpawn.toVector().toString());

		Spawn blueSpawn = meta.getSpawn("blue");
		Bukkit.broadcastMessage(ChatColor.BLUE + "Spawn:");
		Bukkit.broadcastMessage(blueSpawn.toVector().toString());

		for (int i : meta.getFlags().keySet()) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + "Flag " + i + ":");
			Bukkit.broadcastMessage(meta.getFlags().get(i).toString());
		}
	}
	
	public static JsonArray getDummyMap() {
		JsonArray data;
		String name = "dummy";
	    String region = "Eriador";
	    HashMap<String, Spawn> spawns = new HashMap<String, Spawn>();
	    HashMap<Integer, Vector> flags = new HashMap<Integer, Vector>();
	    
		JsonArray mapArr = new JsonArray();
    	JsonObject mapObj = new JsonObject();
    	
    	JsonArray flagsArr = new JsonArray();
    	JsonObject flagObj = new JsonObject();
    	for (Integer i : flags.keySet()) {
    		Vector vec = flags.get(i);
			double x = vec.getX();
			double y = vec.getY();
			double z = vec.getZ();
			JsonObject newFlag = new JsonObject();
			newFlag.addProperty("x", x);
			newFlag.addProperty("y", y);
			newFlag.addProperty("z", z);
			
			flagObj.add(i.toString(), newFlag);
    	}
    	flagsArr.add(flagObj);
    	
    	JsonArray spawnsArr = new JsonArray();
    	JsonObject spawnObj = new JsonObject();
    	for (String s : spawns.keySet()) {
			Spawn spawn = spawns.get(s);
			Location loc = spawn.toLocation();
			
			float pitch = loc.getPitch();
			float yaw = loc.getYaw();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();
			JsonObject newSpawn = new JsonObject();
			newSpawn.addProperty("pitch", pitch);
			newSpawn.addProperty("yaw", yaw);
			newSpawn.addProperty("x", x);
			newSpawn.addProperty("y", y);
			newSpawn.addProperty("z", z);
			
			spawnObj.add(s, newSpawn);
    	}
    	spawnsArr.add(spawnObj);
    	
    	mapObj.addProperty("name", name);
    	mapObj.addProperty("region", region);
    	mapObj.add("flags", flagsArr);
    	mapObj.add("spawns", spawnsArr);
    	
    	mapArr.add(mapObj);
    	data = mapArr;
    	return data;
	}

}
