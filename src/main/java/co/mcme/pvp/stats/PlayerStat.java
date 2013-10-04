package co.mcme.pvp.stats;

import org.bukkit.entity.Player;

public class PlayerStat {

    private int k;
    private int d;
    private Player p;

    public PlayerStat(Player player) {
        p = player;
    }

    public Player getPlayer() {
        return p;
    }

    public int incrementKills(int amount) {
        return k += amount;
    }

    public int getKills() {
        return k;
    }

    public int incrementDeaths(int amount) {
        return d += amount;
    }

    public int getDeaths() {
        return d;
    }
}
