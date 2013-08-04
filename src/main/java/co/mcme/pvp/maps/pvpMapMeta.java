package co.mcme.pvp.maps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.util.Vector;

public class pvpMapMeta {

    private String name;
    private File file;
    private HashMap<String, Spawn> spawns = new HashMap();
    private HashMap<Integer, Vector> flags = new HashMap();
    JsonArray data;

    public pvpMapMeta(JsonArray dat, File metafile) {
        data = dat;
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
            Spawn spawnloc = new Spawn(spawnname, spawndat.get("x").getAsDouble(),
                    spawndat.get("y").getAsDouble(), spawndat.get("z").getAsDouble(),
                    spawndat.get("yaw").getAsFloat(), spawndat.get("pitch").getAsFloat());
            spawns.put(spawnname, spawnloc);
        }
        file = metafile;
    }

    public String getName() {
        return name;
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
