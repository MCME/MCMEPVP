package co.mcme.pvp.stats;

import java.util.ArrayList;
import org.bukkit.entity.Player;

public class PlayerStat {

    private Player p;
    private ArrayList<PvpDeath> kills = new ArrayList();
    private ArrayList<PvpDeath> deaths = new ArrayList();

    public PlayerStat(Player player) {
        p = player;
    }

    public Player getPlayer() {
        return p;
    }

    public int getKillCount() {
        return kills.size();
    }

    public int getDeathCount() {
        return deaths.size();
    }

    public void addDeath(PvpDeath death) {
        deaths.add(death);
    }

    public void addKill(PvpDeath kill) {
        kills.add(kill);
    }

    public ArrayList<PvpDeath> getDeaths() {
        return deaths;
    }

    public ArrayList<PvpDeath> getKills() {
        return kills;
    }
}
