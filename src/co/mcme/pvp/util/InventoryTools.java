package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author meggawatts
 */
public class InventoryTools implements Listener{
      
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (event.getSlotType() == SlotType.ARMOR) {
            if (player.getInventory().getHelmet().getType().equals(Material.WOOL)) {
                //TODO send message to player on armor remove attempt
                event.setResult(Result.DENY);
                event.setCancelled(true);
                resetHelmet((Player) player);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        resetHelmet(player);
    }

    public void resetHelmet(Player player) {
        String pname = player.getName();
        String team = MCMEPVP.PlayerStatus.get(pname);
        if (team.equalsIgnoreCase("red")){
            player.getInventory().setHelmet(new ItemStack(35, 1, (short) 0, (byte) 14));
        }
        if (team.equalsIgnoreCase("blue")){
            player.getInventory().setHelmet(new ItemStack(35, 1, (short) 0, (byte) 11));
        }
    }
    
}
