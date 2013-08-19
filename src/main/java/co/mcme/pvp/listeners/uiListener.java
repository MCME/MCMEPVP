package co.mcme.pvp.listeners;

import co.mcme.pvp.classes.classUI;
import co.mcme.pvp.util.util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class uiListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            if (event.getItem().hasItemMeta()) {
                if (event.getItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Pick your class")) {
                    if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        event.setCancelled(true);
                        event.getPlayer().openInventory(classUI.getInventory());
                    }
                }
            }

        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        util.debug(event.getEventName() + event.getAction().name());
        InventoryAction action = event.getAction();
        if (event.getInventory().getName().equals(ChatColor.AQUA + "Class Selection")) {
            if ((event.getSlotType() == InventoryType.SlotType.CONTAINER)) {
                if (event.getRawSlot() < 9) {
                    if (action.equals(InventoryAction.PICKUP_ALL) || action.equals(InventoryAction.PICKUP_HALF) || action.equals(InventoryAction.PICKUP_ONE) || action.equals(InventoryAction.PICKUP_SOME)) {
                        getGearForSlot(event.getRawSlot(), player);
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    public void getGearForSlot(int slot, Player p) {
        p.sendMessage("Getting gear for slot " + slot);
    }
}