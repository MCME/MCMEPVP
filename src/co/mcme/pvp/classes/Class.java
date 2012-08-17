package co.mcme.pvp.classes;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author meggawatts
 */
public class Class {
    
    public static HashMap playerclasses = new HashMap<Player, String>();
    public static Inventory healer;
    public static Inventory warrior;
    public static Inventory archer;
    public static Inventory thief;
    
    public void getLoadout(Player player){
        if(playerclasses.get(player).equals("healer")){
            
        }
    }
}
