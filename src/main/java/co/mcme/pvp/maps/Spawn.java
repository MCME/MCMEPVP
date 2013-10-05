package co.mcme.pvp.maps;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.teamUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Spawn {

    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private String team;

    public Spawn(String teamFor, double sx, double sy, double sz, float spitch, float syaw) {
        team = teamFor;
        x = sx;
        y = sy;
        z = sz;
        pitch = spitch;
        yaw = syaw;
    }

    public String getSpawnOwner() {
        return team;
    }
    
    public void setSpawnOwner(String newteam) {
        team = newteam;
    }

    public boolean canSpawnHere(Player p, boolean isRandomSpawn) {
        String playerTeam = teamUtil.getPlayerTeam(p);
        return (playerTeam.equalsIgnoreCase(team) || isRandomSpawn) ? true : false;
    }

    public Location toLocation() {
        return new Location(MCMEPVP.PVPWorld, x, y, z, yaw, pitch);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }
}
