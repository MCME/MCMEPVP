package co.mcme.pvp.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author meggawatts <meggawatts@mcme.co>
 */
public class GearGiver {

    public static void giveArmor(Player player, String team) {
        if (team.equals("red")) {
            player.getInventory().setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0xff0000));
            player.getInventory().setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0xff0000));
            player.getInventory().setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0xff0000));
            player.getInventory().setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0xff0000));
        }
        if (team.equals("blue")) {
            player.getInventory().setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0x003cff));
            player.getInventory().setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0x003cff));
            player.getInventory().setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0x003cff));
            player.getInventory().setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0x003cff));
        }
        if (team.equals("green")) {
            player.getInventory().setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0x00ff36));
            player.getInventory().setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0x00ff36));
            player.getInventory().setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0x00ff36));
            player.getInventory().setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0x00ff36));
        }
        if (team.equals("yellow")) {
            player.getInventory().setHelmet(ColorArmor.setColor(new ItemStack(298, 1, (short) 40000), 0xf6ff00));
            player.getInventory().setChestplate(ColorArmor.setColor(new ItemStack(299, 1, (short) 40000), 0xf6ff00));
            player.getInventory().setLeggings(ColorArmor.setColor(new ItemStack(300, 1, (short) 40000), 0xf6ff00));
            player.getInventory().setBoots(ColorArmor.setColor(new ItemStack(301, 1, (short) 40000), 0xf6ff00));
        }

    }

    public static void giveWeapons(Player player, String team) {
        player.getInventory().setItemInHand(new ItemStack(276));//Sword
        player.getInventory().addItem(new ItemStack(261), new ItemStack(262, 32));//Bow + Arrows
    }
}
