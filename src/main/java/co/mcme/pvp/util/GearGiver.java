package co.mcme.pvp.util;

import java.util.logging.Logger;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author meggawatts <meggawatts@mcme.co>
 */
public class GearGiver {
    
    public static final Logger log = Logger.getLogger("Minecraft");
    public static void giveArmor(Player player, String team) {
        PlayerInventory target = player.getInventory();
        if (team.equals("red")) {
            target.setHelmet(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0xff0000), "armor"));
            target.setChestplate(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0xff0000), "armor"));
            target.setLeggings(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0xff0000), "armor"));
            target.setBoots(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0xff0000), "armor"));
        }
        if (team.equals("blue")) {
            target.setHelmet(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0x003cff), "armor"));
            target.setChestplate(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0x003cff), "armor"));
            target.setLeggings(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0x003cff), "armor"));
            target.setBoots(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0x003cff), "armor"));
        }
        if (team.equals("green")) {
            target.setHelmet(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0x00ff36), "armor"));
            target.setChestplate(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0x00ff36), "armor"));
            target.setLeggings(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0x00ff36), "armor"));
            target.setBoots(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0x00ff36), "armor"));
        }
        if (team.equals("yellow")) {
            target.setHelmet(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0xf6ff00), "armor"));
            target.setChestplate(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0xf6ff00), "armor"));
            target.setLeggings(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0xf6ff00), "armor"));
            target.setBoots(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0xf6ff00), "armor"));
        }
        if (team.equals("uncolored")) {
            target.setHelmet(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0x000000), "armor"));
            target.setChestplate(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0x000000), "armor"));
            target.setLeggings(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0x000000), "armor"));
            target.setBoots(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0x000000), "armor"));
        } else if (!(team.equalsIgnoreCase("red") || team.equalsIgnoreCase("blue") 
                ||team.equalsIgnoreCase("green") || team.equalsIgnoreCase("yellow") 
                || team.equalsIgnoreCase("uncolored"))) {
            target.setHelmet(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0xFFFFFF), "armor"));
            target.setChestplate(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0xFFFFFF), "armor"));
            target.setLeggings(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0xFFFFFF), "armor"));
            target.setBoots(ItemUtils.setEnchantments(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0xFFFFFF), "armor"));
            log.warning("Color " + team + " not found, white given instead!");
        }
    }

    public static void giveWeapons(Player player, String team, String loadout) {
        PlayerInventory target = player.getInventory();
        if (loadout.equals("warrior")) {
            target.setItemInHand(new ItemStack(267));//Sword
            target.addItem(new ItemStack(261), new ItemStack(262, 32));//Bow + Arrows 
        }
    }
    public static void giveExtras(Player player, String team, String set) {
        PlayerInventory target = player.getInventory();
        if (set.equals("boating")){
            target.addItem(new ItemStack(333, 5));
        }
    }
}