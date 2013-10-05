package co.mcme.pvp.maps;

import java.util.HashMap;
import org.bukkit.util.Vector;

public class pvpMap {

    private String name;
    private pvpMapMeta meta;
    private HashMap<String, Spawn> spawns = new HashMap<String, Spawn>();
    private HashMap<Integer, Vector> flags = new HashMap<Integer, Vector>();

    public pvpMap(pvpMapMeta m) {
        name = m.getName();
        meta = m;
        flags = meta.getFlags();
        spawns = meta.getSpawns();
    }

    public String getName() {
        return name;
    }

    public int getFlagCount() {
        return flags.size();
    }

    public pvpMapMeta getMapMeta() {
        return meta;
    }
}
