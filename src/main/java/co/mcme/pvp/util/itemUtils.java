package co.mcme.pvp.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 *
 * @author meggawatts <meggawatts@mcme.co>
 */
public class itemUtils {

    public static ItemStack setColor(ItemStack item, Color color) {
        LeatherArmorMeta meta;
        Material type = item.getType();
        if (type.equals(Material.LEATHER_BOOTS) || type.equals(Material.LEATHER_CHESTPLATE) || type.equals(Material.LEATHER_HELMET) || type.equals(Material.LEATHER_LEGGINGS)) {
            meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
            return item;
        } else {
            meta = null;
            return item;
        }

    }

    public static ItemStack nameItem(ItemStack item, String name, String lore, ChatColor color) {
        ItemMeta meta;
        meta = item.getItemMeta();
        meta.setDisplayName(color + name);
        if(!lore.equals("none")){
        	List<String> l = new ArrayList<String>();
        	l.add(lore);
        	meta.setLore(l);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setEnchantments(ItemStack item, String ench) {
        if (ench.equalsIgnoreCase("armor")) {
            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        return item;
    }
}
