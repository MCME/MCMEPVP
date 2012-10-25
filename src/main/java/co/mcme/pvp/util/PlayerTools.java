package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.entity.Player;

/**
 *
 * @author meggawatts
 */
public class PlayerTools{

    public String getTeam(Player player) {
        String team = MCMEPVP.PlayerStatus.get(player.getName());
        return team;
    }
    
    public void giveGear(Player player) {
        String team = MCMEPVP.getPlayerStatus(player);
        if (team.equals("blue")){
            
        }
    }
    public boolean getDead(Player player) {
        if (MCMEPVP.PlayerStatus.get(player.getName()).equals("spectator")) {
            return true;
        } else {
            return false;
        }
    }
}
