package co.mcme.pvp.util;

import net.minecraft.server.NBTTagCompound;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author meggawatts <meggawatts@mcme.co>
 */
public class ItemUtils {

    public static ItemStack setColor(ItemStack item, int color) {
        CraftItemStack craftstack = null;
        net.minecraft.server.ItemStack itemStack = null;
        if (item instanceof CraftItemStack) {
            craftstack = (CraftItemStack) item;
            itemStack = craftstack.getHandle();
        } else if (item instanceof ItemStack) {
            craftstack = new CraftItemStack(item);
            itemStack = craftstack.getHandle();
        }
        NBTTagCompound tag = itemStack.tag;
        if (tag == null) {
            tag = new NBTTagCompound();
            tag.setCompound("display", new NBTTagCompound());
            itemStack.tag = tag;
        }

        tag = itemStack.tag.getCompound("display");
        tag.setInt("color", color);
        itemStack.tag.setCompound("display", tag);
        return craftstack;
    }
    public static ItemStack setEnchantments(ItemStack item, String ench){
        if (ench.equalsIgnoreCase("armor")){
           item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }      
        return item;
    }
}
