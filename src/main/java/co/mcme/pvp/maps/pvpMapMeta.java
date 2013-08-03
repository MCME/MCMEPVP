package co.mcme.pvp.maps;

import co.mcme.pvp.MCMEPVP;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class pvpMapMeta {

    private String name;
    private HashMap<String, Location> spawns = new HashMap();
    private HashMap<Integer, Vector> flags = new HashMap();

    public pvpMapMeta(JsonArray dat) {
        JsonObject mapObj = dat.get(0).getAsJsonObject();
        name = mapObj.get("name").getAsString();
        // Load flags
        JsonObject flags_ = mapObj.get("flags").getAsJsonArray().get(0).getAsJsonObject();
        for (Map.Entry<String, JsonElement> flag : flags_.entrySet()) {
            JsonObject coords = flag.getValue().getAsJsonObject();
            flags.put(Integer.valueOf(flag.getKey()), new Vector(coords.get("x").getAsInt(), coords.get("y").getAsInt(), coords.get("z").getAsInt()));
        }
        // Load spawns
        JsonObject spawns_ = mapObj.get("spawns").getAsJsonArray().get(0).getAsJsonObject();
        for (Map.Entry<String, JsonElement> spawn : spawns_.entrySet()) {
            JsonObject spawndat = spawn.getValue().getAsJsonObject();
            String spawnname = spawn.getKey();
            Location spawnloc = new Location(MCMEPVP.PVPWorld, spawndat.get("x").getAsDouble(),
                    spawndat.get("y").getAsDouble(), spawndat.get("z").getAsDouble(),
                    spawndat.get("yaw").getAsFloat(), spawndat.get("pitch").getAsFloat());
            spawns.put(spawnname, spawnloc);
        }
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Location> getSpawns() {
        return spawns;
    }

    public Location getSpawn(String team) {
        return spawns.get(team);
    }

    public HashMap<Integer, Vector> getFlags() {
        return flags;
    }

    public Vector getFlagVector(int flagid) {
        return flags.get(flagid);
    }
}
