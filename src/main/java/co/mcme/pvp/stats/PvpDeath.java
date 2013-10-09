package co.mcme.pvp.stats;

import org.bukkit.Material;

public class PvpDeath {

    private String v;
    private String k;
    private String m;
    private Material w;
    private String g;

    public PvpDeath(String victim, String killer, String map, Material weapon, String gameType) {
        v = victim;
        k = killer;
        m = map;
        w = weapon;
        g = gameType;
    }

    public String getKiller() {
        return k;
    }

    public String getVictim() {
        return v;
    }

    public String getMap() {
        return m;
    }

    public String getWeapon() {
        if (w.equals(Material.AIR)) {
            return "FIST";
        } else {
            return w.toString();
        }
    }

    public String getGameType() {
        return g;
    }
}
