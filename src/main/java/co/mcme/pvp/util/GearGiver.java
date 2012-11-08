package co.mcme.pvp.util;

import java.util.logging.Logger;
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
            target.setHelmet(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0xff0000));
            target.setChestplate(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0xff0000));
            target.setLeggings(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0xff0000));
            target.setBoots(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0xff0000));
        }
        if (team.equals("blue")) {
            target.setHelmet(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0x003cff));
            target.setChestplate(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0x003cff));
            target.setLeggings(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0x003cff));
            target.setBoots(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0x003cff));
        }
        if (team.equals("green")) {
            target.setHelmet(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0x00ff36));
            target.setChestplate(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0x00ff36));
            target.setLeggings(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0x00ff36));
            target.setBoots(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0x00ff36));
        }
        if (team.equals("yellow")) {
            target.setHelmet(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0xf6ff00));
            target.setChestplate(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0xf6ff00));
            target.setLeggings(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0xf6ff00));
            target.setBoots(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0xf6ff00));
        }
        if (team.equals("uncolored")) {
            target.setHelmet(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0x000000));
            target.setChestplate(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0x000000));
            target.setLeggings(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0x000000));
            target.setBoots(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0x000000));
        } else {
            target.setHelmet(ItemUtils.setColor(new ItemStack(298, 1, (short) 40000), 0xFFFFFF));
            target.setChestplate(ItemUtils.setColor(new ItemStack(299, 1, (short) 40000), 0xFFFFFF));
            target.setLeggings(ItemUtils.setColor(new ItemStack(300, 1, (short) 40000), 0xFFFFFF));
            target.setBoots(ItemUtils.setColor(new ItemStack(301, 1, (short) 40000), 0xFFFFFF));
            log.warning("Color " + team + " not found, white given instead!");
        }
    }

    public static void giveWeapons(Player player, String team, String loadout) {
        PlayerInventory target = player.getInventory();
        if (loadout.equals("warrior")) {
            target.setItemInHand(new ItemStack(276));//Sword
            target.addItem(new ItemStack(261), new ItemStack(262, 32));//Bow + Arrows 
        }
        if (loadout.equals("healer")) {
            target.setItemInHand(new ItemStack(272));
            target.addItem(ItemUtils.setCustomPotion(new ItemStack(373, 3, (short) 16417), 10, 0, 200));
        }
    }
}
/*
 * "Speed" 1
 * "Slowness" 2
 * "Haste" 3
 * "Mining Fatigue" 4
 * "Strength" 5
 * "Instant Health" 6
 * "Instant Damage" 7
 * "Jump Boost" 8
 * "Nausea" 9
 * "Regeneration" 10
 * "Resistance": 11
 * "Fire Resistance" 12
 * "Water Breathing" 13
 * "Invisibility" 14
 * "Blindness" 15
 * "Night Vision" 16
 * "Hunger" 17
 * "Weakness" 18
 * "Poison" 19
 * "Wither" 20
 */