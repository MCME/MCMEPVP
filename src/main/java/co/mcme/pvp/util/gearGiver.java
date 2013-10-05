package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import static co.mcme.pvp.gametypes.ringBearerGame.ringBearers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author meggawatts <meggawatts@mcme.co>
 */
public class gearGiver {

    public static final Logger log = Logger.getLogger("Minecraft");

    public static void giveArmor(Player player, Color col) {
        PlayerInventory target = player.getInventory();
        if (ringBearers.containsKey(player)) {
            target.setHelmet(new ItemStack(89, 1));
        } else {
            target.setHelmet(itemUtils.setEnchantments(itemUtils.setColor(new ItemStack(298, 1, (short) 40000), col), "armor"));
        }
        target.setChestplate(itemUtils.setEnchantments(itemUtils.setColor(new ItemStack(299, 1, (short) 40000), col), "armor"));
        target.setLeggings(itemUtils.setEnchantments(itemUtils.setColor(new ItemStack(300, 1, (short) 40000), col), "armor"));
        target.setBoots(itemUtils.setEnchantments(itemUtils.setColor(new ItemStack(301, 1, (short) 40000), col), "armor"));
    }

    public static void giveWeapons(Player player, String team, String loadout) {
        PlayerInventory target = player.getInventory();
        ItemStack sword = itemUtils.nameItem(new ItemStack(267), player.getName() + "'s Sword", "none", ChatColor.AQUA);
        ItemStack bow = new ItemStack(261);
        ItemStack arrows = new ItemStack(262, 32);
        ItemStack food = new ItemStack(364);
        if (loadout.equals("warrior")) {
            target.setItem(0, sword);
            target.setItem(1, bow);
            target.setItem(2, arrows);
            target.setItem(3, food);
            if (MCMEPVP.horseMode) {
                ItemStack leash = magicItem(false, 6, 1);
                target.setItem(7, leash);
            }
            if (MCMEPVP.CurrentGame.allowCustomAttributes()) {
                ItemStack book = itemUtils.nameItem(new ItemStack(340, 1), "PVP Attributes", "Read me!", ChatColor.DARK_AQUA);
                target.setItem(8, book);
            }
        }
    }

    public static void giveExtras(Player player, String team, String set) {
        PlayerInventory target = player.getInventory();
        if (set.equals("boating")) {
            target.addItem(new ItemStack(333, 5));
        }
    }

    public static void rewardLoot(String string, Player p) {
        Configuration conf = Bukkit.getPluginManager().getPlugin("MCMEPVP").getConfig();
        if (string.equalsIgnoreCase("Sword of Damage")) {
            ItemStack loot = new ItemStack(Material.IRON_SWORD);
            loot.addEnchantment(Enchantment.DAMAGE_ALL, 2);
            loot.getItemMeta().setDisplayName(ChatColor.AQUA + "Sword of Damage");
            p.getInventory().addItem(loot);
        }
    }

    public static ItemStack magicItem(Boolean random, int item, int amount) {
        int i = 0;
        String itemName = "boop!";
        List<String> lore = new ArrayList<String>();
        if (random) {
            i = getRandom(0, 6);
        } else {
            if (item <= 6) {
                i = item;
            }
        }
        Material m = Material.AIR;
        if (i == 0) {
            m = Material.GOLD_NUGGET;
            itemName = ChatColor.GOLD + "Magic Ring";
            lore.add("Ring of Invisibility!");
        }
        if (i == 1) {
            m = Material.SUGAR;
            itemName = ChatColor.LIGHT_PURPLE + "Moon Sugar";
            lore.add("Speed boost!");
        }
        if (i == 2) {
            m = Material.GHAST_TEAR;
            itemName = ChatColor.GREEN + "Gandalf's Pipe";
            lore.add("Health & Hunger Regen!");
        }
        if (i == 3) {
            m = Material.NETHER_STAR;
            itemName = ChatColor.DARK_AQUA + "Galadriel's Phial";
            lore.add("A light for dark places!");
        }
        if (i == 4) {
            m = Material.RAW_FISH;
            itemName = ChatColor.BLUE + "SuperFish";
            lore.add("Instant Health!");
        }
        if (i == 5) {
            m = Material.COOKIE;
            itemName = ChatColor.YELLOW + "Elven Reactions";
            lore.add("Instant Health!");
        }
        if (i == 6) {
            m = Material.LEASH;
            itemName = ChatColor.RED + "Steed of Rohan";
            lore.add("Neigh neigh horse!");
        }
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(lore);
        it.setItemMeta(meta);
        it.setAmount(amount);
        return it;
    }

    public static int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }

    public static void giveRing(Player p, int i) {
        p.getInventory().setItem(4, new ItemStack(371, i));
    }

    public static void loadout(Player player, boolean giveArmor, boolean giveExtras, boolean giveWeapons, String weapons, Color col, String extras, String team) {
        if (giveArmor) {
            giveArmor(player, col);
        }
        if (giveExtras) {
            giveExtras(player, team, extras);
        }
        if (giveWeapons) {
            giveWeapons(player, team, weapons);
        }
    }
}