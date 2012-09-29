package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;

public class inventoryListener implements Listener {

    public inventoryListener(MCMEPVP instance) {
    }

    //TODO prevent player from taking off head
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (event.getSlotType() == SlotType.ARMOR) {
            if (player.getInventory().getHelmet().getType().equals(Material.WOOL)) {
                //TODO send message to player on armor remove attempt
                event.setCancelled(true);
            }
        }
    }
}
