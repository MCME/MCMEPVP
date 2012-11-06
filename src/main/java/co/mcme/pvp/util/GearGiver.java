package co.mcme.pvp.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author meggawatts <meggawatts@mcme.co>
 */
public class GearGiver {

    public static void giveArmor(Player player, String team) {
        PlayerInventory target = player.getInventory();
        if (team.equals("red")) {
            target.setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0xff0000));
            target.setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0xff0000));
            target.setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0xff0000));
            target.setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0xff0000));
        }
        if (team.equals("blue")) {
            target.setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0x003cff));
            target.setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0x003cff));
            target.setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0x003cff));
            target.setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0x003cff));
        }
        if (team.equals("green")) {
            target.setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0x00ff36));
            target.setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0x00ff36));
            target.setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0x00ff36));
            target.setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0x00ff36));
        }
        if (team.equals("yellow")) {
            target.setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0xf6ff00));
            target.setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0xf6ff00));
            target.setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0xf6ff00));
            target.setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0xf6ff00));
        }
        if (team.equals("uncolored")) {
            target.setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0x000000));
            target.setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0x000000));
            target.setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0x000000));
            target.setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0x000000));
        }

    }

    public static void giveWeapons(Player player, String team, String loadout) {
        PlayerInventory target = player.getInventory();
        if (loadout.equals("swordbow")) {
            target.setItemInHand(new ItemStack(276));//Sword
            target.addItem(new ItemStack(261), new ItemStack(262, 32));//Bow + Arrows 
        }
    }
}
