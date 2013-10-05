package co.mcme.pvp.classes;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

public class classUI{

    static Inventory gui = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Class Selection");

    public void setClasses(ArrayList<pvpClass> classes) {
        double numClasses = classes.size();
        int numRows = (int) Math.round(Math.ceil(numClasses / 9));
        gui = null;
        gui = Bukkit.createInventory(null, numRows, ChatColor.AQUA + "Class Selection");
        int slotIndex = 0;
        for (pvpClass _class : classes) {
            gui.setItem(slotIndex, _class.getItemForUI());
        }
    }

    public static Inventory getInventory() {
        return gui;
    }
}
