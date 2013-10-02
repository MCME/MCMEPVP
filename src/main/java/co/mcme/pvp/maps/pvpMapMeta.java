package co.mcme.pvp.maps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class pvpMapMeta {

    private String name;
    private File file;
    private String region;
    private HashMap<String, Spawn> spawns = new HashMap<String, Spawn>();
    private HashMap<Integer, Vector> flags = new HashMap<Integer, Vector>();
    JsonArray data;

    public pvpMapMeta(JsonArray dat, File metafile) {
        data = dat;
        JsonObject mapObj = dat.get(0).getAsJsonObject();
        name = mapObj.get("name").getAsString();
        // Load region
        if (mapObj.has("region")) {
        	region = mapObj.get("region").getAsString();
        }
        // Load flags
        if (mapObj.has("flags")) {
        	JsonObject flags_ = mapObj.get("flags").getAsJsonArray().get(0).getAsJsonObject();
            for (Map.Entry<String, JsonElement> flag : flags_.entrySet()) {
                JsonObject coords = flag.getValue().getAsJsonObject();
                flags.put(Integer.valueOf(flag.getKey()), new Vector(coords.get("x").getAsDouble(), coords.get("y").getAsDouble(), coords.get("z").getAsDouble()));
            }
        }
     // Load spawns
        if (mapObj.has("spawns")) {
        	JsonObject spawns_ = mapObj.get("spawns").getAsJsonArray().get(0).getAsJsonObject();
            for (Map.Entry<String, JsonElement> spawn : spawns_.entrySet()) {
                JsonObject spawndat = spawn.getValue().getAsJsonObject();
                String spawnname = spawn.getKey();
                Spawn spawnloc = new Spawn(spawnname, spawndat.get("x").getAsDouble(),
                        spawndat.get("y").getAsDouble(), spawndat.get("z").getAsDouble(),
                        spawndat.get("pitch").getAsFloat(), spawndat.get("yaw").getAsFloat());
                spawns.put(spawnname, spawnloc);
            }
        }
        file = metafile;
    }

    public String getName() {
        return name;
    }
    
    public String getRegion() {
        return region;
    }

    public HashMap<String, Spawn> getSpawns() {
        return spawns;
    }

    public Spawn getSpawn(String team) {
        return spawns.get(team);
    }

    public HashMap<Integer, Vector> getFlags() {
        return flags;
    }

    public Vector getFlagVector(int flagid) {
        return flags.get(flagid);
    }
    
    // Set Stuff
    public void setName(String newName) {
    	name = newName;
    	constructNewData();
    	return;
    }
    
    public void setRegion(String newRegion) {
    	region = newRegion;
    	constructNewData();
    	return;
    }
    
    public void setSpawn(String team, Spawn newSpawn) {
    	if (spawns.containsKey(team)) {
    		spawns.remove(team);
    	}
    	spawns.put(team, newSpawn);
    	constructNewData();
    	return;
    }
    
    public void setFlag(Integer i, Vector newVector) {
    	if (flags.containsKey(i)) {
    		flags.remove(i);
    	}
    	flags.put(i, newVector);
    	constructNewData();
    	return;
    }
    
    public void constructNewData() {
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
    }

    public void writeToFile() {
        Gson builder = new GsonBuilder().setPrettyPrinting().create();
        
        String jsonString = builder.toJson(data);
        BufferedWriter writer = null;
        try {
            file.delete();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonString);
        } catch (IOException e) {
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
