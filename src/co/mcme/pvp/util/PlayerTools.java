package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.entity.Player;

/**
 *
 * @author meggawatts
 */
public class PlayerTools{

    public String getTeam(Player p) {
        String team = MCMEPVP.PlayerStatus.get(p.getName());
        return team;
    }

    public boolean isDead(Player p) {
        if (MCMEPVP.PlayerStatus.get(p.getName()).equals("spectator")) {
            return true;
        } else {
            return false;
        }
    }
}
