package co.mcme.pvp.maps;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class pvpMap {

    private String name;
    private pvpMapMeta meta;
    private HashMap<String, Location> spawns = new HashMap();
    private HashMap<Integer, Vector> flags = new HashMap();

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
