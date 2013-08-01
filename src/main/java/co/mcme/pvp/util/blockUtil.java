package co.mcme.pvp.util;

import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

public class blockUtil {

    public boolean isContainer(Block b) {
        boolean result = false;
        if (b.getState() instanceof InventoryHolder) {
            result = true;
        }
        return result;
    }

    public boolean isFlag(Block b) {
        boolean result = false;
        return result;
    }
}
